package com.obm.ukrainiansmessenger.models;


import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String messageContent;
    private String sender;
    private String recipient;
    private String messageSide;
    private Date time;
    private String chat_id;




    public Message(){}

    public Message(String messageContent, String messageSide) {
        this.messageContent = messageContent;
        this.messageSide = messageSide;
    }

    public Message(String messageContent, String sender, String recipient, String messageSide) {
        this.messageContent = messageContent;
        this.sender = sender;
        this.recipient = recipient;
        this.messageSide = messageSide;
        this.time = new Date();
    }
    public Message(String messageContent, String sender, String recipient, String messageSide,String chat_id) {
        this.messageContent = messageContent;
        this.sender = sender;
        this.recipient = recipient;
        this.messageSide = messageSide;
        this.time = new Date();
        this.chat_id=chat_id;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Date getTime() {
        return time;
    }
    public String getStrTime() throws ParseException {
        SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm");
        return formatDate.format(time);
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageSide() {
        return messageSide;
    }

    public void setMessageSide(String messageSide) {
        this.messageSide = messageSide;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
