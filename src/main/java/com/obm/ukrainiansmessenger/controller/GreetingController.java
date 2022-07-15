package com.obm.ukrainiansmessenger.controller;

import com.obm.ukrainiansmessenger.models.Greeting;
import com.obm.ukrainiansmessenger.models.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;

@Controller
public class GreetingController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message, Principal principal) throws Exception {
        Thread.sleep(1);
        return new Greeting(principal.getName()+" "+ HtmlUtils.htmlEscape(message.getName()));
    }
}

