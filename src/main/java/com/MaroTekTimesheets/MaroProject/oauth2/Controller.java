package com.MaroTekTimesheets.MaroProject.oauth2;

import com.MaroTekTimesheets.MaroProject.Entity.User;
import com.MaroTekTimesheets.MaroProject.Service.UserService;
import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashMap;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
@Getter
@Setter
@org.springframework.stereotype.Controller
@PermitAll
@ControllerAdvice

public final class Controller {

    private final UserService userService;
    static private User user;

    public Controller(UserService userService) throws IOException {this.userService = userService;}

    public User getUser() {
        return user;
    }

    private String Token;

    @GetMapping("")
    public String showLoginPage(){
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
