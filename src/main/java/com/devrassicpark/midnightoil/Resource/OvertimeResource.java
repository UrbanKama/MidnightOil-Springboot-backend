package com.devrassicpark.midnightoil.Resource;

import com.devrassicpark.midnightoil.Utility.JwtTokenProvider;
import com.devrassicpark.midnightoil.models.Overtime;
import com.devrassicpark.midnightoil.services.OvertimeService;
import org.hibernate.sql.ast.tree.expression.Over;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@RequestMapping(path = {"/", "/overtime"})
@RestController
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

    // CREATE AN OVERTIME SHIFT
    @PostMapping("/createShift")
    public ResponseEntity<Overtime> createOvertimeShift(@RequestBody Overtime overtime){

        Overtime newOvertimeShift = overtimeService.createOvertimeShift(overtime.getShiftDate(), overtime.getShiftStartTime(), overtime.getShiftEndTime(), overtime.getDepartment(), overtime.getCoveredBy());

        return new ResponseEntity<>(newOvertimeShift, HttpStatus.OK);
    }

    // GET ALL AVAILABLE OVERTIME
    @GetMapping("/allOvertime")
    public ResponseEntity<List<Overtime>> getAllAvailableOvertime(){
        List<Overtime> overtimeShifts = overtimeService.getOvertimeShifts();
        return new ResponseEntity<>(overtimeShifts, HttpStatus.OK);
    }

    // GET AVAILABLE OVERTIME BY DATE
    @GetMapping("/availableOvertime/{date}")
    public ResponseEntity<List<Overtime>> getAvailableOvertimeByDate(@PathVariable("date") @DateTimeFormat(pattern= "dd-MM-yyyy") Date date){
        List<Overtime> availableOvertime = overtimeService.getAvailableOvertimeByDate(date);
        return new ResponseEntity<>(availableOvertime, HttpStatus.OK);
    }
}
