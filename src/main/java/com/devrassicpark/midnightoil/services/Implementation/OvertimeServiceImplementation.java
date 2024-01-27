package com.devrassicpark.midnightoil.services.Implementation;

import com.devrassicpark.midnightoil.models.Overtime;
import com.devrassicpark.midnightoil.repositories.OvertimeRepository;
import com.devrassicpark.midnightoil.services.OvertimeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OvertimeServiceImplementation implements OvertimeService {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private OvertimeRepository overtimeRepository;

    @Autowired
    private OvertimeServiceImplementation(OvertimeRepository overtimeRepository){
        this.overtimeRepository = overtimeRepository;
    }


    @Override
    public Overtime createOvertimeShift(Date shiftDate, String shiftStartTime, String shiftEndTime, String department, String coveredBy) {
        Overtime overtime = new Overtime();
        overtime.setShiftDate(shiftDate);
        overtime.setDepartment(department);
        overtime.setShiftStartTime(shiftStartTime);
        overtime.setShiftEndTime(shiftEndTime);

        overtimeRepository.save(overtime);
        LOGGER.info("shift" + overtime);
        return overtime;
    }

    @Override
    public List<Overtime> getOvertimeShifts() {
        return overtimeRepository.findAll();
    }

    @Override
    public List<Overtime> getAvailableOvertimeByDate(Date shiftDate) {
        return overtimeRepository.findOvertimeByShiftDate(shiftDate);
    }


    @Override
    public void deleteOvertimeShift(Long id) {
    }

    @Override
    public Overtime findOvertimeShiftByDepartment(String department) {
        return null;
    }
}
