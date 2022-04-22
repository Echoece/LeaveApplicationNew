package com.example.leaveapplicationnew.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping("/")
    //@PreAuthorize("hasRole('ADMIN')")
    public void testing(Principal user){
        System.out.println("hello world");
        System.out.println(user);
    }
}
