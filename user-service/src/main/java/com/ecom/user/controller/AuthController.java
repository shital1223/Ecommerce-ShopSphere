package com.ecom.user.controller;

import com.ecom.user.dto.AuthResponse;
import com.ecom.user.dto.LoginRequest;
import com.ecom.user.dto.RegisterRequest;
import com.ecom.user.model.Role;
import com.ecom.user.model.User;
import com.ecom.user.service.RoleService;
import com.ecom.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ecom.user.security.JwtTokenProvider;

import java.util.Set;

@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequest loginReq){
        Authentication auth;
        System.out.println("Loginreq"+loginReq);
        try {
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getUsername(), loginReq.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect Username or password");
        }
        String jwt = jwtTokenProvider.createToken(auth);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterRequest registerReq){
        //1.check if user already exist
        if(userService.userExist(registerReq.getUserName())){
            return ResponseEntity.badRequest().body("Username Already Exist");
        }
        if (userService.existsByEmail(registerReq.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        // 2. create user object to persist in db
        // Hash the password
        String encodedPassword = passwordEncoder.encode(registerReq.getPassword());
        User user = userService.getUserFromRequest(registerReq,encodedPassword);

        // 5.Create and set role
        Role role =  roleService.getRole("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRoles(Set.of(role));
        userService.createUser(user);

        return ResponseEntity.ok("User Registered Successfully");
    }

}
