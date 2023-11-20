package com.devrassicpark.midnightoil.services;

import com.devrassicpark.midnightoil.models.Overtime;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface OvertimeService {

    // Get all available overtime
    List<Overtime> getOvertimeShifts();

    // Create an Overtime shift
    Overtime createOvertimeShift(LocalDate shiftDate, String shiftStartTime, String shiftEndTime, String department, String coveredBy);

    // Delete Overtime shift
    void deleteOvertimeShift(Long id);

    // Find Overtime by Department
    Overtime findOvertimeShiftByDepartment(String department);
}
