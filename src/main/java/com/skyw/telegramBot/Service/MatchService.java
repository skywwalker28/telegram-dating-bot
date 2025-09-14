package com.skyw.telegramBot.Service;

import com.skyw.telegramBot.Model.User;
import com.skyw.telegramBot.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatchService {

    private final UserRepository userRepository;
    private final Map<Long, Set<Long>> likesMap = new HashMap<>();
    private final Map<Long, Set<Long>> showProfiles = new HashMap<>();

    public MatchService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean handleLike(Long fromUserId, Long targetUserId) {
        likesMap.putIfAbsent(targetUserId, new HashSet<>());
        Set<Long> targetLikes = likesMap.getOrDefault(fromUserId, new HashSet<>());

        boolean isMatch = targetLikes.contains(targetUserId);
        likesMap.get(targetUserId).add(fromUserId);

        return isMatch;
    }


    public User getNextProfile(Long chatId) {
        List<User> users = userRepository.findAll();

        showProfiles.putIfAbsent(chatId, new HashSet<>());
        Set<Long> shown = showProfiles.get(chatId);

        for (User current : users) {
            if (!current.getChatId().equals(chatId) && !shown.contains(current.getChatId())) {
                shown.add(current.getChatId());
                return current;
            }
        }




        return null;
    }
}
