package com.MaroTekTimesheets.MaroProject.Service;

import com.MaroTekTimesheets.MaroProject.Dto.CustomerDto;
import com.MaroTekTimesheets.MaroProject.Dto.UserDto;
import com.MaroTekTimesheets.MaroProject.Entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

public interface UserService {

    DefaultOidcUser CreateUser(
            DefaultOidcUser defaultOidcUser);

    List<UserDto> GetUsers();

    User GetUser(Long id);

    UserDto Insert(UserDto userDto);
    User ControlUser(OAuth2User user);

    User getUserByEmail(String mail);
    User getUserById(long id);
    UserDto getUserDtoById(long id);
    Boolean isActive(OAuth2User user);
}
