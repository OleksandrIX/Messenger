package com.obm.ukrainiansmessenger.models;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "usr")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username, password;
    private boolean active;
    private String email, activationCode;


    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @ManyToMany( cascade=CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "usersList")
    @ToString.Exclude
    private List<Chat> chat ;

    public Integer getChatWith(User user) {
        List<Chat> chatList = this.getChat();
        List<Chat> chatList1 = user.getChat();
        for (Chat chat : chatList1) {
            if (chatList.contains(chat)) {
                return Math.toIntExact(chat.getId());
            }
        }
        return -1;
    }

//    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//    @ElementCollection(targetClass = Chat.class,fetch = FetchType.EAGER)
//    @CollectionTable(name = "chat_users",joinColumns = @JoinColumn(name="id_user"))
//    private List<Chat> chats;


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        return authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}