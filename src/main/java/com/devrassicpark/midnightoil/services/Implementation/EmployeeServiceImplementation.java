package com.devrassicpark.midnightoil.services.Implementation;

import com.devrassicpark.midnightoil.Enums.Role;
import com.devrassicpark.midnightoil.Exception.domains.EmailExistsException;
import com.devrassicpark.midnightoil.Exception.domains.EmployeeNotFoundException;
import com.devrassicpark.midnightoil.Exception.domains.UsernameExistsException;
import static com.devrassicpark.midnightoil.constants.employeeImplementationConstants.*;
import com.devrassicpark.midnightoil.models.Employee;
import com.devrassicpark.midnightoil.models.EmployeePrincipal;
import com.devrassicpark.midnightoil.repositories.EmployeeRepository;
import com.devrassicpark.midnightoil.services.EmployeeService;
import com.devrassicpark.midnightoil.services.LoginAttemptService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
@Qualifier("UserDetailsService")
public class EmployeeServiceImplementation implements EmployeeService, UserDetailsService {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private EmployeeRepository employeeRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private LoginAttemptService loginAttemptService;

    @Autowired
    public EmployeeServiceImplementation(EmployeeRepository employeeRepository, BCryptPasswordEncoder bCryptPasswordEncoder, LoginAttemptService loginAttemptService) {
        this.employeeRepository = employeeRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttemptService = loginAttemptService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findEmployeeByUsername(username);
        if (employee == null){
            LOGGER.error(NO_EMPLOYEE_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_EMPLOYEE_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(employee);
            employee.setLastLoginDateDisplay(employee.getLastLoginDate());
            employee.setLastLoginDate(String.valueOf(new Date()));
            employeeRepository.save(employee);
            EmployeePrincipal employeePrincipal = new EmployeePrincipal(employee);
            LOGGER.info("Returning found user by username: " + username);
            return employeePrincipal;
        }

    }

    private void validateLoginAttempt(Employee employee) {
        if (employee.isNotLocked()){
            if (loginAttemptService.hasExceededMaxAttempts(employee.getUsername())){
                employee.setNotLocked(false);
            } else {
                employee.setNotLocked(true);
            }
        }else {
            loginAttemptService.evictUserFromLoginAttemptedCache(employee.getUsername());
        }
    }

    @Override
    public Employee register(String firstName, String lastName, String username, String email) throws UsernameExistsException, EmailExistsException, EmployeeNotFoundException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);

        Employee employee = new Employee();
        employee.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodedPassword(password);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setUsername(username);
        employee.setEmail(email);
        employee.setJoinDate(new Date());
        employee.setPassword(encodedPassword);
        employee.setActive(true);
        employee.setNotLocked(true);
        employee.setRole(Role.ROLE_USER.name());
        employee.setAuthorities(Role.ROLE_USER.getAuthorities());
        employee.setProfileImageUrl(getTemporaryProfileImageUrl());

        employeeRepository.save(employee);
        LOGGER.info("New user password: " + password);
        return employee;
    }

    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH).toUriString();
    }

    private String encodedPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private Employee validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws EmployeeNotFoundException, UsernameExistsException, EmailExistsException {
        Employee employeeByNewUsername = findEmployeeByUsername(currentUsername);
        Employee employeeByNewEmail = findEmployeeByEmail(newEmail);

        if (StringUtils.isNotBlank(currentUsername)){
            Employee currentEmployee = findEmployeeByUsername(currentUsername);
            if (currentEmployee == null){
                throw new EmployeeNotFoundException(NO_EMPLOYEE_FOUND_BY_USERNAME + currentUsername);
            }
            if (employeeByNewUsername != null && currentEmployee.getId().equals(employeeByNewUsername.getId())) {
                throw new UsernameExistsException("Employee with username: " + employeeByNewUsername + " already exists");
            }
            if (employeeByNewEmail != null && currentEmployee.getId().equals(employeeByNewEmail.getId())) {
                throw new EmailExistsException("Employee with email: " + employeeByNewEmail + " already exists");
            }
            return currentEmployee;
        } else {
            if (employeeByNewUsername != null){
                throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (employeeByNewEmail != null){
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findEmployeeByUsername(String username) {
        return employeeRepository.findEmployeeByUsername(username);
    }

    @Override
    public Employee findEmployeeByEmail(String email) {
        return employeeRepository.findEmployeeByEmail(email);
    }
}
