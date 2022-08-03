package com.MaroTekTimesheets.MaroProject.Dto;

import com.MaroTekTimesheets.MaroProject.Entity.Role;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Date;

@Data
public class UserDto {
    private Long id;
    private String Name;
    private String email;
    private String CreateDate;
    private String Status;
    private String Create_User;
    private String user_role;
}
