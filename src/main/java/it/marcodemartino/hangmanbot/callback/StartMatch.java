package it.marcodemartino.hangmanbot.callback;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.callbacks.CallbackDataHandler;
import io.github.ageofwar.telejam.callbacks.CallbackQuery;
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery;
import io.github.ageofwar.telejam.methods.EditMessageText;
import io.github.ageofwar.telejam.text.Text;
import it.marcodemartino.hangmanbot.languages.Localization;
import it.marcodemartino.hangmanbot.logic.Hangman;

import java.util.Locale;
import java.util.Map;

public class StartMatch implements CallbackDataHandler {

    private final Localization localization;
    private final Bot bot;
    private Map<String, Hangman> matches;

    public StartMatch(Localization localization, Bot bot, Map<String, Hangman> matches) {
        this.localization = localization;
        this.bot = bot;
        this.matches = matches;
    }

    @Override
    public void onCallbackData(CallbackQuery callbackQuery, String s, String s1) throws Throwable {
        if (!callbackQuery.getInlineMessageId().isPresent()) return;
        if (!callbackQuery.getData().isPresent()) return;
        if (!callbackQuery.getData().get().startsWith("player")) return;

        Hangman hangman = matches.get(callbackQuery.getInlineMessageId().get());
        Locale locale = callbackQuery.getSender().getLocale();

        if (callbackQuery.getData().get().contains("2"))
            hangman.setMultiplayer(true);

        /* Match already ended or not found*/
        if (hangman == null) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                    .callbackQuery(callbackQuery)
                    .text(localization.getString("match_not_found", locale))
                    .showAlert(true);
            bot.execute(answerCallbackQuery);
            return;
        }

        if (hangman.getSenderId() != callbackQuery.getSender().getId()) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                    .callbackQuery(callbackQuery)
                    .text(localization.getString("cant_choose_play_mode", locale))
                    .showAlert(true);
            bot.execute(answerCallbackQuery);
            return;
        }

        String message = localization.getString("generalMatchMessage", locale);
        message = localization.handlePlaceholder(message, hangman);

        EditMessageText editMessageText = new EditMessageText()
                .inlineMessage(callbackQuery.getInlineMessageId().get())
                .replyMarkup(hangman.generateKeyboard())
                .text(Text.parseHtml(message));

        bot.execute(editMessageText);
    }

}
