package com.bugtrackers.ms_user.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_user.models.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    

    List<Appointment> findByResourceIdOrEmployeeId(Integer resource, Integer employee);
}
