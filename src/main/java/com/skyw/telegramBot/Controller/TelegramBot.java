package com.skyw.telegramBot.Controller;

import com.skyw.telegramBot.Model.User;
import com.skyw.telegramBot.Repository.UserRepository;
import com.skyw.telegramBot.Service.MatchService;
import com.skyw.telegramBot.Utils.InlineKeyBoardFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.*;
import org.slf4j.Logger;
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    String botToken;

    @Value("${telegram.bot.username}")
    String botUsername;

    Map<Long, Boolean> waitingForLastName = new HashMap<>();
    Map<Long, Boolean> waitingForPhoto = new HashMap<>();

    private final UserRepository userRepository;
    private final InlineKeyBoardFactory inlineKeyBoardFactory;
    private final MatchService matchService;
    private final String TEXT = "You are not registered, please enter /start";
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    public TelegramBot(UserRepository userRepository, InlineKeyBoardFactory inlineKeyBoardFactory,
                       MatchService matchService) {
        this.userRepository = userRepository;
        this.inlineKeyBoardFactory =inlineKeyBoardFactory;
        this.matchService = matchService;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
            return;
        }

        Message originalMessage = update.getMessage();
        Long chatId = originalMessage.getChatId();

        if (originalMessage.hasPhoto() && waitingForPhoto.getOrDefault(chatId, false)) {
            String fileId = originalMessage.getPhoto().get(originalMessage.getPhoto().
                            size() - 1).getFileId();

            User user = userRepository.findById(chatId).orElse(null);
            if (user != null) {
                user.setPhotoFileId(fileId);
                userRepository.save(user);
                sendMessage(chatId, "Photo successfully add! Now you can enter /view");
            }
            waitingForPhoto.put(chatId, false);
        }

        if (originalMessage.hasText()) {
            String text = originalMessage.getText();
            if (waitingForLastName.getOrDefault(chatId, false)) {
                User user = userRepository.findById(chatId).orElse(null);

                if (user != null) {
                    user.setLastName(text);
                    userRepository.save(user);
                    sendMessage(chatId, "Your last name saved! Now, send the photo:");
                }
                waitingForLastName.put(chatId, false);
                waitingForPhoto.put(chatId, true);

                return;
            }

            allCommands(chatId, text, originalMessage);
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        if (data.startsWith("like_")) {
            Long targetUserId = Long.parseLong(data.replace("like_", ""));
            boolean isMatch = matchService.handleLike(chatId, targetUserId);

            User target = userRepository.findById(targetUserId).orElse(null);
            User liker = userRepository.findById(chatId).orElse(null);

            if (isMatch) {
                sendMessage(chatId, "🎉 It's a match with " + target.getUsername() + "!");
                sendMessage(targetUserId, "🎉 It's a match with " + liker.getUsername() + "!");
            } else {
                sendMessage(chatId, "\uD83D\uDC4D");
                sendMessage(targetUserId, "Someone is interested in you! Check this profile: ");
                sendProfileWithButtons(targetUserId, liker);

                showNextProfile(chatId);
            }
        }

        if (data.startsWith("dislike_")) {
            sendMessage(chatId, "\uD83D\uDC4E");
            showNextProfile(chatId);
        }


         try {
            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setChatId(chatId.toString());
            editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());

            InlineKeyboardMarkup emptyKeyBoard = new InlineKeyboardMarkup();
            emptyKeyBoard.setKeyboard(Collections.emptyList());
            editMessageReplyMarkup.setReplyMarkup(emptyKeyBoard);

            execute(editMessageReplyMarkup);
        } catch (Exception e) {
            logger.error("Error while editing message reply markup", e);
        }
    }

    private void allCommands(Long chatId, String text, Message originalMessage) {
        switch (text) {
            case "/start" -> {
                startCommand(chatId, "@" + originalMessage.getFrom().getUserName(),
                        originalMessage.getFrom().getFirstName());
            }


            case "/delete" -> deleteCommand(chatId);
            case "/view" -> viewCommand(chatId);
            case "/myProfile" -> myProfile(chatId);
            default -> sendMessage(chatId, "Unknown command, please use /start or /delete");
        }
    }

    private void startCommand(Long chatId, String username, String firstName) {
        if (userRepository.existsById(chatId)) {
            sendMessage(chatId, "Hello again, " + firstName + ".\nLet's start looking at the profiles /view");
        } else {
            sendMessage(chatId, "Hello, nice to meet you!" + "\nPlease, enter your lastname: ");
            waitingForLastName.put(chatId, true);

            User user = new User(chatId, username, firstName, "", "");
            userRepository.save(user);
        }
    }

    public void deleteCommand(Long chatId) {
        if (userRepository.existsById(chatId)) {
            userRepository.deleteById(chatId);
            sendMessage(chatId,"Your account successfully deleted.");
        } else {
            sendMessage(chatId, TEXT);
        }
    }

    public void viewCommand(Long chatId) {
        if (!userRepository.existsById(chatId)) {
            sendMessage(chatId, TEXT);
            return;
        }
        showNextProfile(chatId);
    }

    private void myProfile(Long chatId) {
        if (!userRepository.existsById(chatId)) {
            sendMessage(chatId, TEXT);
            return;
        }

        User user = userRepository.findById(chatId).orElse(null);
        if (user != null && user.getPhotoFileId() != null && !user.getPhotoFileId().isEmpty()) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setPhoto(new InputFile(user.getPhotoFileId()));

            String caption = "Name: " + user.getFirstName() + "\n" +
                    "Last name: " + user.getLastName() + "\n";
            sendPhoto.setCaption(caption);

            try {
                execute(sendPhoto);
            } catch (Exception e) {
                logger.error("Error sending photo", e);
            }
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(text);

        try {
            execute(response);
        } catch (Exception e) {
            logger.error("Error sending message", e);
        }
    }


    private void sendProfileWithButtons(Long chatId, User user) {
        String text = "Name: " + user.getFirstName() + "\n"
                + "Last Name: " + user.getLastName() + "\n";

        SendPhoto message = new SendPhoto();
        message.setChatId(chatId.toString());

        if (user.getPhotoFileId() != null && !user.getPhotoFileId().isEmpty()) {
            message.setPhoto(new InputFile(user.getPhotoFileId()));
        } else {
            String textNoPhoto = "Name: " + user.getFirstName() + "\n"
                    + "Last name: " + user.getLastName() + "\n"
                    + "\"User don't have photo\"";

            sendMessage(chatId, textNoPhoto);
            return;
        }

        message.setCaption(text);
        message.setReplyMarkup(inlineKeyBoardFactory.createLikeDislikeKeyBoard(user.getChatId()));

        try {
            execute(message);
        } catch (Exception e) {
            logger.error("Error sending profile with inline buttons", e);
        }
    }

    private void showNextProfile(Long chatId) {
        User nextProfile = matchService.getNextProfile(chatId);
        if (nextProfile != null) {
            sendProfileWithButtons(chatId, nextProfile);
        } else {
            sendMessage(chatId, "No more profiles available");
        }
    }
}