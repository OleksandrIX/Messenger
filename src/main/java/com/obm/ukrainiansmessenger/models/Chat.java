package com.obm.ukrainiansmessenger.models;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userName;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "chat_user",
            joinColumns = {@JoinColumn(name = "cht_id")},
            inverseJoinColumns = {@JoinColumn(name = "usr_id")}
    )
    private List<User> usersList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Message> messages;


    public Chat() {
    }


    public void setName(String userName) {
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {

        String name = " ";
        for (var user : usersList
        ) {
            if (!userName.equals(user.getUsername()) && name.equals(" ")) {
                name += user.getUsername() + "";
            } else if (!userName.equals(user.getUsername())) {
                name += user.getUsername() + ", ";
            }
        }
        return name;
    }


    public List<User> getUsers() {
        return usersList;
    }

    public void setUsers(User user) {
        usersList.add(user);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getSortMessage() {

        return messages.stream().sorted(Comparator.comparing(Message::getTime)).collect(Collectors.toList());
    }

    public void addMessage(Message message) {
        this.messages.add(message);

    }

    public boolean contains(User user) {
        if (usersList.contains(user)) {
            return true;
        }
        return false;
    }
}
