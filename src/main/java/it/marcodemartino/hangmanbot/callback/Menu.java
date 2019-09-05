package it.marcodemartino.hangmanbot.callback;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.callbacks.CallbackDataHandler;
import io.github.ageofwar.telejam.callbacks.CallbackQuery;
import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton;
import io.github.ageofwar.telejam.inline.InlineKeyboardButton;
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery;
import io.github.ageofwar.telejam.methods.EditMessageText;
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup;
import io.github.ageofwar.telejam.text.Text;
import io.github.ageofwar.telejam.users.User;
import it.marcodemartino.hangmanbot.HangmanBot;
import it.marcodemartino.hangmanbot.languages.Localization;
import it.marcodemartino.hangmanbot.stats.DatabaseManager;
import it.marcodemartino.hangmanbot.stats.StatsManager;
import it.marcodemartino.hangmanbot.stats.UserStats;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@AllArgsConstructor
public class Menu implements CallbackDataHandler {

    private final Bot bot;
    private final Localization localization;
    private final StatsManager statsManager;
    private final DatabaseManager database;

    @Override
    public void onCallbackData(CallbackQuery callbackQuery, String s, String s1) throws IOException {
        if (!callbackQuery.getData().isPresent()) return;
        User user = callbackQuery.getSender();

        if (callbackQuery.getData().get().equals("choose_language")) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (Locale locale : HangmanBot.SUPPORTED_LANGUAGES) {
                CallbackDataInlineKeyboardButton button = new CallbackDataInlineKeyboardButton(localization.getString("language", locale), "choosen_language_" + locale.getLanguage());
                buttons.add(button);
            }

            InlineKeyboardButton[][] buttonArray = InlineKeyboardMarkup.fromColumns(2, buttons).getInlineKeyboard();
            InlineKeyboardButton[][] newButtonArray = Arrays.copyOf(buttonArray, buttonArray.length + 1);

            newButtonArray[buttonArray.length] = new InlineKeyboardButton[]{new CallbackDataInlineKeyboardButton(localization.getString("menu_back", user), "menu_back")};

            EditMessageText editMessageText = new EditMessageText()
                    .callbackQuery(callbackQuery)
                    .text(localization.getString("change_language_message", user))
                    .replyMarkup(new InlineKeyboardMarkup(newButtonArray));
            bot.execute(editMessageText);

        } else if (callbackQuery.getData().get().startsWith("choosen_language")) {

            String language = callbackQuery.getData().get().replace("choosen_language_", "");
            database.updateUserLanguage(callbackQuery.getSender().getId(), language);

            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                    .callbackQuery(callbackQuery)
                    .text(localization.getString("change_language_done", user));
            bot.execute(answerCallbackQuery);
        } else if (callbackQuery.getData().get().equals("stats")) {

            StringBuilder message = new StringBuilder(localization.getString("stats_message_title", user));

            for (UserStats userStats : statsManager.getBestUsers()) {
                message.append(localization.handlePlaceholder(localization.getString("stats_message_body", user), userStats));
            }

            UserStats userStats = statsManager.getUserStats(user.getId());
            message.append(localization.handlePlaceholder(localization.getString("stats_message_sender", user), userStats));

            CallbackDataInlineKeyboardButton button = new CallbackDataInlineKeyboardButton(localization.getString("menu_back", user), "menu_back");

            EditMessageText editMessageText = new EditMessageText()
                    .callbackQuery(callbackQuery)
                    .text(Text.parseHtml(message.toString()))
                    .replyMarkup(new InlineKeyboardMarkup(button));
            bot.execute(editMessageText);

        }

        if (callbackQuery.getData().get().equals("menu_back") || callbackQuery.getData().get().startsWith("choosen_language")) {

            InlineKeyboardButton[] buttons = {
                    new CallbackDataInlineKeyboardButton(localization.getString("change_language_button", user), "choose_language"),
                    new CallbackDataInlineKeyboardButton(localization.getString("show_statistics", user), "stats"),
            };

            EditMessageText editMessageText = new EditMessageText()
                    .callbackQuery(callbackQuery)
                    .replyMarkup(new InlineKeyboardMarkup(buttons))
                    .text(localization.getString("menu_message", user));

            bot.execute(editMessageText);
        }

    }
}
