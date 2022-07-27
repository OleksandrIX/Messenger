package com.obm.ukrainiansmessenger.repository;

import com.obm.ukrainiansmessenger.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

}
