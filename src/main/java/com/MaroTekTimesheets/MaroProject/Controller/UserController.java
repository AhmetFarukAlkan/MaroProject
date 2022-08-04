package com.MaroTekTimesheets.MaroProject.Controller;

import com.MaroTekTimesheets.MaroProject.Dto.UserDto;
import com.MaroTekTimesheets.MaroProject.Entity.Customer;
import com.MaroTekTimesheets.MaroProject.Entity.User;
import com.MaroTekTimesheets.MaroProject.Repo.UserRepository;
import com.MaroTekTimesheets.MaroProject.Service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//@RestController
@Controller
@RequestMapping("/home/users")
@RequiredArgsConstructor
@Getter
@Setter
public class UserController{
    @Autowired
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("")
    public String showUserList(Model model, @AuthenticationPrincipal OAuth2User user){
        if (userService.isActive(user)){
            if (userService.ControlUser(user).getUser_role().getName().equals("ADMIN")){
                List<UserDto> userList = userService.GetUsers();
                model.addAttribute("listUsers", userList);
                model.addAttribute("picture", user.getAttributes().get("picture"));
                model.addAttribute("user", userService.ControlUser(user));
                return "users";
            }else if (userService.ControlUser(user).getUser_role().getName().equals("USER")){
                return "redirect:/home";
            }
        }else {
            return "redirect:/passive-user";
        }
        model.addAttribute("user", new User());
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal OAuth2User user1){
        if (userService.isActive(user1)){
            if (userService.ControlUser(user1).getUser_role().getName().equals("ADMIN")){
                UserDto user = userService.getUserDtoById(id);
                model.addAttribute("user", user);
                model.addAttribute("pagetitle", "Edit User");
                model.addAttribute("picture", user1.getAttributes().get("picture"));
                model.addAttribute("user1", userService.ControlUser(user1));
                return "user_form";
            }
            else if (userService.ControlUser(user1).getUser_role().getName().equals("USER")){
                return "redirect:/home";
            }
        }else {
            return "redirect:/passive-user";
        }

        model.addAttribute("user", new User());
        return "redirect:/";
    }

//    @GetMapping("/get")
//    public ResponseEntity<List<UserDto>> GetUsers(){
//        return ResponseEntity.ok(userService.GetUsers());
//    }
//
//    @GetMapping("/get/{id}")
//    public ResponseEntity<User> Select(@PathVariable(value = "id") long id){
//        return ResponseEntity.ok(userService.GetUser(id));
//    }

    @PostMapping({"/save"})
    public String Insert(UserDto user,@AuthenticationPrincipal OAuth2User user1){
        //user.setId(user.getId());
        user.setCreate_User(String.valueOf(userService.ControlUser(user1).getId()));
        userService.Insert(user);
        return "redirect:/home/users";
    }
}
