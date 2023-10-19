package com.devrassicpark.midnightoil.Listeners;

import com.devrassicpark.midnightoil.models.Employee;
import com.devrassicpark.midnightoil.models.EmployeePrincipal;
import com.devrassicpark.midnightoil.services.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {
    private LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof EmployeePrincipal){
            Employee employee = (Employee) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptedCache(employee.getUsername());
        }
    }
}
