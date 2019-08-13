package it.marcodemartino.hangmanbot.callback;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.callbacks.CallbackDataHandler;
import io.github.ageofwar.telejam.callbacks.CallbackQuery;
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery;
import io.github.ageofwar.telejam.methods.EditMessageText;
import io.github.ageofwar.telejam.text.Text;
import it.marcodemartino.hangmanbot.logic.Hangman;
import it.marcodemartino.hangmanbot.stats.StatsManager;

import java.util.Map;

public class StartMatch implements CallbackDataHandler {

    private final Bot bot;
    private final String generalMessage;
    private StatsManager statsManager;
    private Map<String, Hangman> matches;

    public StartMatch(Bot bot, StatsManager statsManager, Map<String, Hangman> matches, String generalMessage) {
        this.bot = bot;
        this.statsManager = statsManager;
        this.matches = matches;
        this.generalMessage = generalMessage;
    }

    @Override
    public void onCallbackData(CallbackQuery callbackQuery, String s, String s1) throws Throwable {
        if (!callbackQuery.getInlineMessageId().isPresent()) return;
        if (!callbackQuery.getData().isPresent()) return;
        if (!callbackQuery.getData().get().startsWith("player")) return;

        Hangman hangman = matches.get(callbackQuery.getInlineMessageId().get());
        if (callbackQuery.getData().get().contains("2"))
            hangman.setMultiplayer(true);

        /* Match already ended or not found*/
        if (hangman == null) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                    .callbackQuery(callbackQuery)
                    .text("Questa partita è già finita o è stata avviata prima che il bot venisse riavviato");
            bot.execute(answerCallbackQuery);
            return;
        }

        if (hangman.getSenderId() != callbackQuery.getSender().getId()) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                    .callbackQuery(callbackQuery)
                    .text("Può scegliere soltato chi ha inviato il messaggio!");
            bot.execute(answerCallbackQuery);
            return;
        }

        String message = handlePlaceholder(generalMessage, hangman);

        EditMessageText editMessageText = new EditMessageText()
                .inlineMessage(callbackQuery.getInlineMessageId().get())
                .replyMarkup(hangman.generateKeyboard())
                .text(Text.parseHtml(message));

        bot.execute(editMessageText);
    }

    private String handlePlaceholder(String string, Hangman hangman) {
        string = string.replace("word_state", hangman.getCurrentState());
        string = string.replace("current_errors", String.valueOf(hangman.getErrors()));
        string = string.replace("max_errors", String.valueOf(hangman.getMaxErrors()));
        string = string.replace("category", hangman.getCategory());
        return string;
    }
}
