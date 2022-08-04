package com.MaroTekTimesheets.MaroProject.oauth2;

import com.MaroTekTimesheets.MaroProject.Entity.Role;
import com.MaroTekTimesheets.MaroProject.Entity.Task;
import com.MaroTekTimesheets.MaroProject.Entity.User;
import com.MaroTekTimesheets.MaroProject.Repo.RoleRepository;
import com.MaroTekTimesheets.MaroProject.Repo.TaskRepository;
import com.MaroTekTimesheets.MaroProject.Repo.UserRepository;
import com.MaroTekTimesheets.MaroProject.Service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;

@Getter
@Setter
@org.springframework.stereotype.Controller
@PermitAll
@ControllerAdvice

public final class Controller {

    private  final RoleRepository roleRepository;

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final UserService userService;
    static private User user;

    public Controller(RoleRepository roleRepository, TaskRepository taskRepository, UserRepository userRepository, UserService userService) throws IOException {
        this.roleRepository = roleRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userService = userService;}

    public User getUser() {
        return user;
    }

    private String Token;

    @GetMapping("")
    public String showLoginPage(){
//        Role role = new Role();
//        role.setName("USER");
//        role.setDescription("User");
//        roleRepository.save(role);
//
//        Role role1 = new Role();
//        role1.setName("ADMIN");
//        role1.setDescription("Admin");
//        roleRepository.save(role1);
//
//        Task task = new Task();
//        task.setId(4L);
//        task.setName("Project Management");
//        task.setDesc("project management");
//        taskRepository.save(task);
//        Task task1 = new Task();
//        task1.setId(2L);
//        task1.setName("Development");
//        task1.setDesc("development");
//        taskRepository.save(task1);
//        Task task2 = new Task();
//        task2.setId(3L);
//        task2.setName("Presale");
//        task2.setDesc("presale");
//        taskRepository.save(task2);
//        Task task3 = new Task();
//        task3.setName("Management");
//        task3.setDesc("management");
//        taskRepository.save(task3);
//        Task task4 = new Task();
//        task4.setName("Business Analysis & Test");
//        task4.setDesc("business analysis & test");
//        taskRepository.save(task4);
//        Task task5 = new Task();
//        task5.setName("Eğitim");
//        task5.setDesc("eğitim");
//        taskRepository.save(task5);
//        User user1 = new User();
//        user1.setCreate_User("2");
//        user1.setCreateDate(new Date());
//        user1.setStatus("A");
//        user1.setName("Ahmet Faruk Alkan");
//        user1.setEmail("ahmetfarukalkan08@gmail.com");
//        user1.setUser_role(roleRepository.getReferenceById("ADMIN"));
//        userRepository.save(user1);

        return "Login";
    }

    @GetMapping({"/home/logout","/login?logout"})
    public String logout(){
        return "redirect:/";
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception ex,
                                         HttpServletRequest request, HttpServletResponse response) {
        if (ex instanceof NullPointerException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/home")
    public String showHomePage(Model model, @AuthenticationPrincipal OAuth2User user) throws IOException {
        if(userService.ControlUser(user) != null && userService.ControlUser(user).getStatus().equals("A")){
            if (userService.ControlUser(user).getUser_role().getName().equals("ADMIN")){
                //System.out.println(userService.ControlUser(user).getUser_role().getName());
                model.addAttribute("picture", user.getAttributes().get("picture"));
                model.addAttribute("user", userService.ControlUser(user));
                return "admin";
            }
            else if (userService.ControlUser(user).getUser_role().getName().equals("USER")){
                //System.out.println(userService.ControlUser(user).getUser_role().getName());
                model.addAttribute("picture", user.getAttributes().get("picture"));
                model.addAttribute("user", userService.ControlUser(user));
                return "user";
            }
        }
        else{
            return "redirect:/passive-user";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String Login(){
        //System.out.println("/login");
        return "redirect:/home";
    }

    @GetMapping("/passive-user")
    public String PassiveUser(){
        return "passive_user";
    }

    @PostMapping("/login")
    public void authentication(@RequestParam(value = "idtoken") String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList("574033859076-1htgf8rnd862hdqc8j2a335ne18ganuv.apps.googleusercontent.com"))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            //System.out.println("idtokenString: " + idTokenString);
            //this.Token = idTokenString;
            GoogleIdToken.Payload payload = idToken.getPayload();

            //System.out.println("İdtoken: " + idToken);
            //System.out.println("payload: " + payload);
            // Print user identifier
            //String userId = payload.getSubject();
            //System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            //System.out.println(email);

            User user1 = new User();
            user1.setName(name);
            user1.setEmail(email);
            user1.setStatus("A");
            //user = userService.ControlUser(user1);
            // Use or store profile information
            // ...

        } else {
            System.out.println("Invalid ID token.");
        }

    }

//    @GetMapping("/login/oauth2/code/google")
////    @GetMapping("/oauth2/authorization/google/login")
////    @GetMapping({"/oauth2/authorization/google","/login/oauth2/code/google"})
//    public String showHomePage(){
//        System.out.println("login1");
//        if(SecurityContextHolder.getContext().getAuthentication() != null){
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            Object pricipal = auth.getPrincipal();
//            if (pricipal instanceof DefaultOidcUser){
//                //System.out.println(((DefaultOidcUser) pricipal).getAttributes());
//                User user1 = new User();
//                user1.setName(((DefaultOidcUser) pricipal).getFullName());
//                user1.setEmail(((DefaultOidcUser) pricipal).getEmail());
//                user1.setStatus("A");
//                user = userService.ControlUser(user1);
//            }
//        }
//        System.out.println("login2");
//        return "redirect:/home";
//    }

//    @GetMapping("/login")
//    public String loginPage(Model model){
//        System.out.println("goto...");
////        return "redirect:/home";
//        model.addAttribute("user", user);
//        return "admin";
//
//    }


//    @GetMapping({"/oauth2/authorization/google/login","/log","/login/oauth2/code/google"})
//    public String showHomePage(){
//        System.out.println("shp1");
//        if(SecurityContextHolder.getContext().getAuthentication() != null){
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            Object pricipal = auth.getPrincipal();
//            if (pricipal instanceof DefaultOidcUser){
//                System.out.println(((DefaultOidcUser) pricipal).getAttributes());
//                System.out.println(((DefaultOidcUser) pricipal).getEmail());
//                user = userService.getUserByEmail(((DefaultOidcUser) pricipal).getEmail());
//            }
//        }
//        return "redirect:/home";
//    }

}
