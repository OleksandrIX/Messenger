package com.obm.ukrainiansmessenger.repository;

import com.obm.ukrainiansmessenger.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
