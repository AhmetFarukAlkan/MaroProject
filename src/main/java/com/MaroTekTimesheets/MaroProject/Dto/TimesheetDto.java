package com.MaroTekTimesheets.MaroProject.Dto;

import com.MaroTekTimesheets.MaroProject.Entity.Customer;
import com.MaroTekTimesheets.MaroProject.Entity.Task;
import com.MaroTekTimesheets.MaroProject.Entity.User;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Data
public class TimesheetDto {
    private Long id;
    private User user;
    private String customer;
    private String TimeSheetDate;
    private Long Duration;
    private String Location;
    private String task;
    private String Desc;
    private Date CreateDate;
}
