package it.marcodemartino.hangmanbot.callback;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.callbacks.CallbackDataHandler;
import io.github.ageofwar.telejam.callbacks.CallbackQuery;
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery;
import io.github.ageofwar.telejam.methods.DeleteMessage;
import io.github.ageofwar.telejam.methods.EditMessageText;
import io.github.ageofwar.telejam.text.Text;
import it.marcodemartino.hangmanbot.logic.GuessResult;
import it.marcodemartino.hangmanbot.logic.Hangman;

import java.util.Map;

public class LetterClick implements CallbackDataHandler {

    private final Bot bot;
    private Map<String, Hangman> matches;

    @Override
    public void onCallbackData(CallbackQuery callbackQuery, String s, String s1) throws Throwable {
        if(!s.startsWith("letter")) return;
        if(!callbackQuery.getInlineMessageId().isPresent()) return;

        Hangman hangman = matches.get(callbackQuery.getInlineMessageId().get());

        if(hangman == null) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                    .callbackQuery(callbackQuery)
                    .text("Questa partita è già finita");
            bot.execute(answerCallbackQuery);
            return;
        }

        char c = s.replace("letter_", "").charAt(0);
        GuessResult guessResult = hangman.guessLetter(c);

        if(!guessResult.equals(GuessResult.LETTER_ALREADY_SAID) && !guessResult.equals(GuessResult.WORD_ALREADY_SAID)) {
            EditMessageText editMessageText = new EditMessageText()
                    .text(Text.parseHtml("🔡 <b>Parola da indovinare:</b> " + hangman.getCurrentState() + "\n❌ <b>Errori:</b> " + hangman.getErrors() + "/" + hangman.getMaxErrors()))
                    .replyMarkup(hangman.generateKeyboard())
                    .inlineMessage(callbackQuery.getInlineMessageId().get());
            bot.execute(editMessageText);
        }

        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                .callbackQuery(callbackQuery)
                .text(getResponseMessage(guessResult));


        GuessResult status = hangman.getStatus();
        if(status == null) {
            bot.execute(answerCallbackQuery);
            return;
        }

        answerCallbackQuery.text(getResponseMessage(status));
        bot.execute(answerCallbackQuery);
        matches.remove(callbackQuery.getInlineMessageId().get());

        String message = "🔡 <b>Parola da indovinare:</b> " + hangman.getCurrentState() + "\n❌ <b>Errori:</b> " + hangman.getErrors() + "/" + hangman.getMaxErrors() + "\n\n";
        if(status.equals(GuessResult.MATCH_WIN)) message += "🎊 Hai vinto!";
        else message += "⚠ <b>Hai perso!</b> La <b>parola</b> da indovinare era: " + hangman.getWord();

        EditMessageText editMessageText = new EditMessageText()
                .text(Text.parseHtml(message))
                .inlineMessage(callbackQuery.getInlineMessageId().get());
        bot.execute(editMessageText);
    }

    private String getResponseMessage(GuessResult guessResult) {
        switch (guessResult) {
            case LETTER_ALREADY_SAID:
                return "Lettera già detta";
            case LETTER_WRONG:
                return "Lettera sbagliata";
            case LETTER_GUESSED:
                return "Hai indovinato una lettera";
            case WORD_WRONG:
                return "Parola sbagliata";
            case WORD_GUESSED:
                return "Parola indovinata";
            case MATCH_LOSE:
                return "Hai perso la partita";
            case MATCH_WIN:
                return "Hai vinto la partita";
        }
        return "C'è stato un errore";
    }

    public LetterClick(Bot bot, Map<String, Hangman> matches) {
        this.bot = bot;
        this.matches = matches;
    }

}
