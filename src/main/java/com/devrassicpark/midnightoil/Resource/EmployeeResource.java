package com.devrassicpark.midnightoil.Resource;

import com.devrassicpark.midnightoil.Exception.domains.*;
import com.devrassicpark.midnightoil.Utility.JwtTokenProvider;
import com.devrassicpark.midnightoil.constants.SecurityConstant;
import com.devrassicpark.midnightoil.models.Employee;
import com.devrassicpark.midnightoil.models.EmployeePrincipal;
import com.devrassicpark.midnightoil.models.HttpResponse;
import com.devrassicpark.midnightoil.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.devrassicpark.midnightoil.constants.FileConstant.*;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/","/employee"})
@CrossOrigin("http://localhost:4200")
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

        return new ResponseEntity<>(loginEmployee, jwtHeader, OK);
    }


    @PostMapping("/register")
    public ResponseEntity<Employee> registerEmployee(@RequestBody Employee employee) throws EmployeeNotFoundException, UsernameExistsException, EmailExistsException {
       Employee newEmployee =  employeeService.register(employee.getFirstName(), employee.getLastName(), employee.getUsername(), employee.getEmail(), employee.getPassword());

       return new ResponseEntity<>(newEmployee, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Employee> addNewEmployee(@RequestParam("firstName") String firstName,
                                                   @RequestParam("lastName") String lastName,
                                                   @RequestParam("username") String username,
                                                   @RequestParam("email") String email,
                                                   @RequestParam("role") String role,
                                                   @RequestParam("isActive") String isActive,
                                                   @RequestParam("isNonLocked") String isNonLocked,
                                                   @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, IOException, EmployeeNotFoundException {
        Employee newEmployee = employeeService.addNewEmployee(firstName, lastName, username, email, role,Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNonLocked), profileImage);

        return new ResponseEntity<>(newEmployee, OK);

    }

    @PutMapping("/update")
    public ResponseEntity<Employee> updateEmployee(@RequestParam("currentUsername") String currentUsername,
                                                   @RequestParam("firstName") String firstName,
                                                   @RequestParam("lastName") String lastName,
                                                   @RequestParam("username") String username,
                                                   @RequestParam("email") String email,
                                                   @RequestParam("role") String role,
                                                   @RequestParam("isActive") String isActive,
                                                   @RequestParam("isNonLocked") String isNonLocked,
                                                   @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, IOException, EmployeeNotFoundException {
        Employee updatedEmployee = employeeService.updateEmployee(currentUsername, firstName, lastName, username, email, role,Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNonLocked), profileImage);

        return new ResponseEntity<>(updatedEmployee, OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<Employee> getEmployee(@PathVariable("username") String username){
        Employee employee = employeeService.findEmployeeByUsername(username);
        return new ResponseEntity<>(employee, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Employee>> getAllEmployee(){
        List<Employee> employees = employeeService.getEmployees();
        return new ResponseEntity<>(employees, OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        employeeService.resetPassword(email);
        return response(OK, "A reset email has been sent to " + email);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteEmployee(@PathVariable("id") long id){
        employeeService.deleteEmployee(id);
        return response(NO_CONTENT, "Employee deleted successfully");
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<Employee> updateProfileImage(
                                                   @RequestParam("username") String username,
                                                   @RequestParam(value = "profileImage") MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, IOException, EmployeeNotFoundException {
        Employee employee = employeeService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(employee, OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") long fileName) throws IOException {
        return Files.readAllBytes(Paths.get(EMPLOYEE_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()){
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0){
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }

        return byteArrayOutputStream.toByteArray();
    }


    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase()), httpStatus);
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
