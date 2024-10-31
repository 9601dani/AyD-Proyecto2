package com.bugtrackers.ms_user.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_user.models.Appointment;
import com.bugtrackers.ms_user.models.Employee;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    


    List<Appointment> findByResourceIdOrEmployeeId(Integer resource, Integer employee);

    List<Appointment> findByUserId(Integer userId);

    @Query(value= """
           SELECT
           b.name AS client_name_on_bill,
           b.nit AS client_nit,
           b.address AS client_address,
           b.price AS total_amount,
           b.advancement AS advancement,
           b.created_at AS bill_date
           FROM bill b
           LEFT JOIN appointment a ON b.FK_Appointment = a.id;
            """, nativeQuery = true)
    List<Object[]> getBill();

    List<Appointment> findByEmployeeAndStateOrState(Employee employee, String state, String state2);

}
