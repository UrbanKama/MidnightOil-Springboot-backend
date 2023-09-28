package com.devrassicpark.midnightoil.repositories;

import com.devrassicpark.midnightoil.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    //
    Employee findEmployeeByUsername(String username);

    Employee findEmployeeByEmail(String email);
}
