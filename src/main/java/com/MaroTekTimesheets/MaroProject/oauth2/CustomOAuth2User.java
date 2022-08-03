package com.MaroTekTimesheets.MaroProject.oauth2;

import com.MaroTekTimesheets.MaroProject.Entity.User;
import com.MaroTekTimesheets.MaroProject.Repo.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

//public class CustomOAuth2User implements org.springframework.security.oauth2.core.user.OAuth2User {
//
//    private UserRepository userRepository;
//    private OAuth2User oAuth2User;
//
//    public CustomOAuth2User(OAuth2User auth2User){
//        this.oAuth2User = auth2User;
//    }
//
//    @Override
//    public Map<String, Object> getAttributes() {
//        return oAuth2User.getAttributes();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return oAuth2User.getAuthorities();
//    }
//
//    @Override
//    public String getName() {
//        return oAuth2User.getAttribute("Name");
//    }
//
//    public String getFullName() {
//        return oAuth2User.getAttribute("fullName");
//    }
//
//    public Long getId(){
//        return getUserByEmail(oAuth2User.getAttribute("mail")).getId();
//    }
//
//    private User getUserByEmail(String mail){
//        return   userRepository.findAll().stream()
//                .filter(u -> u.getEmail().equals(mail))
//                .findFirst().orElse(null);
//    }
//}
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
class CustomOAuth2UserService extends DefaultOAuth2UserService  {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user =  super.loadUser(userRequest);
        return new CustomOAuth2User(user);
    }

}