package com.skyw.telegramBot.Controller;


import com.skyw.telegramBot.Model.User;
import com.skyw.telegramBot.Repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserRepository userRepository;

    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{chatId}")
    public User getUser(@PathVariable Long chatId) {
        return userRepository.findById(chatId).orElse(null);
    }


    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping("/{chatId}")
    public User updateUser(@PathVariable Long chatId, @RequestBody User updateUser) {
        return userRepository.findById(chatId)
                .map(user -> {
                    user.setFirstName(updateUser.getFirstName());
                    user.setLastName(updateUser.getLastName());
                    user.setPhotoFileId(updateUser.getPhotoFileId());
                    return userRepository.save(user);
                })
                .orElse(null);
    }


    @DeleteMapping("/{chatId}")
    public void deleteUser(@PathVariable Long chatId) {
        userRepository.deleteById(chatId);
    }
}
