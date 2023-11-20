package com.devrassicpark.midnightoil.services.Implementation;

import com.devrassicpark.midnightoil.Enums.Role;
import com.devrassicpark.midnightoil.Exception.domains.EmailExistsException;
import com.devrassicpark.midnightoil.Exception.domains.EmailNotFoundException;
import com.devrassicpark.midnightoil.Exception.domains.EmployeeNotFoundException;
import com.devrassicpark.midnightoil.Exception.domains.UsernameExistsException;

import static com.devrassicpark.midnightoil.constants.FileConstant.*;
import static com.devrassicpark.midnightoil.constants.employeeImplementationConstants.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.devrassicpark.midnightoil.models.Employee;
import com.devrassicpark.midnightoil.models.EmployeePrincipal;
import com.devrassicpark.midnightoil.repositories.EmployeeRepository;
import com.devrassicpark.midnightoil.services.EmployeeService;
import com.devrassicpark.midnightoil.services.LoginAttemptService;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
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
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public Employee register(String firstName, String lastName, String username, String email, String password) throws UsernameExistsException, EmailExistsException, EmployeeNotFoundException {
        validateNewUsernameAndEmail(EMPTY, username, email);

        Employee employee = new Employee();
        employee.setUserId(generateUserId());
        employee.setPassword(password);
//        String password = generatePassword();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setUsername(username);
        employee.setEmail(email);
        employee.setJoinDate(new Date());
        employee.setPassword(encodedPassword(password));
        employee.setActive(true);
        employee.setNotLocked(true);
        employee.setRole(Role.ROLE_USER.name());
        employee.setAuthorities(Role.ROLE_USER.getAuthorities());
        employee.setProfileImageUrl(getTemporaryProfileImageUrl(username));

        employeeRepository.save(employee);
        LOGGER.info("New user password: " + password);
        return employee;
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_EMPLOYEE_IMAGE_PATH + username).toUriString();
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
        Employee employeeByNewUsername = findEmployeeByUsername(newUsername);
        Employee employeeByNewEmail = findEmployeeByEmail(newEmail);

        if (StringUtils.isNotBlank(currentUsername)){
            Employee currentEmployee = findEmployeeByUsername(currentUsername);
            if (currentEmployee == null){
                throw new EmployeeNotFoundException(NO_EMPLOYEE_FOUND_BY_USERNAME + currentUsername);
            }
            if (employeeByNewUsername != null && !currentEmployee.getId().equals(employeeByNewUsername.getId())) {
                throw new UsernameExistsException("Employee with username: " + employeeByNewUsername + " already exists");
            }
            if (employeeByNewEmail != null && !currentEmployee.getId().equals(employeeByNewEmail.getId())) {
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
    public Employee addNewEmployee(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, EmployeeNotFoundException, IOException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        Employee employee = new Employee();
        String password = generatePassword();
        String encodedPassword = encodedPassword(password);
        employee.setUserId(generateUserId());
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setJoinDate(new Date());
        employee.setUsername(username);
        employee.setEmail(email);
        employee.setPassword(encodedPassword);
        employee.setActive(isActive);
        employee.setNotLocked(isNonLocked);
        employee.setRole(getRoleEnumName(role).name());
        employee.setAuthorities(getRoleEnumName(role).getAuthorities());
        employee.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        employeeRepository.save(employee);
        saveProfileImage(employee, profileImage);
        return employee;
    }

    private void saveProfileImage(Employee employee, MultipartFile profileImage) throws IOException {
        if (profileImage != null){
            Path userFolder = Paths.get(EMPLOYEE_FOLDER + employee.getUsername()).toAbsolutePath().normalize();
            if (Files.exists(userFolder)){
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + employee.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(employee.getUsername()+DOT+JPG_EXTENSION), REPLACE_EXISTING);
            employee.setProfileImageUrl(setProfileImageUrl(employee.getUsername()));
            employeeRepository.save(employee);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
            
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(EMPLOYEE_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    @Override
    public Employee updateEmployee(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, EmployeeNotFoundException, IOException {
        Employee currentEmployee = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        Employee employee = new Employee();
        currentEmployee.setFirstName(newFirstName);
        currentEmployee.setLastName(newLastName);
        currentEmployee.setUsername(newUsername);
        currentEmployee.setEmail(newEmail);
        currentEmployee.setActive(isActive);
        currentEmployee.setNotLocked(isNonLocked);
        currentEmployee.setRole(getRoleEnumName(role).name());
        currentEmployee.setAuthorities(getRoleEnumName(role).getAuthorities());
        employeeRepository.save(currentEmployee);
        saveProfileImage(currentEmployee, profileImage);
        return currentEmployee;
    }

    @Override
    public void deleteEmployee(long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
    Employee employee = employeeRepository.findEmployeeByEmail(email);
    if (employee == null){
        throw new EmailNotFoundException(NO_EMPLOYEE_FOUND_BY_EMAIL + email);
    }
    String password = generatePassword();
    employee.setPassword(encodedPassword(password));
    employeeRepository.save(employee);
        LOGGER.info("New reset user password: " + password);
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

    @Override
    public Employee updateProfileImage(String username, MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, EmployeeNotFoundException, IOException {
        Employee employee = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(employee, profileImage);
        return employee;
    }
}
