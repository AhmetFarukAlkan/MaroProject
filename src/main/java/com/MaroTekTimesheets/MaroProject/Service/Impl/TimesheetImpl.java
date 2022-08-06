package com.MaroTekTimesheets.MaroProject.Service.Impl;

import com.MaroTekTimesheets.MaroProject.Dto.TimesheetDto;
import com.MaroTekTimesheets.MaroProject.Entity.Task;
import com.MaroTekTimesheets.MaroProject.Entity.Timesheet;
import com.MaroTekTimesheets.MaroProject.Repo.RoleRepository;
import com.MaroTekTimesheets.MaroProject.Repo.TaskRepository;
import com.MaroTekTimesheets.MaroProject.Repo.TimesheetRepository;
import com.MaroTekTimesheets.MaroProject.Service.TimesheetService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor

public class TimesheetImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;

    private final TaskRepository taskRepository;

    private final RoleRepository roleRepository;
    @Override
    public Timesheet CreateTimesheet(Timesheet timesheet) {
        final Timesheet timesheetDb = timesheetRepository.save(timesheet);
        timesheet.setId(timesheetDb.getId());
        return timesheet;
    }

    @Override
    public List<TimesheetDto> GetTimeSheets(){
        List<Timesheet> timesheets = timesheetRepository.findAll();
        Collections.sort(timesheets);
        List<TimesheetDto> timesheetDtos =  new ArrayList<>();
        for (Timesheet timesheet:timesheets) {
            TimesheetDto timesheetDto = new TimesheetDto();
            timesheetDto.setId(timesheet.getId());
            String pattern = "dd MMMM yyyy EEEEE";
            SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern, new Locale("tr", "TR"));
            timesheetDto.setTimeSheetDate(simpleDateFormat.format(timesheet.getTimeSheetDate()));
            timesheetDto.setLocation(timesheet.getLocation());
            timesheetDto.setDesc(timesheet.getDesc());
            timesheetDto.setDuration(timesheet.getDuration());
            timesheetDto.setCustomer(timesheet.getCustomer().getName());
            timesheetDto.setTask(timesheet.getTask().getName());
            timesheetDto.setUser(timesheet.getUser());
            timesheetDto.setCreateDate(timesheet.getCreateDate());
            timesheetDtos.add(timesheetDto);

        }
        return timesheetDtos;
    }

    @Override
    public List<Task> GetTasks(){
        List<Task> tasks = taskRepository.findAll();
        return tasks;
    }

    @Override
    public TimesheetDto getActivityById(Long id) {
        Timesheet timesheet = timesheetRepository.findAll().stream()
                .filter(t -> t.getId() == id)
                .findFirst().orElse(null);
        TimesheetDto timesheetDto = new TimesheetDto();
        timesheetDto.setId(timesheet.getId());
        timesheetDto.setCreateDate(timesheet.getCreateDate());
        timesheetDto.setCustomer(timesheet.getCustomer().getName());
        timesheetDto.setTask(timesheet.getTask().getName());
        timesheetDto.setUser(timesheet.getUser());
        timesheetDto.setDesc(timesheet.getDesc());
        timesheetDto.setLocation(timesheet.getLocation());
        timesheetDto.setDuration(timesheet.getDuration());
        //
        timesheetDto.setTimeSheetDate(String.valueOf(timesheet.getTimeSheetDate()));
        return timesheetDto;
    }

    @Override
    public TimesheetDto timesheetToTimesheetDto(Timesheet timesheet){
        TimesheetDto timesheetDto = new TimesheetDto();
        timesheetDto.setId(timesheet.getId());
        timesheetDto.setUser(timesheet.getUser());
        timesheetDto.setTask(timesheet.getTask().getName());
        timesheetDto.setCustomer(timesheet.getCustomer().getName());
        timesheetDto.setDuration(timesheet.getDuration());
        timesheetDto.setDesc(timesheet.getDesc());
        timesheetDto.setCreateDate(timesheet.getCreateDate());
        timesheetDto.setLocation(timesheet.getLocation());
        String pattern = "dd MMMM yyyy EEEEE";
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern, new Locale("tr", "TR"));
        timesheetDto.setTimeSheetDate(simpleDateFormat.format(timesheet.getTimeSheetDate()));
        return timesheetDto;
    }

    @Override
    public void deleteActivity(Timesheet timesheet){
        timesheetRepository.delete(timesheet);
    }

    @Override
    public Timesheet getTSById(Long id) {
        return timesheetRepository.findAll().stream()
                .filter(t -> t.getId() == id)
                .findFirst().orElse(null);
    }
}
