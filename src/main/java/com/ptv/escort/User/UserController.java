package com.ptv.escort.User;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptv.escort.Config.JwtResponse;
import com.ptv.escort.Config.JwtUtil;
import com.ptv.escort.Config.UserVerification;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;



@RestController
public class UserController {


    @Autowired
    private AuthenticationManager authenticationmanager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserVerification myUserDetailsService;

    @Autowired
    private JwtUtil jwttokenutil;



    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken() throws Exception {
//        try {
//            authenticationmanager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
//        } catch (BadCredentialsException e) {
//            throw new Exception("Wrong credentials", e);
//        }
//        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());
//        if (userDetails == null) {
//            throw new RuntimeException("User does not exist.");
//        }
//        User user = userInterface.signIn(authenticationRequest);a
//        try{
//            ObjectMapper mapper = new ObjectMapper();
////            logger.info(mapper.writeValueAsString(userDetails));
//        }catch (Exception e){
//
//        }
        final String jwt = jwttokenutil.generateToken("test");
        logger.info(jwt);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }


    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody User user){
        return ResponseEntity.ok(userService.createUser(user));
    }
}