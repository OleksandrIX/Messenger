package com.obm.ukrainiansmessenger.servise;

import com.obm.ukrainiansmessenger.models.Role;
import com.obm.ukrainiansmessenger.models.User;
import com.obm.ukrainiansmessenger.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, MailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public void addUser(User user){
        User userFromDb = userRepository.findByEmail(user.getEmail());
        if (userFromDb != null){
            return;
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i<6;i++){
            code.append(random.nextInt(10));
        }
        user.setActivationCode(String.valueOf(code));
        userRepository.save(user);
        String message = String.format(
                "Ласкаво просимо до Українського Месенджера. Ваша код активації %s.",
                user.getActivationCode()
        );
        mailSender.send(user.getEmail(), "Код активації", message);
    }

    public boolean searchEmailInDB(String email) {
        User user = userRepository.findByEmail(email);
        if (user!=null){
            user.setActive(true);
            StringBuilder code = new StringBuilder();
            Random random = new Random();
            for(int i = 0; i<6;i++){
                code.append(random.nextInt(10));
            }
            user.setActivationCode(String.valueOf(code));
            userRepository.save(user);
            String message = String.format(
                     "Ласкаво просимо до Українського Месенджера. Ваша код активації %s.",
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Код активації", message);
        }

        return true;
    }

    public User activeUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if(user==null){
            return null;
        }

        user.setActivationCode(null);
        userRepository.save(user);

        return user;
    }

    public boolean save(String username, String password, User user) {
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
        return true;
    }

    public User findByUsername(String name) {
        return userRepository.findByUsername(name);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
