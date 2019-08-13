package it.marcodemartino.hangmanbot.callback;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.callbacks.CallbackDataHandler;
import io.github.ageofwar.telejam.callbacks.CallbackQuery;
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery;
import io.github.ageofwar.telejam.methods.EditMessageText;
import io.github.ageofwar.telejam.methods.SendMessage;
import io.github.ageofwar.telejam.text.Text;
import it.marcodemartino.hangmanbot.inline.InlineResult;
import it.marcodemartino.hangmanbot.logic.GuessResult;
import it.marcodemartino.hangmanbot.logic.Hangman;
import it.marcodemartino.hangmanbot.stats.StatsManager;

import java.util.Map;

public class LetterClick implements CallbackDataHandler {

    private final Bot bot;
    private StatsManager statsManager;
    private Map<String, Hangman> matches;
    private final String generalMessage;

    public LetterClick(Bot bot, StatsManager statsManagers, Map<String, Hangman> matches, String generalMessage) {
        this.bot = bot;
        this.statsManager = statsManagers;
        this.matches = matches;
        this.generalMessage = generalMessage;
    }

    @Override
    public void onCallbackData(CallbackQuery callbackQuery, String s, String s1) throws Throwable {
        if(!s.startsWith("letter")) return;
        if(!callbackQuery.getInlineMessageId().isPresent()) return;

        Hangman hangman = matches.get(callbackQuery.getInlineMessageId().get());

        /* Match already ended or not found*/
        if(hangman == null) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                    .callbackQuery(callbackQuery)
                    .text("Questa partita è già finita o è stata avviata prima che il bot venisse riavviato");
            bot.execute(answerCallbackQuery);
            return;
        }

        if (!hangman.isMultiplayer() && hangman.getSenderId() != callbackQuery.getSender().getId()) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                    .callbackQuery(callbackQuery)
                    .text("Può giocare soltato chi ha inviato il messaggio!");
            bot.execute(answerCallbackQuery);
            return;
        }

        char c = s.replace("letter_", "").charAt(0);
        GuessResult guessResult = hangman.guessLetter(c);

        /* Preparing the messages */
        StringBuilder message = new StringBuilder(handlePlaceholder(generalMessage, hangman));

        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                .callbackQuery(callbackQuery)
                .text(getResponseMessage(guessResult));

        EditMessageText editMessageText = new EditMessageText()
                .inlineMessage(callbackQuery.getInlineMessageId().get());

        /* Increasing stats */
        statsManager.increaseStats(callbackQuery.getSender(), guessResult);

        GuessResult status = hangman.getStatus();
        /* Match is ended: updating the messages */
        if (status != null) {
            answerCallbackQuery.text(getResponseMessage(status));
            if (status.equals(GuessResult.MATCH_WIN)) message.append("\n\n🎊 Hai vinto!");
            else
                message.append("\n\n⚠ <b>Hai perso!</b> La <b>parola</b> da indovinare era: ").append(hangman.getWord());
            matches.remove(callbackQuery.getInlineMessageId().get());
        } else
            editMessageText.replyMarkup(hangman.generateKeyboard());

        editMessageText.text(Text.parseHtml(message.toString()));

        /* Alerting match status */
        bot.execute(editMessageText);
        bot.execute(answerCallbackQuery);

        if (matches.isEmpty() && !InlineResult.isStartMatch()) {
            SendMessage sendMessage = new SendMessage()
                    .chat(229856560L)
                    .text("Bot spento con successo!");
            bot.execute(sendMessage);
        }
    }

    private String getResponseMessage(GuessResult guessResult) {
        switch (guessResult) {
            case LETTER_ALREADY_SAID:
                return "Lettera già detta";
            case LETTER_WRONG:
                return "Lettera sbagliata";
            case LETTER_GUESSED:
                return "Hai indovinato una lettera";
            case MATCH_LOSE:
                return "Hai perso la partita";
            case MATCH_WIN:
                return "Hai vinto la partita";
        }
        return "C'è stato un errore";
    }

    private String handlePlaceholder(String string, Hangman hangman) {
        string = string.replace("word_state", hangman.getCurrentState());
        string = string.replace("current_errors", String.valueOf(hangman.getErrors()));
        string = string.replace("max_errors", String.valueOf(hangman.getMaxErrors()));
        string = string.replace("category", hangman.getCategory());
        return string;
    }


}
