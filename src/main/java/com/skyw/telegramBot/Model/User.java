package com.skyw.telegramBot.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bot_users")
public class User {

    @Id
    private Long chatId;

    private String username;
    private String firstName;
    private String lastName;
    private String photoFileId;


    public User() {}

    public User(Long chatId, String username, String firstName, String lastName, String photoFieldId) {
        this.chatId = chatId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoFileId = photoFieldId;
    }

    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) {this.chatId = chatId;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getPhotoFileId() {return photoFileId;}
    public void setPhotoFileId(String photoFileId) {this.photoFileId = photoFileId;}
}
