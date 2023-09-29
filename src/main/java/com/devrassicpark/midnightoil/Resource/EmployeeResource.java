package com.devrassicpark.midnightoil.Resource;

import com.devrassicpark.midnightoil.Exception.domains.EmailExistsException;
import com.devrassicpark.midnightoil.Exception.domains.ExceptionHandling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/","/employee"})
public class EmployeeResource extends ExceptionHandling {

    @GetMapping("/home")
    public String showEmployee() throws EmailExistsException{

//        return "Application works";
        throw new EmailExistsException("this email is taken");
    }
}
