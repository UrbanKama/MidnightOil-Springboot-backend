package com.devrassicpark.midnightoil.Resource;

import com.devrassicpark.midnightoil.Utility.JwtTokenProvider;
import com.devrassicpark.midnightoil.models.Overtime;
import com.devrassicpark.midnightoil.services.OvertimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"/", "/overtime"})
@CrossOrigin("http://localhost:4200")
public class OvertimeResource {

    private OvertimeService overtimeService;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public OvertimeResource(OvertimeService overtimeService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.overtimeService = overtimeService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/createShift")
    public ResponseEntity<Overtime> createOvertimeShift(@RequestBody Overtime overtime){

        Overtime newOvertimeShift = overtimeService.createOvertimeShift(overtime.getShiftDate(), overtime.getShiftStartTime(), overtime.getShiftEndTime(), overtime.getDepartment(), overtime.getCoveredBy());

        return new ResponseEntity<>(newOvertimeShift, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Overtime>> getAllAvailableOvertime(){
        List<Overtime> overtimeShifts = overtimeService.getOvertimeShifts();
        return new ResponseEntity<>(overtimeShifts, HttpStatus.OK);
    }
}
