package com.obm.ukrainiansmessenger.repository;

import com.obm.ukrainiansmessenger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findAll();
    User findByActivationCode(String code);

    User findByEmail(String email);
}
