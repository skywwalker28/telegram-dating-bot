package com.skyw.telegramBot.Utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class InlineKeyBoardFactory {
    public InlineKeyboardMarkup createLikeDislikeKeyBoard(Long userId) {
        InlineKeyboardButton likeButton = new InlineKeyboardButton("\uD83D\uDC4D Like");
        likeButton.setCallbackData("like_" + userId);

        InlineKeyboardButton dislikeButton = new InlineKeyboardButton("\uD83D\uDC4E Dislike");
        dislikeButton.setCallbackData("dislike_" + userId);

        List<InlineKeyboardButton> row = List.of(likeButton, dislikeButton);
        List<List<InlineKeyboardButton>> keyboard = List.of(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}
