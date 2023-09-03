package com.devrassicpark.midnightoil.Controllers;

import com.devrassicpark.midnightoil.DTO.Employee;
import com.devrassicpark.midnightoil.DTO.EmployeeRegistrationDto;
import com.devrassicpark.midnightoil.Service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService){
        this.registrationService = registrationService;
    }

    @PostMapping("/employee")
    public ResponseEntity<String> registerEmployee(@RequestBody EmployeeRegistrationDto registrationDto) {
        try {
            registrationService.registerEmployee(registrationDto);
            return ResponseEntity.ok("Employee registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
