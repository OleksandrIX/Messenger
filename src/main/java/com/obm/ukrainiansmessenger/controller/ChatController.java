package com.obm.ukrainiansmessenger.controller;

import com.obm.ukrainiansmessenger.models.Chat;
import com.obm.ukrainiansmessenger.models.Message;
import com.obm.ukrainiansmessenger.models.ResponseMessage;
import com.obm.ukrainiansmessenger.models.User;
import com.obm.ukrainiansmessenger.repository.ChatRepository;
import com.obm.ukrainiansmessenger.repository.UserRepository;
import com.obm.ukrainiansmessenger.servise.WSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChatController {
    @Autowired
    private WSService service;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatRepository chatRepository;


    @GetMapping("/home")
    public String userList(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName());
        List<User> userList = userRepository.findAll();
        model.addAttribute("users", userList);
        model.addAttribute("user", user);
        return "home";
    }
    @PostMapping("/mes")
    public String createChat(@RequestParam("recipientId") Long id, Principal principal, Model model, final @RequestParam(value = "image", required = false) MultipartFile file) {
        User user = userRepository.findByUsername(principal.getName());
        User friend = userRepository.findById(id).get();
        Chat chat = new Chat();

       chat.setUsers(user);
       chat.setUsers(friend);
        chat.setName(user.getUsername() + "-" + friend.getUsername());
        chatRepository.save(chat);
        for(var b : chat.getUsers()){
            System.out.println(b.getUsername() + " blin");
        }
        model.addAttribute("chat", chat);
        model.addAttribute("user", user);
        return "redirect:/chatRoom";
    }

    @GetMapping("/chatRoom")
    public String chatRoom(Principal principal,Model model){

        var cht =userRepository.findByUsername(principal.getName()).getChat();
        for (var ch:cht
        ) {
            ch.setName(principal.getName());
        }
        model.addAttribute("chats", cht);
        return "chatRoom";
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "id", required = false) Long id, Principal principal, Model model){
        var cht =userRepository.findByUsername(principal.getName()).getChat();
        for (var ch:cht
        ) {
            ch.setName(principal.getName());
        }
        model.addAttribute("chats", cht);
        model.addAttribute("recipient",
                chatRepository.findById(id).get().getUsers().stream()
                        .filter(x->!(x.getUsername().equals(principal.getName())))
                        .collect(Collectors.toList()).get(0).getUsername());
        model.addAttribute("chat_id",id);
        var messages = chatRepository.findById(id).get().getSortMessage();
        for (var mes :messages){
            if (!mes.getSender().equals(principal.getName())){
                mes.setMessageSide("left");
            }
        }
        model.addAttribute("mess",messages);

        return "chat";
    }

    @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public ResponseMessage getPrivateMessage(final Message message,
                                             final Principal principal) throws InterruptedException, ParseException {
        Thread.sleep(1);
        Message newMessage = new Message(message.getMessageContent(),principal.getName(),
                message.getRecipient(), message.getMessageSide(), message.getChat_id());
        service.notifyUser(newMessage);
        var chat =  chatRepository.findById(Long.parseLong(message.getChat_id())).get();
        chat.addMessage(newMessage);
        chatRepository.save(chat);
        return new ResponseMessage(principal.getName()+" "+HtmlUtils.htmlEscape(message.getMessageContent()),
                HtmlUtils.htmlEscape(message.getMessageSide()),
                HtmlUtils.htmlEscape(newMessage.getSender()),
                HtmlUtils.htmlEscape(newMessage.getStrTime()),
                HtmlUtils.htmlEscape(newMessage.getChat_id())
        );
    }
}
