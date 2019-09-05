package it.marcodemartino.hangmanbot.commands;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.commands.Command;
import io.github.ageofwar.telejam.commands.CommandHandler;
import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton;
import io.github.ageofwar.telejam.inline.InlineKeyboardButton;
import io.github.ageofwar.telejam.messages.TextMessage;
import io.github.ageofwar.telejam.methods.SendMessage;
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup;
import io.github.ageofwar.telejam.text.Text;
import io.github.ageofwar.telejam.users.User;
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
        User user = textMessage.getSender();
        InlineKeyboardButton[] buttons = {
                new CallbackDataInlineKeyboardButton(localization.getString("change_language_button", user), "choose_language"),
                new CallbackDataInlineKeyboardButton(localization.getString("show_statistics", user), "stats"),
        };

        SendMessage sendMessage = new SendMessage()
                .chat(textMessage.getChat())
                .replyMarkup(new InlineKeyboardMarkup(buttons))
                .text(Text.parseHtml(localization.getString("menu_message", user)));

        bot.execute(sendMessage);

    }

}
