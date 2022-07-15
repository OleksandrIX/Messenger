package com.obm.ukrainiansmessenger.servise;

import com.obm.ukrainiansmessenger.models.Role;
import com.obm.ukrainiansmessenger.models.User;
import com.obm.ukrainiansmessenger.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
            return null;
        }
        return user;
    }

    public boolean addUser(User user){
        User userFromDb = userRepository.findByUsername(user.getUsername());
        if (userFromDb != null){
            return false;
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(user);
        if((user.getEmail()!=null)){
            String message = String.format(
                    "Привіт, %s\n" +
                            "Ласкаво просимо до Українського Месенджера. Будь ласка, перейдіть за посиланням: http://localhost:8080/activate/%s для підтвердження вашої пошти.",
                    user.getUsername(),
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Код активації", message);
        }

        return true;
    }

    public MailSender getMailSender() {
        return mailSender;
    }

    public boolean activeUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if(user==null){
            return false;
        }
        user.setActivationCode(null);
        userRepository.save(user);

        return true;
    }
}
