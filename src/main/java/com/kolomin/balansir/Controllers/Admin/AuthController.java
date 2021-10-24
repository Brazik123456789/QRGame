package com.kolomin.balansir.Controllers.Admin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kolomin.balansir.Config.SecurityConfig;
import com.kolomin.balansir.Entities.User;
import com.kolomin.balansir.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@RestController
public class AuthController {
    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String getBCryptPassword(HttpEntity<String> rq){
        return userService.login(rq);
    }
}