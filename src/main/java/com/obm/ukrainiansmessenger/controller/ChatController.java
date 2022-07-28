package com.obm.ukrainiansmessenger.controller;

import com.obm.ukrainiansmessenger.models.Chat;
import com.obm.ukrainiansmessenger.models.Message;
import com.obm.ukrainiansmessenger.models.ResponseMessage;
import com.obm.ukrainiansmessenger.models.User;
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

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "id", required = false) Long id, Principal principal, Model model) {
        List<Chat> chatList = userService.findByUsername(principal.getName()).getChat();
        List<User> userList = userService.findAll();
        userList.remove(userService.findByUsername(principal.getName()));
        User user = userService.findByUsername(principal.getName());
        for (Chat chat : chatList) {
            chat.setName(principal.getName());
        }
        if (id!=null) {
            model.addAttribute("chats", chatList);
            String recipient = chatService.findById(id).getUsers().stream()
                    .filter(x -> !(x.getUsername().equals(principal.getName())))
                    .toList().get(0).getUsername();

            model.addAttribute("recipient",recipient);
            model.addAttribute("chat_id", id);
            var messages = chatService.findById(id).getSortMessage();
            for (var mes : messages) {
                if (!mes.getSender().equals(principal.getName())) {
                    mes.setMessageSide("left");
                }
            }
            model.addAttribute("mess", messages);
        } else {
            model.addAttribute("chat_id", id);
            model.addAttribute("chats", chatList);
            model.addAttribute("users", userList);
            model.addAttribute("user", user);
        }

        return "chat";
    }

    @PostMapping("/mes")
    public String createChat(@RequestParam("recipientId") Long id, Principal principal, Model model, final @RequestParam(value = "image", required = false) MultipartFile file) {
        User user = userService.findByUsername(principal.getName());
        User friend = userService.findById(id);
        Chat chat = new Chat();

        chat.setUsers(user);
        chat.setUsers(friend);
        chat.setName(user.getUsername() + "-" + friend.getUsername());
        chatService.save(chat);
        model.addAttribute("chat", chat);
        model.addAttribute("user", user);
        return "redirect:/chat?id="+chat.getId();
    }

    @PostMapping("/open")
    public String openChar(@RequestParam("userId") Long id, Model model, Principal principal) {
        User user = userService.findById(id);
        User authUser = userService.findByUsername(principal.getName());
        List<Chat> chatList = user.getChat();
        Chat chat = chatList.stream().filter(cht->cht.contains(authUser)).findAny().get();
        model.addAttribute("chat", chat);
        model.addAttribute("user", user);
        return "redirect:/chat?id="+chat.getId();
    }

    @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public ResponseMessage getPrivateMessage(final Message message,
                                             final Principal principal) throws InterruptedException, ParseException {
        Thread.sleep(0,100);
        Message newMessage = new Message(message.getMessageContent(), principal.getName(),
                message.getRecipient(), message.getMessageSide(), message.getChat_id());
        service.notifyUser(newMessage);
        var chat = chatService.findById(Long.parseLong(message.getChat_id()));
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
