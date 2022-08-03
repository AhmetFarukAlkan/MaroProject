package com.MaroTekTimesheets.MaroProject.Repo;

import com.MaroTekTimesheets.MaroProject.Entity.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
}
