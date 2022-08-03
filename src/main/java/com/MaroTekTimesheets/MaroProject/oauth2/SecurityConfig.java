package com.MaroTekTimesheets.MaroProject.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@Order(67)
@Configuration
public class SecurityConfig
{
    @Autowired
    private DataSource dataSource;

    @Autowired
    private CustomOAuth2UserService oauthUserService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .csrf().disable()
//                .requestMatchers().antMatchers("/oauth2/authorization/google/login**","/oauth2/authorization/google**","/login/oauth2/code/google**")
////                .requestMatchers().antMatchers("/home**","/oauth2/authorization/google/login**","/oauth2/authorization/google**")
////                .requestMatchers().antMatchers("/login/oauth2/code/google**","/login**","/users/login**","/oauth2/authorization/google/login**","/oauth2/authorization/google**")
//                .and()
//                .authorizeRequests().antMatchers("/").permitAll()
//                .anyRequest().fullyAuthenticated()
//                .and()
//                .oauth2Login();

//        httpSecurity.authorizeRequests()
//                .antMatchers("/**", "/home**/**", "/oauth/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin().permitAll()
//                .and()
//                .oauth2Login()
//                .loginPage("/")
//                .userInfoEndpoint()
//                .userService(oauthUserService)
//                .and()
//                .and()
//                .oauth2Login()
//                .loginPage("/home")
//                .userInfoEndpoint()
//                .userService(oauthUserService)
//                .and()
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                                        Authentication authentication) throws IOException, ServletException {
//                        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
//                        System.out.println(oauthUser.getEmail());
//                        response.sendRedirect("/list");
//                    }
//                })

//        httpSecurity
//                 .csrf()
//                .disable()
//                .antMatcher("/home**")
//                .authorizeRequests()
//                .antMatchers("/home", "/admin.html")
//                .permitAll()
//                .anyRequest()
//                .authenticated();

            httpSecurity
                    .csrf().disable()
//                    "/home**/**"
                    .requestMatchers().antMatchers("/home**/**","/oauth2/authorization/google/login**","/oauth2/authorization/google**","/login/oauth2/code/google**")
                    .and()
                    .authorizeRequests().antMatchers("/").permitAll()
                    .anyRequest().fullyAuthenticated()
                    .and()
                    .oauth2Login()
                    //

                    //
                    .and()
                    .logout()
                    .logoutUrl("/home/logout")
                    .logoutSuccessUrl("/")
                    //.and().rememberMe().tokenRepository(persistentTokenRepository())
            ;
        return httpSecurity.build();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository =  new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        System.out.println(tokenRepository);
        return tokenRepository;
    }
    //@Autowired
    //private CustomOAuth2UserService oAuth2UserService;

}