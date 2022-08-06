package com.MaroTekTimesheets.MaroProject.Entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Timesheet")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Builder
public class Timesheet implements Comparable<Timesheet>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "UserId")
    private User user;

    @OneToOne
    @JoinColumn(name = "CustomerId")
    private Customer customer;
    //
    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "TimeSheetDate")
    private Date TimeSheetDate;

    @Column(name = "Duration")
    private Long Duration;

    @Column(length = 50,name = "Location")
    private String Location;

    @OneToOne
    @JoinColumn(name = "TaskId")
    private Task task;

    @Column(name = "description")
    private String Desc;

    @Column(name = "CreateDate")
    private Date CreateDate;

    @Override
    public int compareTo(Timesheet o) {
        return getTimeSheetDate().compareTo(o.getTimeSheetDate());
    }
}
