package com.obm.ukrainiansmessenger.servise;

import com.obm.ukrainiansmessenger.models.Chat;
import com.obm.ukrainiansmessenger.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService  {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void save(Chat chat) {
        chatRepository.save(chat);
    }

    public Chat findById(Long id) {
        return chatRepository.findById(id).get();
    }
}
