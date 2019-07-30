package it.marcodemartino.hangmanbot.inline;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.inline.*;
import io.github.ageofwar.telejam.methods.AnswerInlineQuery;
import io.github.ageofwar.telejam.methods.EditMessageText;
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup;
import io.github.ageofwar.telejam.text.Text;
import it.marcodemartino.hangmanbot.logic.Hangman;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StartInlineMatch implements InlineQueryHandler {

    private final Bot bot;
    private Map<String, Hangman> matches;
    private InlineKeyboardMarkup cancelButton;

    public StartInlineMatch(Bot bot, Map<String, Hangman> matches) {
        this.bot = bot;
        this.matches = matches;
        cancelButton = new InlineKeyboardMarkup(new CallbackDataInlineKeyboardButton("Annulla", "cancel_message"));
    }

    @Override
    public void onChosenInlineResult(ChosenInlineResult chosenInlineResult) throws IOException {
        if(!chosenInlineResult.getInlineMessageId().isPresent()) return;

        Hangman hangman = new Hangman("hello");
        EditMessageText editMessageText = new EditMessageText()
                .inlineMessage(chosenInlineResult.getInlineMessageId().get())
                .replyMarkup( hangman.generateKeyboard())
                .text("Parola da indovinare: -----");

        bot.execute(editMessageText);
        matches.put(chosenInlineResult.getInlineMessageId().get(), hangman);
    }

    @Override
    public void onInlineQuery(InlineQuery inlineQuery) throws IOException {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                .inlineQuery(inlineQuery)
                .cacheTime(0)
                .results(
                        newInlineQueryResult()
                );
        bot.execute(answerInlineQuery);
    }

    private InlineQueryResult newInlineQueryResult() {
        return new InlineQueryResultArticle(
                "new_match",
                "Nuova partita",
                new InputTextMessageContent(new Text("Loading"), null),
                cancelButton,
                "Clicca per cominciare una nuova partita"
        );
    }

    private InlineKeyboardMarkup getKeyboardLetters() {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (char c : alphabet) {
            CallbackDataInlineKeyboardButton button = new CallbackDataInlineKeyboardButton(String.valueOf(c), "letter_" + c);
            buttons.add(button);
        }

        return InlineKeyboardMarkup.fromColumns(6, buttons);
    }

}
