package com.MaroTekTimesheets.MaroProject.Entity;

import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.*;

import javax.persistence.*;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;

@Entity
@Table(name = "Users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"email"})
public class User {
    public User(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory()).setAudience(Collections.singletonList("574033859076-1htgf8rnd862hdqc8j2a335ne18ganuv.apps.googleusercontent.com"))
                .build();
        GoogleIdToken idToken = verifier.verify(idTokenString);
        IdToken.Payload payload = idToken.getPayload();
        this.Name = (String) payload.get("given_name");
        this.email = (String) payload.get("email");
        this.Status = "A";
        this.CreateDate = new Date();
    }

    @Id
    @SequenceGenerator(name = "seq_Costumer",allocationSize = 1)
    @GeneratedValue(generator = "seq_Costumer",strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(length = 100, name = "Name")
    private String Name;
    @Column(length = 100, name = "email",unique = true)
    private String email;
    @Column(name = "CreateDate")
    private Date CreateDate;
    @Column(length = 1, name = "Status")
    private String Status;
    @Column(name = "Create_User")
    private String Create_User;
    @OneToOne
    //@JoinColumn(name = "UserId")
    private Timesheet timesheet;
    @OneToOne
    @JoinColumn(name = "user_role")
//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//    @PrimaryKeyJoinColumn
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_role")
    private Role user_role;

}
