package com.example.leaveapplicationnew.controller;


import com.example.leaveapplicationnew.auth.jwt.JwtUtils;
import com.example.leaveapplicationnew.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/resetPassword")
    public void resetPassword(@RequestBody String newPassword){
        authService.resetPassword(newPassword);
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerAccount(@RequestBody Map<String, Object> registerRequest){
        return ResponseEntity.ok(authService.registerUser(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/role")
    public ResponseEntity<?> role(@RequestBody Map<String, String> request){
        return ResponseEntity.ok(authService.addRole(request));
    }

    @PostMapping("/user-role")
    public ResponseEntity<?> userRole(@RequestBody Map<String, Long> request){
        return ResponseEntity.ok(authService.addUserRole(request));
    }

    @GetMapping("/leave-type")
    public ResponseEntity<?> leaveType(){
        return ResponseEntity.ok(authService.getLeaveTypes());
    }


}
