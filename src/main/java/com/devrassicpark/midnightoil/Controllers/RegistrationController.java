package com.devrassicpark.midnightoil.Controllers;

import com.devrassicpark.midnightoil.DTO.EmployeeRegistrationDto;
import com.devrassicpark.midnightoil.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/employee")
    public ResponseEntity<String> registerEmployee(@RequestBody EmployeeRegistrationDto registrationDto) {
        try {
            userService.registerEmployee(registrationDto);
            return ResponseEntity.ok("Employee registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
