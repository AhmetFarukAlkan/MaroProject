package com.MaroTekTimesheets.MaroProject.Entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Customer")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
public class Customer implements Comparable<Customer>{
    @Id
    @SequenceGenerator(name = "seq_Costumer",allocationSize = 1)
    @GeneratedValue(generator = "seq_Costumer",strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(length = 100, name = "Name")
    private String name;
    @Column(length = 100,name = "Location")
    private String location;
    @Column(length = 1, name = "Status")
    private String Status;
    @Column(name = "CreateDate")
    private Date CreateDate;
    @Column(length = 100,name = "CreateUser")
    private String CreateUser;
    @OneToOne
    //@JoinColumn(name = "CustomerId")
    private Timesheet timesheet;

    @Override
    public int compareTo(Customer o) {
        return getName().compareTo(o.getName());
    }
}
