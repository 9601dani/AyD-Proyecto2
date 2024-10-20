package com.bugtrackers.ms_user.repositories;

import com.bugtrackers.ms_user.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}
