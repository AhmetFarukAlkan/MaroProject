package com.MaroTekTimesheets.MaroProject.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "role")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"Name"})
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String Name;

    @Column(length = 100, name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "user_role")
//    @OneToOne
//    @MapsId
//    @JoinColumn(name = "user_id")
    private User user;


}
