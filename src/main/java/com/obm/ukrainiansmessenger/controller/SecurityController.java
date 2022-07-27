package com.obm.ukrainiansmessenger.controller;

import com.obm.ukrainiansmessenger.models.User;
import com.obm.ukrainiansmessenger.models.UserTransporter;
import com.obm.ukrainiansmessenger.servise.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {

    private final UserService userService;

    public SecurityController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String login() {
        return "index";
    }

    @PostMapping("/")
    public String addUser(@RequestParam("email") String email) {
        if (userService.searchEmailInDB(email)) {
            User user = new User();
            user.setEmail(email);
            userService.addUser(user);
        }
        return "redirect:/activate";
    }

    @GetMapping("/activate")
    public String activate() {
        return "activate";
    }

    @PostMapping("/activate")
    public String sendActivateCode(@RequestParam("code") String code, Model model) {
        User user = userService.activeUser(code);
        if (user.getActivationCode() == null) {
            UserTransporter.setUser(user);
            if (user.getUsername()==null && user.getPassword()==null) {
                return "redirect:/registration";
            }else {
                return "redirect:/login";
            }
        }else {
            model.addAttribute("message","Confirmation code entered incorrectly");
        }
        return "activate";
    }

    @GetMapping("/login")
    public String pageLogin(Model model){
        User user = UserTransporter.getUser();
        model.addAttribute("user", user);
        return "login";
    }

    @GetMapping("/registration")
    public String pageRegistration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String pageRegistration(@RequestParam("username")String username,
                                   @RequestParam("password")String password){
        User user = UserTransporter.getUser();
        if(userService.save(username,password,user)){
            return "redirect:/login";
        }
        return "registration";
    }
}
