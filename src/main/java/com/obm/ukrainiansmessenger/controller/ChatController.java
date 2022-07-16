package com.obm.ukrainiansmessenger.controller;

import com.obm.ukrainiansmessenger.models.User;
import com.obm.ukrainiansmessenger.repository.ChatRepository;
import com.obm.ukrainiansmessenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatRepository chatRepository;

    @GetMapping("/home")
    public String showAllUsers(Model model){
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users",users);
        return "home";
    }

    @PostMapping("/createChat")
    public String createChat(User user,Principal principal, @PathVariable(name = "userName") String username){
//        Chat chat = new Chat();
//        System.out.println(userRepository.findByUsername(principal.getName()));
         System.out.println(username);
//        chat.setSenderUserId(userRepository.findByUsername(principal.getName()).getId());
//        System.out.println(userRepository.findByUsername(principal.getName()).getId());
//        chat.setRecipientUserId(userRepository.findByUsername(userName).getId());
//        System.out.println(userRepository.findByUsername(userName).getId());
//        chatRepository.save(chat);
        return "home";
    }
}
