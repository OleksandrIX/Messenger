package com.obm.ukrainiansmessenger.servise;

import com.obm.ukrainiansmessenger.models.Role;
import com.obm.ukrainiansmessenger.models.User;
import com.obm.ukrainiansmessenger.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MailSender mailSender;

    public UserService(UserRepository userRepository, MailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user.getActivationCode()!=null) {
            user.setActive(false);
        }
        return user;
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
                "Ласкаво просимо до Українського Месенджера. Ваша код активації%s.",
                user.getActivationCode()
        );
        mailSender.send(user.getEmail(), "Код активації", message);
    }

    public boolean searchEmailInDB(String email) {
        User userFromDb = userRepository.findByEmail(email);
        if (userFromDb!=null){
            userFromDb.setActive(true);
            StringBuilder code = new StringBuilder();
            Random random = new Random();
            for(int i = 0; i<6;i++){
                code.append(random.nextInt(10));
            }
            userFromDb.setActivationCode(String.valueOf(code));
            String message = String.format(
                     "Ласкаво просимо до Українського Месенджера. Ваша код активації%s.",
                    userFromDb.getActivationCode()
            );
            mailSender.send(userFromDb.getEmail(), "Код активації", message);
            userRepository.save(userFromDb);
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
        user.setPassword(password);

        userRepository.save(user);
        return true;
    }
}
