package com.bugtrackers.ms_user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_user.models.Appointment;
import com.bugtrackers.ms_user.models.Bill;
import java.util.Optional;


@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    
    Optional<Bill> findByAppointment(Appointment appointment);
}
