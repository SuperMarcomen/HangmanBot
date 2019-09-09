package it.marcodemartino.hangmanbot.callback;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.callbacks.CallbackDataHandler;
import io.github.ageofwar.telejam.callbacks.CallbackQuery;
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery;
import io.github.ageofwar.telejam.methods.EditMessageText;
import io.github.ageofwar.telejam.text.Text;
import it.marcodemartino.hangmanbot.languages.Localization;
import it.marcodemartino.hangmanbot.languages.LocalizedWord;
import it.marcodemartino.hangmanbot.logic.GuessResult;
import it.marcodemartino.hangmanbot.logic.Hangman;
import it.marcodemartino.hangmanbot.stats.StatsManager;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class LetterClick implements CallbackDataHandler {

    private final Localization localization;
    private final LocalizedWord localizedWord;
    private final Bot bot;
    private StatsManager statsManager;
    private Map<String, Hangman> matches;

    public LetterClick(Localization localization, LocalizedWord localizedWord, Bot bot, StatsManager statsManagers, Map<String, Hangman> matches) {
        this.localization = localization;
        this.localizedWord = localizedWord;
        this.bot = bot;
        this.statsManager = statsManagers;
        this.matches = matches;
    }

    @Override
    public void onCallbackData(CallbackQuery callbackQuery, String s, String s1) throws Throwable {
        if(!s.startsWith("letter")) return;
        if(!callbackQuery.getInlineMessageId().isPresent()) return;

        Hangman hangman = matches.get(callbackQuery.getInlineMessageId().get());
        Locale userLocale = localization.getUserLocale(callbackQuery.getSender());

        /* Match already ended or not found*/
        if(hangman == null) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                    .callbackQuery(callbackQuery)
                    .text(localization.getString("match_not_found", userLocale))
                    .showAlert(true);
            bot.execute(answerCallbackQuery);
            return;
        }

        Locale matchLocale = hangman.getLocale();

        if (!hangman.isMultiplayer() && hangman.getSenderId() != callbackQuery.getSender().getId()) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                    .callbackQuery(callbackQuery)
                    .text(localization.getString("match_not_multiplayer", userLocale))
                    .showAlert(true);
            bot.execute(answerCallbackQuery);
            return;
        }

        if (hangman.isCustomMatch() && hangman.getSenderId() == callbackQuery.getSender().getId()) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                    .callbackQuery(callbackQuery)
                    .text(localization.getString("cant_play_your_custom_match", userLocale))
                    .showAlert(true);
            bot.execute(answerCallbackQuery);
            return;
        }

        String letter = s.replace("letter_", "");
        GuessResult guessResult = hangman.guessLetter(letter);

        /* Preparing the messages */
        String string = localization.getString("general_match_message", matchLocale);
        string = localization.handlePlaceholder(string, hangman);
        StringBuilder message = new StringBuilder(string);

        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                .callbackQuery(callbackQuery);

        EditMessageText editMessageText = new EditMessageText()
                .inlineMessage(callbackQuery.getInlineMessageId().get());

        /* Increasing stats */
        statsManager.increaseStats(callbackQuery.getSender(), guessResult);

        GuessResult status = hangman.getStatus();
        /* Match is ended: updating the messages */
        if (status != null) {
            answerCallbackQuery.text(getResponseMessage(status, userLocale));
            if (status.equals(GuessResult.MATCH_WIN))
                message.append(localization.getString("win_edit_message", matchLocale));
            else
                message.append(localization.handlePlaceholder(localization.getString("lose_edit_message", matchLocale), hangman));
            matches.remove(callbackQuery.getInlineMessageId().get());
        } else {
            editMessageText
                    .replyMarkup(hangman.generateKeyboard());
            answerCallbackQuery
                    .text(getResponseMessage(guessResult, userLocale));
            message.append(localization.getString("lag_alert", matchLocale));
        }

        editMessageText.text(Text.parseHtml(message.toString()));

        /* Alerting match status */
        if (!guessResult.equals(GuessResult.LETTER_ALREADY_SAID))
            bot.execute(editMessageText);
        bot.execute(answerCallbackQuery);

    }

    private String getResponseMessage(GuessResult guessResult, Locale locale) throws IOException {
        switch (guessResult) {
            case LETTER_ALREADY_SAID:
                return localization.getString("alert_letter_already_said", locale);
            case LETTER_WRONG:
                return localization.getString("alert_wrong_letter", locale);
            case LETTER_GUESSED:
                return localization.getString("alert_guessed_letter", locale);
            case MATCH_LOSE:
                return localization.getString("alert_match_lose", locale);
            case MATCH_WIN:
                return localization.getString("alert_match_win", locale);
        }
        return localization.getString("alert_error", locale);
    }

}
