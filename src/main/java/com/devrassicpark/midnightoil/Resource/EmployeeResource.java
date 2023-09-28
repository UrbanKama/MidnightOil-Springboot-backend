package com.devrassicpark.midnightoil.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/employee")
public class EmployeeResource {

    @GetMapping("/home")
    public String showEmployee(){
        return "Application works";
    }
}
