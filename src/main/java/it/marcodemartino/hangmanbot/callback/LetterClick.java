package it.marcodemartino.hangmanbot.callback;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.callbacks.CallbackDataHandler;
import io.github.ageofwar.telejam.callbacks.CallbackQuery;
import io.github.ageofwar.telejam.methods.DeleteMessage;
import io.github.ageofwar.telejam.methods.EditMessageText;
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
        char c = s.replace("letter_", "").charAt(0);
        hangman.guessLetter(c);

        EditMessageText editMessageText = new EditMessageText()
                .text(hangman.getCurrentState())
                .replyMarkup(hangman.generateKeyboard())
                .inlineMessage(callbackQuery.getInlineMessageId().get());
        bot.execute(editMessageText);
    }

    public LetterClick(Bot bot, Map<String, Hangman> matches) {
        this.bot = bot;
        this.matches = matches;
    }

}
