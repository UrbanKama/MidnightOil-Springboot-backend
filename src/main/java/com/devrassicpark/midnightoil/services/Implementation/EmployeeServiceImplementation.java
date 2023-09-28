package com.devrassicpark.midnightoil.services.Implementation;

import com.devrassicpark.midnightoil.models.Employee;
import com.devrassicpark.midnightoil.models.EmployeePrincipal;
import com.devrassicpark.midnightoil.repositories.EmployeeRepository;
import com.devrassicpark.midnightoil.services.EmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
@Qualifier("UserDetailsService")
public class EmployeeServiceImplementation implements EmployeeService, UserDetailsService {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImplementation(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findEmployeeByUsername(username);
        if (employee == null){
            LOGGER.error("Employee not found by username: " + username);
            throw new UsernameNotFoundException("Employee not found by username: " + username);
        } else {
            employee.setLastLoginDateDisplay(employee.getLastLoginDate());
            employee.setLastLoginDate(String.valueOf(new Date()));
            employeeRepository.save(employee);
            EmployeePrincipal employeePrincipal = new EmployeePrincipal(employee);
            LOGGER.info("Returning found user by username: " + username);
            return employeePrincipal;
        }

    }
}
