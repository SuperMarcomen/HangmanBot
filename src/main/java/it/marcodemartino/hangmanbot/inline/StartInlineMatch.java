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
import java.util.Random;

public class StartInlineMatch implements InlineQueryHandler {

    private final Bot bot;
    private final String generalMessage;
    private Map<String, List<String>> wordCategory;
    private Map<String, Hangman> matches;
    private InlineKeyboardMarkup cancelButton;

    public StartInlineMatch(Bot bot, Map<String, Hangman> matches, String generalMessage, Map<String, List<String>> wordCategory) {
        this.bot = bot;
        this.matches = matches;
        this.generalMessage = generalMessage;
        this.wordCategory = wordCategory;
        cancelButton = new InlineKeyboardMarkup(new CallbackDataInlineKeyboardButton("Annulla", "cancel_message"));
    }

    @Override
    public void onChosenInlineResult(ChosenInlineResult chosenInlineResult) throws IOException {
        if(!chosenInlineResult.getInlineMessageId().isPresent()) return;
        String category = chosenInlineResult.getResultId().substring(6);
        Hangman hangman = new Hangman(getRandomWord(category), category, 5);
        String message = handlePlaceholder(generalMessage, hangman);

        EditMessageText editMessageText = new EditMessageText()
                .inlineMessage(chosenInlineResult.getInlineMessageId().get())
                .replyMarkup(hangman.generateKeyboard())
                .text(Text.parseHtml(message));

        bot.execute(editMessageText);
        matches.put(chosenInlineResult.getInlineMessageId().get(), hangman);
        System.out.println(category + ":" + hangman.getWord());
    }

    @Override
    public void onInlineQuery(InlineQuery inlineQuery) throws IOException {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                .inlineQuery(inlineQuery)
                .cacheTime(0)
                .results(
                        getInlineQueryResults().toArray(new InlineQueryResult[0])
                );
        bot.execute(answerInlineQuery);
    }

    private String handlePlaceholder(String string, Hangman hangman) {
        string = string.replace("word_state", hangman.getCurrentState());
        string = string.replace("current_errors", String.valueOf(hangman.getErrors()));
        string = string.replace("max_errors", String.valueOf(hangman.getMaxErrors()));
        string = string.replace("category", hangman.getCategory());
        return string;
    }

    private List<InlineQueryResult> getInlineQueryResults() {
        List<InlineQueryResult> inlineQueryResults = new ArrayList<>();
        inlineQueryResults.add(newInlineQueryResult("match_random", "Categoria casuale"));
        wordCategory.forEach((category, wordList) -> inlineQueryResults.add(newInlineQueryResult("match_" + category, "Categoria: " + category)));
        return inlineQueryResults;
    }

    private InlineQueryResult newInlineQueryResult(String id, String title) {
        return new InlineQueryResultArticle(
                id,
                title,
                new InputTextMessageContent(new Text("Loading"), null),
                cancelButton,
                "Clicca per cominciare una nuova partita"
        );
    }

    private String getRandomWord(String category) {
        List<String> words = wordCategory.get(category);
        if (category.equals("random")) words = getRandomCategory();

        return words.get(new Random().nextInt(words.size()));
    }

    private List<String> getRandomCategory() {
        List<String> keysAsArray = new ArrayList<>(wordCategory.keySet());
        keysAsArray.remove("Java");
        String randomKey = keysAsArray.get(new Random().nextInt(keysAsArray.size()));
        return wordCategory.get(randomKey);
    }

}
