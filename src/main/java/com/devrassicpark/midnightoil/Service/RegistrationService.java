package com.devrassicpark.midnightoil.Service;

import com.devrassicpark.midnightoil.DTO.Employee;
import com.devrassicpark.midnightoil.DTO.EmployeeRegistrationDto;
import com.devrassicpark.midnightoil.Repo.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerEmployee(EmployeeRegistrationDto registrationDto){
        // check db if the email is already registered
        if(employeeRepository.findByEmail(registrationDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already exists.");
        }

        // create a new employee and populate fields
        Employee employee = new Employee();
        employee.setEmail(registrationDto.getEmail());
        employee.setFirstName(registrationDto.getFirstName());
        employee.setLastName(registrationDto.getLastName());

        //Hash employee password with BCrypt
        String hashedPassword = passwordEncoder.encode(registrationDto.getPassword());
        employee.setPassword(hashedPassword);

        //Save new employee to the database
        employeeRepository.save(employee);
    }
}
