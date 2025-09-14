package com.skyw.telegramBot.Validation;

import com.skyw.telegramBot.Controller.TelegramBot;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class BotValidator {

    private final TelegramBot bot;
    private static final Logger logger = LoggerFactory.getLogger(BotValidator.class);

    public BotValidator(TelegramBot bot) {
        this.bot = bot;
    }

    @PostConstruct
    public void validateBot() {
        logger.info("Checking Telegram bot availability...");
        GetMe getMe = new GetMe();
        try {
            User user = bot.execute(getMe);
            logger.info("Bot is available");
            logger.info("Bot username: @" + user.getUserName());
            logger.info("Bot id: " + user.getId());
        } catch (TelegramApiException e) {
            logger.error("Bot token is invalid or bot is not available ", e);
        }
    }
}
