package com.obm.ukrainiansmessenger.repository;

import com.obm.ukrainiansmessenger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
