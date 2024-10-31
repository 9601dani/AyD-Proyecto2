package com.bugtrackers.ms_user.repositories;

import com.bugtrackers.ms_user.models.Employee;
import com.bugtrackers.ms_user.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByUser(User user);
}
