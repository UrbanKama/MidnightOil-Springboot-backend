package com.devrassicpark.midnightoil.services;

import com.devrassicpark.midnightoil.Exception.domains.EmailExistsException;
import com.devrassicpark.midnightoil.Exception.domains.EmployeeNotFoundException;
import com.devrassicpark.midnightoil.Exception.domains.UsernameExistsException;
import com.devrassicpark.midnightoil.models.Employee;

import java.util.List;

public interface EmployeeService {

    Employee register(String firstName, String lastName, String username, String email) throws UsernameExistsException, EmailExistsException, EmployeeNotFoundException;

    List<Employee> getEmployees();

    Employee findEmployeeByUsername(String username);

    Employee findEmployeeByEmail(String email);
}
