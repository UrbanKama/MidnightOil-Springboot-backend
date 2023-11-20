package com.devrassicpark.midnightoil.repositories;

import com.devrassicpark.midnightoil.models.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;

public interface OvertimeRepository extends JpaRepository<Overtime, Long> {

    // Find overtime by date
    Overtime findOvertimeByShiftDate(LocalDate shiftDate);

}
