package com.obm.ukrainiansmessenger.repository;

import com.obm.ukrainiansmessenger.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
