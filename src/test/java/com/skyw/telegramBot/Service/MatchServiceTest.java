package com.skyw.telegramBot.Service;

import com.skyw.telegramBot.Model.User;
import com.skyw.telegramBot.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MatchServiceTest {

    private UserRepository userRepository;
    private MatchService matchService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        matchService = new MatchService(userRepository);
    }

    @Test
    void handleLike_shouldReturnTrue_whenMutualLike() {
        Long user1 = 1L;
        Long user2 = 2L;

        matchService.handleLike(user1, user2);
        boolean isMatch = matchService.handleLike(user2, user1);

        assertTrue(isMatch, "Should return true when there is a mutual like");
    }

    @Test
    void getNextProfile_shouldReturnNextUnshownUser() {
        Long chatId = 1L;

        User user1 = new User(1L, "user1", "First1", "Last1", "");
        User user2 = new User(2L, "user2", "First2", "Last2", "");
        User user3 = new User(3L, "user3", "First3", "Last3", "");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        User next = matchService.getNextProfile(chatId);
        assertNotNull(next);
        assertNotEquals(chatId, next.getChatId());

        User next2 = matchService.getNextProfile(chatId);
        assertNotEquals(next.getChatId(), next2.getChatId());
    }

    @Test
    void getNextProfile_shouldReturnNull_whenAllShown() {
        Long chatId = 1L;
        User user2 = new User(2L, "user2", "First2", "Last2", "");

        when(userRepository.findAll()).thenReturn(List.of(user2));

        matchService.getNextProfile(chatId);
        User next = matchService.getNextProfile(chatId);

        assertNull(next, "Should return null if no more profiles");
    }
}
