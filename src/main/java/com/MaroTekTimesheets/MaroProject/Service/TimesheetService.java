package com.MaroTekTimesheets.MaroProject.Service;


import com.MaroTekTimesheets.MaroProject.Dto.TimesheetDto;
import com.MaroTekTimesheets.MaroProject.Entity.Task;
import com.MaroTekTimesheets.MaroProject.Entity.Timesheet;

import java.text.ParseException;
import java.util.List;

public interface TimesheetService {
    List<TimesheetDto> GetTimeSheets() throws ParseException;

    Timesheet CreateTimesheet(Timesheet timesheet);

    List<Task> GetTasks();

    TimesheetDto getActivityById(Long id);

    Timesheet getTSById(Long id);

    void deleteActivity(Timesheet timesheet);

    TimesheetDto timesheetToTimesheetDto(Timesheet timesheet);
}
