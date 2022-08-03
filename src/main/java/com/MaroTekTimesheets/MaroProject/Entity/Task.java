package com.MaroTekTimesheets.MaroProject.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Task")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, name = "name")
    private String name;
    @Column(name = "description")
    private String desc;

    @OneToOne
    @JoinColumn(name = "TaskId")
    private Timesheet timesheet;

}
