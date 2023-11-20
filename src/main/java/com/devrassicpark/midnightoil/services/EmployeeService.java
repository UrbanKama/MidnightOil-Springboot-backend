package com.devrassicpark.midnightoil.services;

import com.devrassicpark.midnightoil.Exception.domains.EmailExistsException;
import com.devrassicpark.midnightoil.Exception.domains.EmailNotFoundException;
import com.devrassicpark.midnightoil.Exception.domains.EmployeeNotFoundException;
import com.devrassicpark.midnightoil.Exception.domains.UsernameExistsException;
import com.devrassicpark.midnightoil.models.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {

    Employee register(String firstName, String lastName, String username, String email, String password) throws UsernameExistsException, EmailExistsException, EmployeeNotFoundException;

    List<Employee> getEmployees();

    Employee findEmployeeByUsername(String username);

    Employee findEmployeeByEmail(String email);

    Employee addNewEmployee(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, EmployeeNotFoundException, IOException;

    Employee updateEmployee(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, EmployeeNotFoundException, IOException;

    void deleteEmployee(long id);

    void resetPassword(String email) throws EmailNotFoundException;

    Employee updateProfileImage(String username, MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, EmployeeNotFoundException, IOException;
}
