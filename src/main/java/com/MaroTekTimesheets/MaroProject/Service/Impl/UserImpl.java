package com.MaroTekTimesheets.MaroProject.Service.Impl;

import com.MaroTekTimesheets.MaroProject.Dto.UserDto;
import com.MaroTekTimesheets.MaroProject.Entity.Customer;
import com.MaroTekTimesheets.MaroProject.Entity.Role;
import com.MaroTekTimesheets.MaroProject.Entity.User;
import com.MaroTekTimesheets.MaroProject.Repo.RoleRepository;
import com.MaroTekTimesheets.MaroProject.Repo.UserRepository;
import com.MaroTekTimesheets.MaroProject.Service.UserService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Override
    @Transactional
    public DefaultOidcUser CreateUser(DefaultOidcUser defaultOidcUser) {
        final User user = new User();
        user.setName(defaultOidcUser.getFullName());
        user.setEmail(defaultOidcUser.getEmail());
        user.setCreateDate(new Date());
        user.setStatus("A");
        user.setCreate_User("1");
        Role role = roleRepository.getReferenceById("USER");
        user.setUser_role(role);
        if(getUserByEmail(user.getEmail())==null)
            userRepository.save(user);

        return defaultOidcUser;
    }
    @Override
    public List<UserDto> GetUsers(){
        List<User> users = userRepository.findAll();
        Collections.sort(users);
        List<UserDto> usersDtos = new ArrayList<>();
        users.forEach(user -> {
            UserDto userDto = new UserDto();
            userDto.setCreate_User(user.getCreate_User());
            userDto.setUser_role(user.getUser_role().getName());
            userDto.setEmail(user.getEmail());
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern, new Locale("tr", "TR"));
            userDto.setCreateDate(simpleDateFormat.format(user.getCreateDate()));
            userDto.setStatus(user.getStatus());
            userDto.setId(user.getId());
            userDto.setName(user.getName());
            usersDtos.add(userDto);
        });
        return usersDtos;

    }

    @Override
    public User GetUser(Long id){
        return getUserById(id);
    }

    @Override
    public UserDto Insert(UserDto userDto){
        final User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setStatus(userDto.getStatus());
        user.setCreateDate(new Date());
        user.setCreate_User(userDto.getCreate_User());
        user.setUser_role(roleRepository.getReferenceById(userDto.getUser_role()));
        //if(getUserByEmail(user.getEmail())==null)
            userRepository.save(user);
        return userDto;
    }



    @Override
    public User ControlUser(OAuth2User user){
        if (getUserByEmail((String) user.getAttributes().get("email"))==null){
            User user1 = new User();
            user1.setEmail((String) user.getAttributes().get("email"));
            user1.setName((String) user.getAttributes().get("name"));
            Role role = roleRepository.getReferenceById("USER");
            user1.setCreateDate(new Date());
            user1.setUser_role(role);
            user1.setStatus("A");
            user1.setCreate_User("1");
            user1 = userRepository.save(user1);
            return user1;
        }else{
            return getUserByEmail((String) user.getAttributes().get("email"));
        }
    }
    @Override
    public User getUserByEmail(String mail){
        return   userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(mail))
                .findFirst().orElse(null);
    }

    @Override
    public User getUserById(long id){
        return   userRepository.findAll().stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDto getUserDtoById(long id){
        User user = userRepository.findAll().stream()
                .filter(user1 -> user1.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = new UserDto();
        userDto.setCreate_User(user.getCreate_User());
        userDto.setUser_role(user.getUser_role().getName());
        userDto.setEmail(user.getEmail());
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern, new Locale("tr", "TR"));
        userDto.setCreateDate(simpleDateFormat.format(user.getCreateDate()));
        userDto.setStatus(user.getStatus());
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        return userDto;
    }

    @Override
    public Boolean isActive(OAuth2User user){
        if (ControlUser(user).getStatus().equals("A")){
            return true;
        }
        else {
            return false;
        }
    }

}
