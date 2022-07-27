package com.obm.ukrainiansmessenger.controller;

import com.obm.ukrainiansmessenger.models.Chat;
import com.obm.ukrainiansmessenger.models.Message;
import com.obm.ukrainiansmessenger.models.ResponseMessage;
import com.obm.ukrainiansmessenger.models.User;
import com.obm.ukrainiansmessenger.repository.ChatRepository;
import com.obm.ukrainiansmessenger.repository.UserRepository;
import com.obm.ukrainiansmessenger.servise.ChatService;
import com.obm.ukrainiansmessenger.servise.UserService;
import com.obm.ukrainiansmessenger.servise.WSService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ChatController {
    private final WSService service;
    private final UserService userService;
    private final ChatService chatService;

    public ChatController(WSService service, UserService userService, ChatService chatService) {
        this.service = service;
        this.userService = userService;
        this.chatService = chatService;
    }


    @GetMapping("/listChats")
    public String userList(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        List<User> userList = userService.findAll();
        List<Chat> chatList = userService.findByUsername(principal.getName()).getChat();
//        for (User us: userList) {
//           Optional<Chat> ch = chatList.stream().filter(true).findAny();
//        }
       // chatList.stream().filter(chat -> chat.getName().equals(user.getUsername())).findAny();
        userList.remove(user);
        model.addAttribute("users", userList);
        model.addAttribute("user", user);
        model.addAttribute("chats", chatList);
        return "listChats";
    }

    @PostMapping("/mes")
    public String createChat(@RequestParam("recipientId") Long id, Principal principal, Model model, final @RequestParam(value = "image", required = false) MultipartFile file) {
        User user = userService.findByUsername(principal.getName());
        Optional<User> friend = userService.findById(id);
        Chat chat = new Chat();

        chat.setUsers(user);
        chat.setUsers(friend.get());
        chat.setName(user.getUsername() + "-" + friend.get().getUsername());
        chatService.save(chat);
        for (var b : chat.getUsers()) {
            System.out.println(b.getUsername() + " blin");
        }
        model.addAttribute("chat", chat);
        model.addAttribute("user", user);
        return "redirect:/chatRoom";
    }

    @GetMapping("/chatRoom")
    public String chatRoom(Principal principal, Model model) {
        var cht = userService.findByUsername(principal.getName()).getChat();
        for (var ch : cht
        ) {
            ch.setName(principal.getName());
        }
        model.addAttribute("chats", cht);
        return "chatRoom";
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "id", required = false) Long id, Principal principal, Model model) {
        var cht = userService.findByUsername(principal.getName()).getChat();
        for (var ch : cht
        ) {
            ch.setName(principal.getName());
        }
        model.addAttribute("chats", cht);
        model.addAttribute("recipient",
                chatService.findById(id).get().getUsers().stream()
                        .filter(x -> !(x.getUsername().equals(principal.getName())))
                        .collect(Collectors.toList()).get(0).getUsername());
        model.addAttribute("chat_id", id);
        var messages = chatService.findById(id).get().getSortMessage();
        for (var mes : messages) {
            if (!mes.getSender().equals(principal.getName())) {
                mes.setMessageSide("left");
            }
        }
        model.addAttribute("mess", messages);

        return "chat";
    }

    @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public ResponseMessage getPrivateMessage(final Message message,
                                             final Principal principal) throws InterruptedException, ParseException {
        Thread.sleep(1);
        Message newMessage = new Message(message.getMessageContent(), principal.getName(),
                message.getRecipient(), message.getMessageSide(), message.getChat_id());
        service.notifyUser(newMessage);
        var chat = chatService.findById(Long.parseLong(message.getChat_id())).get();
        chat.addMessage(newMessage);
        chatService.save(chat);
        return new ResponseMessage(principal.getName() + " " + HtmlUtils.htmlEscape(message.getMessageContent()),
                HtmlUtils.htmlEscape(message.getMessageSide()),
                HtmlUtils.htmlEscape(newMessage.getSender()),
                HtmlUtils.htmlEscape(newMessage.getStrTime()),
                HtmlUtils.htmlEscape(newMessage.getChat_id())
        );
    }
}
