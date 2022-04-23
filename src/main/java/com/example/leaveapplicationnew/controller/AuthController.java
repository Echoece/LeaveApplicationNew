package com.example.leaveapplicationnew.controller;


import com.example.leaveapplicationnew.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void registerAccount(){

    }


}
