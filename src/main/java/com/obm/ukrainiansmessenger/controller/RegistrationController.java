package com.obm.ukrainiansmessenger.controller;

import com.obm.ukrainiansmessenger.models.User;
import com.obm.ukrainiansmessenger.servise.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController{
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model){
        if(!userService.addUser(user)){
            model.addAttribute("message", "Такий користувач вже існує");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String sendActivateCode(@PathVariable String code, Model model){
        boolean isActivated = userService.activeUser(code);

        if (isActivated){
            model.addAttribute("message", "User successfully activated");
        }else {
            model.addAttribute("message", "Activation code is not found");
        }

        return "login";
    }

}
