package com.devrassicpark.midnightoil.Resource;

import com.devrassicpark.midnightoil.Exception.domains.EmailExistsException;
import com.devrassicpark.midnightoil.Exception.domains.EmployeeNotFoundException;
import com.devrassicpark.midnightoil.Exception.domains.ExceptionHandling;
import com.devrassicpark.midnightoil.Exception.domains.UsernameExistsException;
import com.devrassicpark.midnightoil.Utility.JwtTokenProvider;
import com.devrassicpark.midnightoil.constants.SecurityConstant;
import com.devrassicpark.midnightoil.models.Employee;
import com.devrassicpark.midnightoil.models.EmployeePrincipal;
import com.devrassicpark.midnightoil.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = {"/","/employee"})
public class EmployeeResource extends ExceptionHandling {

    private EmployeeService employeeService;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public EmployeeResource(EmployeeService employeeService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.employeeService = employeeService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @PostMapping("/login")
    public ResponseEntity<Employee> loginEmployee(@RequestBody Employee employee) {
       authenticate(employee.getUsername(), employee.getPassword());
       Employee loginEmployee = employeeService.findEmployeeByUsername(employee.getUsername());
        EmployeePrincipal employeePrincipal = new EmployeePrincipal(loginEmployee);
        HttpHeaders jwtHeader = getJwtHeader(employeePrincipal);

        return new ResponseEntity<>(loginEmployee, jwtHeader, HttpStatus.OK);
    }


    @PostMapping("/register")
    public ResponseEntity<Employee> registerEmployee(@RequestBody Employee employee) throws EmployeeNotFoundException, UsernameExistsException, EmailExistsException {
       Employee newEmployee =  employeeService.register(employee.getFirstName(), employee.getLastName(), employee.getUsername(), employee.getEmail());

       return new ResponseEntity<>(newEmployee, HttpStatus.OK);
    }

    private HttpHeaders getJwtHeader(EmployeePrincipal employeePrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(employeePrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
