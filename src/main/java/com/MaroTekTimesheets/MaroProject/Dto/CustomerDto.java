package com.MaroTekTimesheets.MaroProject.Dto;

import lombok.Data;
import java.util.Date;

@Data
public class CustomerDto {
    private long id;
    private String Name;
    private String Location;
    private String Status;
    private String CreateDate;
    private String CreateUser;
}
