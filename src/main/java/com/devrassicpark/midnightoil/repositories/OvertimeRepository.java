package com.devrassicpark.midnightoil.repositories;

import com.devrassicpark.midnightoil.models.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OvertimeRepository extends JpaRepository<Overtime, Long> {

    // Find overtime by date
    @Query(value = "SELECT * FROM overtime WHERE shift_date = ?1", nativeQuery = true)
    List<Overtime> findOvertimeByShiftDate(Date shiftDate);

}
