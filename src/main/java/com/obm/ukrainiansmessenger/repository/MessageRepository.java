package com.obm.ukrainiansmessenger.repository;

import com.obm.ukrainiansmessenger.models.HelloMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<HelloMessage, Long> {

}
