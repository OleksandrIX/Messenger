package com.obm.ukrainiansmessenger.repository;

import com.obm.ukrainiansmessenger.models.ChatRoom;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findChatRoomByChatId(Long id);
}
