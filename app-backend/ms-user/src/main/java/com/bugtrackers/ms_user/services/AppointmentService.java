package com.bugtrackers.ms_user.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bugtrackers.ms_user.dto.response.BillReportResponse;
import org.springframework.stereotype.Service;

import com.bugtrackers.ms_user.clients.EmailRestClient;
import com.bugtrackers.ms_user.dto.request.AppointmentRequest;
import com.bugtrackers.ms_user.dto.request.EmailRequest;
import com.bugtrackers.ms_user.dto.response.AppointmentResponse;
import com.bugtrackers.ms_user.exceptions.CompanySettingNotFoundException;
import com.bugtrackers.ms_user.exceptions.EmployeeNotFoundException;
import com.bugtrackers.ms_user.exceptions.ResourceNotFoundException;
import com.bugtrackers.ms_user.exceptions.UserNotFoundException;
import com.bugtrackers.ms_user.models.Appointment;
import com.bugtrackers.ms_user.models.AppointmentHasService;
import com.bugtrackers.ms_user.models.Bill;
import com.bugtrackers.ms_user.models.CompanySetting;
import com.bugtrackers.ms_user.models.Employee;
import com.bugtrackers.ms_user.models.Resource;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.repositories.AppointmentRepository;
import com.bugtrackers.ms_user.repositories.AppointmentServiceRepository;
import com.bugtrackers.ms_user.repositories.BillRepository;
import com.bugtrackers.ms_user.repositories.CompanySettingRepository;
import com.bugtrackers.ms_user.repositories.EmployeeRepository;
import com.bugtrackers.ms_user.repositories.ResourceRepository;
import com.bugtrackers.ms_user.repositories.ServiceRepository;
import com.bugtrackers.ms_user.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final EmployeeRepository employeeRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final CompanySettingRepository companySettingRepository;
    private final BillRepository billRepository;
    private final EmailRestClient emailClient;

    public AppointmentResponse save(AppointmentRequest appointmentRequest) {

        Appointment appointment = new Appointment();

        Optional<User> uOptional = this.userRepository.findById(appointmentRequest.userId());

        if (uOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        User user = uOptional.get();
        appointment.setUser(user);

        if (appointmentRequest.resourceId() != null) {
            Optional<Resource> rOptional = this.resourceRepository.findById(appointmentRequest.resourceId());

            if (rOptional.isEmpty()) {
                throw new ResourceNotFoundException("Recurso no encontrado.");
            }

            appointment.setResource(rOptional.get());
        }

        if (appointmentRequest.employeeId() != null) {
            Optional<Employee> eOptional = this.employeeRepository.findById(appointmentRequest.employeeId());

            if (eOptional.isEmpty()) {
                throw new EmployeeNotFoundException("Empleado no encontrado.");
            }

            appointment.setEmployee(eOptional.get());
        }

        appointment.setStartTime(LocalDateTime.parse(appointmentRequest.startTime()));
        appointment.setEndTime(LocalDateTime.parse(appointmentRequest.endTime()));

        BigDecimal total = BigDecimal.ZERO;

        List<AppointmentHasService> appoinmentServices = new ArrayList<>();
        List<com.bugtrackers.ms_user.models.Service> services = this.serviceRepository
                .findAllByIdIn(appointmentRequest.servicesId());

        total = services.stream().map(com.bugtrackers.ms_user.models.Service::getPrice)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        appointment.setTotal(total);

        Appointment saved = this.appointmentRepository.save(appointment);

        services.forEach(service -> {
            AppointmentHasService aHasService = new AppointmentHasService();
            aHasService.setAppointment(saved);
            aHasService.setService(service);
            aHasService.setPrice(service.getPrice());
            aHasService.setTimeAprox(service.getTimeAprox());
            appoinmentServices.add(aHasService);

        });

        List<AppointmentHasService> saveAll = this.appointmentServiceRepository.saveAll(appoinmentServices);
        saved.setAppointmentHasServices(saveAll);

        // Bill logic
        createBill(user, saved);

        return new AppointmentResponse(saved);
    }

    public List<AppointmentResponse> findByResourceOrEmployee(Integer resourceId, Integer employeeId) {
        List<Appointment> appointments = this.appointmentRepository.findByResourceIdOrEmployeeId(resourceId,
                employeeId);
        return appointments.stream().map(AppointmentResponse::new).toList();
    }

    private void createBill(User user, Appointment appointment) {

        Optional<CompanySetting> taxOptional = this.companySettingRepository.findByKeyName("tax");
        Optional<CompanySetting> feeOptional = this.companySettingRepository.findByKeyName("fee");

        if (taxOptional.isEmpty()) {
            throw new CompanySettingNotFoundException("No se encontró el porcentaje de impuesto.");
        }

        if (feeOptional.isEmpty()) {
            throw new CompanySettingNotFoundException("No se encontró el porcentaje adelanto.");
        }

        CompanySetting taxValue = taxOptional.get();
        CompanySetting feeValue = feeOptional.get();
        Integer taxPorcentage = Integer.valueOf(taxValue.getKeyValue());
        Integer feePorcentage = Integer.valueOf(feeValue.getKeyValue());
        BigDecimal price = appointment.getTotal();

        BigDecimal tax = price.multiply(BigDecimal.valueOf(taxPorcentage).divide(BigDecimal.valueOf(100)));
        BigDecimal fee = price.multiply(BigDecimal.valueOf(feePorcentage).divide(BigDecimal.valueOf(100)));

        Bill bill = new Bill();
        bill.setAppointment(appointment);
        bill.setNit(user.getUserInformation() == null ? "CF" : user.getUserInformation().getNit());
        bill.setName(user.getUsername());
        bill.setAddress("CIUDAD");
        String services = appointment.getAppointmentHasServices().stream()
                .map(AppointmentHasService::getService)
                .map(com.bugtrackers.ms_user.models.Service::getName)
                .reduce("", (a, b) -> a + ", " + b);
        bill.setDescription("Por los siguientes servicios: " + services);
        bill.setPrice(appointment.getTotal());
        bill.setAdvancement(fee);
        bill.setTax(tax);

        Bill billSaved = this.billRepository.save(bill);

        Optional<CompanySetting> templateOptional = this.companySettingRepository.findByKeyName("gmail_bill");
        Optional<CompanySetting> companyNameOptional = this.companySettingRepository.findByKeyName("company_name");
        Optional<CompanySetting> logoOptional = this.companySettingRepository.findByKeyName("company_img");
        Optional<CompanySetting> currencyOptional = this.companySettingRepository.findByKeyName("currency");
        Optional<CompanySetting> directionOptional = this.companySettingRepository.findByKeyName("direction");
        Optional<CompanySetting> startOptional = this.companySettingRepository.findByKeyName("start_hour");
        Optional<CompanySetting> endOptional = this.companySettingRepository.findByKeyName("end_hour");
        Optional<CompanySetting> phoneOptional = this.companySettingRepository.findByKeyName("phone_number");

        if (templateOptional.isEmpty()) {
            throw new CompanySettingNotFoundException("No se encontró una configuración.");
        }

        if (companyNameOptional.isEmpty()) {
            throw new CompanySettingNotFoundException("No se encontró una configuración.");
        }

        if (logoOptional.isEmpty()) {
            throw new CompanySettingNotFoundException("No se encontró una configuración.");
        }

        if (currencyOptional.isEmpty()) {
            throw new CompanySettingNotFoundException("No se encontró una configuración.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        CompanySetting billTemplate = templateOptional.get();
        CompanySetting currency = currencyOptional.get();
        String template = billTemplate.getKeyValue();
        template = template.replace("COMPANY_LOGO", logoOptional.get().getKeyValue());
        template = template.replace("COMPANY_NAME", companyNameOptional.get().getKeyValue());
        template = template.replace("DIRECTION", directionOptional.get().getKeyValue());
        template = template.replace("PHONE_NUMBER", phoneOptional.get().getKeyValue());
        template = template.replace("START_HOUR", startOptional.get().getKeyValue());
        template = template.replace("END_HOUR", endOptional.get().getKeyValue());
        template = template.replace("USERNAME", billSaved.getName());
        template = template.replace("USER_NIT", billSaved.getNit());
        template = template.replace("INVOICE_DATE", billSaved.getCreatedAt().format(formatter));
        template = template.replace("INVOICE_NUMBER", billSaved.getId().toString());
        template = template.replace("SUBTOTAL", currency.getKeyValue() + " "
                + billSaved.getPrice().subtract(tax).setScale(2, RoundingMode.HALF_UP).toString());
        template = template.replace("TOTAL",
                currency.getKeyValue() + " " + billSaved.getPrice().setScale(2, RoundingMode.HALF_UP).toString());
        template = template.replace("TAX",
                currency.getKeyValue() + " " + tax.setScale(2, RoundingMode.HALF_UP).toString());

        String rows = billSaved.getAppointment().getAppointmentHasServices().stream()
                .map(AppointmentHasService::getService)
                .map(service -> {
                    return "<tr>" +
                            "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + service.getId() + "</td>" +
                            "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + service.getName() + "</td>" +
                            "<td style=\"padding: 10px; border: 1px solid #ddd;\"> 1 </td>" +
                            "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + service.getTimeAprox()
                            + " Minutos</td>" +
                            "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + currency.getKeyValue() + " "
                            + service.getPrice().setScale(2, RoundingMode.HALF_UP).toString() + "</td>" +
                            "</tr>";
                })
                .reduce("", (a, b) -> a + b);

        template = template.replace("TABLE_ROWS", rows);

        EmailRequest request = new EmailRequest(user.getEmail(), "Factura", template, true);
        this.emailClient.sendEmail(request);
    }

    public List<AppointmentResponse> findAll() {
        List<Appointment> appointments = this.appointmentRepository.findAll();
        return appointments.stream().map(AppointmentResponse::new).toList();
    }

    public List<BillReportResponse> getBill() {
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Object[]> bills = this.appointmentRepository.getBill();
        return bills.stream().map(bill -> new BillReportResponse(
                (String) bill[0],
                (String) bill[1],
                (String) bill[2],
                ((BigDecimal) bill[3]).doubleValue(),
                ((BigDecimal) bill[4]).doubleValue(),
                ((Timestamp) bill[5]).toLocalDateTime())).toList();
    }

    public String updateAppointmentState(Integer id, String state) {

        Optional<Appointment> aOptional = this.appointmentRepository.findById(id);

        if (aOptional.isEmpty()) {
            throw new UserNotFoundException("No se encontró la cita.");
        }

        Appointment appointment = aOptional.get();
        appointment.setState(state);
        this.appointmentRepository.save(appointment);

        Optional<CompanySetting> templateOptional = this.companySettingRepository.findByKeyName("email_notification");
        Optional<CompanySetting> companyNameOptional = this.companySettingRepository.findByKeyName("company_name");
        Optional<CompanySetting> logoOptional = this.companySettingRepository.findByKeyName("company_img");
        Optional<CompanySetting> phoneOptional = this.companySettingRepository.findByKeyName("phone_number");

        if (templateOptional.isEmpty()) {
            throw new CompanySettingNotFoundException("No se encontró una configuración.");
        }

        if (companyNameOptional.isEmpty()) {
            throw new CompanySettingNotFoundException("No se encontró una configuración.");
        }

        if (logoOptional.isEmpty()) {
            throw new CompanySettingNotFoundException("No se encontró una configuración.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        CompanySetting billTemplate = templateOptional.get();
        String template = billTemplate.getKeyValue();
        template = template.replace("COMPANY_LOGO", logoOptional.get().getKeyValue());
        template = template.replace("COMPANY_NAME", companyNameOptional.get().getKeyValue());
        template = template.replace("PHONE_NUMBER", phoneOptional.get().getKeyValue());
        template = template.replace("USERNAME", appointment.getUser().getUsername());
        template = template.replace("APPOINTMENT_NO", appointment.getId().toString());
        template = template.replace("APPOINTMENT_DATE",
                appointment.getStartTime().format(formatter) + " - " + appointment.getEndTime().format(formatter));
        template = template.replace("STATE", state);

        String rows = appointment.getAppointmentHasServices().stream()
                .map(AppointmentHasService::getService)
                .map(service -> {
                    return "<tr>" +
                            "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + service.getId() + "</td>" +
                            "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + service.getName() + "</td>" +
                            "</tr>";
                })
                .reduce("", (a, b) -> a + b);

        template = template.replace("TABLE_ROWS", rows);
        EmailRequest request = new EmailRequest(appointment.getUser().getEmail(), "Notificación de actualización.",
                template, false);
        this.emailClient.sendEmail(request);
        return "Cita actualizada con éxito!";
    }

    public List<AppointmentResponse> findAppointmentsByEmployee(Integer userId) {
        Optional<User> uOptional = this.userRepository.findById(userId);

        if (uOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        Optional<Employee> eOptional = this.employeeRepository.findByUser(uOptional.get());

        if (eOptional.isEmpty()) {
            throw new EmployeeNotFoundException("Empleado no encontrado.");
        }

        List<Appointment> appointments = this.appointmentRepository.findByEmployeeAndStateOrState(eOptional.get(),
                "CONFIRMADA", "FINALIZADA");
        return appointments.stream()
                .map(AppointmentResponse::new).toList();
    }

    public BillReportResponse findBillByAppointmentId(Integer id) {
        Optional<Appointment> aOptional = this.appointmentRepository.findById(id);

        if (aOptional.isEmpty()) {
            throw new UserNotFoundException("Cita no encontrada.");
        }

        Optional<Bill> bOptional = this.billRepository.findByAppointment(aOptional.get());
        Bill bill = bOptional.get();
        return new BillReportResponse(bill.getName(), bill.getNit(), bill.getAddress(), bill.getPrice().doubleValue(),
                bill.getAdvancement().doubleValue(), bill.getCreatedAt());

    }

}
