package com.MaroTekTimesheets.MaroProject.oauth2;


import com.google.api.client.http.apache.ApacheHttpTransport;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//@RestController
public class SecurityController {
//
//    @Autowired
//    private TokenCreator tokenCreator;
//
//    @Value("${google.client.id}")
//    private String GOOGLE_CLIENT_ID;
//
//    @Autowired
//    SecurityController() {	}
//
//
//    @RequestMapping(method = RequestMethod.POST, path="/googleToken", produces={"application/json"}, consumes={"application/json"})
//    ResponseEntity<?> googleToken(@RequestBody String idTokenString  ) {
//
//        ApacheHttpTransport transport = new ApacheHttpTransport();
//        JacksonFactory jsonFactory = new JacksonFactory();
//
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
//                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
//                .build();
//
//        // (Receive idTokenString by HTTPS POST)
//
//        GoogleIdToken idToken;
//        try {
//            idToken = verifier.verify(idTokenString);
//            if (idToken != null) {
//                Payload payload = idToken.getPayload();
//
//                /*
//                 * UserInfo is a HashMap which will store the User Information,
//                 * later to be sent back to the frontend as JSON
//                 */
//                HashMap<String, String> UserInfo = new HashMap<String, String>();
//
//                String userId = payload.getSubject();
//                UserInfo.put("id", userId);
//
//                // Get profile information from payload
//                String email = payload.getEmail();
//                UserInfo.put("email", email);
//
//                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//                UserInfo.put("emailVerified", String.valueOf(emailVerified));
//
//                String name = (String) payload.get("name");
//                UserInfo.put("name", name);
//
//                String family_name = (String) payload.get("family_name");
//                UserInfo.put("family_name", name);
//
//                String pictureUrl = (String) payload.get("picture");
//                UserInfo.put("pictureUrl", pictureUrl);
//
//                String[] claims;
//
//                /*
//                 * This code is where we define the "authorization" part…
//                 * Which users will we allow to access the sensitive parts of our API?
//                 * Typically this will be coded into some database repository, but for
//                 * illustrative purposes we define this in our code, right here
//                 *
//                 * In this case, only the user who has the Google YOLO token containing
//                 * davevassallo@gmail.com as their address is given an extra Authority …. "ACTUATOR"
//                 *
//                 */
//
//                if (email.equals("davevassallo@gmail.com")){
//                    claims = new String[]{"API_ALLOWED", "ACTUATOR"};
//                } else {
//                    claims = new String[]{"API_ALLOWED"};
//                }
//
//                /*
//                 * The Authorities we granted to the users above are coded into the JWT as "claims"
//                 */
//
//                String token = tokenCreator.issueToken(claims, userId, null);
//                UserInfo.put("api_token", token);
//
//                /*
//                 * We turn the HashMap into a JSON string and send it back to the user
//                 */
//
//                return new ResponseEntity<String>(new ObjectMapper().writeValueAsString(UserInfo), HttpStatus.OK);
//
//            } else {
//                System.out.println("Invalid ID token.");
//                return new ResponseEntity<Error>(HttpStatus.UNAUTHORIZED);
//            }
//        } catch (GeneralSecurityException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return new ResponseEntity<Error>(HttpStatus.INTERNAL_SERVER_ERROR);
//
//    }


}