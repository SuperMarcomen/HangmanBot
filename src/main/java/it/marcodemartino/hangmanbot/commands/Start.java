package it.marcodemartino.hangmanbot.commands;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.commands.Command;
import io.github.ageofwar.telejam.commands.CommandHandler;
import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton;
import io.github.ageofwar.telejam.inline.InlineKeyboardButton;
import io.github.ageofwar.telejam.messages.TextMessage;
import io.github.ageofwar.telejam.methods.SendMessage;
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup;
import it.marcodemartino.hangmanbot.languages.Localization;

import java.io.IOException;

public class Start implements CommandHandler {

    private final Localization localization;
    private final Bot bot;

    public Start(Localization localization, Bot bot) {
        this.localization = localization;
        this.bot = bot;
    }

    public void onCommand(Command command, TextMessage textMessage) throws IOException {
        InlineKeyboardButton[] keyboardButtons = {new CallbackDataInlineKeyboardButton(localization.getString("supported_languages_button", textMessage.getSender().getLocale()), "supported_languages")};
        SendMessage sendMessage = new SendMessage()
                .text(localization.getString("start_message", textMessage.getSender().getLocale()))
                .replyMarkup(new InlineKeyboardMarkup(keyboardButtons))
                .chat(textMessage.getChat());
        bot.execute(sendMessage);
    }

}
