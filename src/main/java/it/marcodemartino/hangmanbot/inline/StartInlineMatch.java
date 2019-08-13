package it.marcodemartino.hangmanbot.inline;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.inline.*;
import io.github.ageofwar.telejam.methods.AnswerInlineQuery;
import io.github.ageofwar.telejam.methods.EditMessageText;
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup;
import io.github.ageofwar.telejam.text.Text;
import it.marcodemartino.hangmanbot.logic.GuessResult;
import it.marcodemartino.hangmanbot.logic.Hangman;
import it.marcodemartino.hangmanbot.stats.StatsManager;
import it.marcodemartino.hangmanbot.stats.UserStats;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StartInlineMatch implements InlineQueryHandler {

    private final Bot bot;
    private StatsManager statsManager;
    private final String generalMessage;
    private Map<String, List<String>> wordCategory;
    private Map<String, Hangman> matches;
    private InlineKeyboardMarkup cancelButton;
    @Setter
    @Getter
    private static boolean startMatch = true;

    public StartInlineMatch(Bot bot, StatsManager statsManager, Map<String, Hangman> matches, String generalMessage, Map<String, List<String>> wordCategory) {
        this.bot = bot;
        this.statsManager = statsManager;
        this.matches = matches;
        this.generalMessage = generalMessage;
        this.wordCategory = wordCategory;
        cancelButton = new InlineKeyboardMarkup(new CallbackDataInlineKeyboardButton("Annulla", "cancel_message"));
    }

    @Override
    public void onChosenInlineResult(ChosenInlineResult chosenInlineResult) throws IOException, SQLException {
        if(!chosenInlineResult.getInlineMessageId().isPresent()) return;
        if (chosenInlineResult.getSender().getId() == 229856560L && (chosenInlineResult.getQuery().equalsIgnoreCase("stop") || chosenInlineResult.getQuery().equalsIgnoreCase("reload")))
            return;

        if (!StartInlineMatch.startMatch) {
            EditMessageText editMessageText = new EditMessageText()
                    .inlineMessage(chosenInlineResult.getInlineMessageId().get())
                    .text(Text.parseHtml("Sto aspettando che le partite in corso per Telegram finiscano.\nL'autore del bot @SuperMarcomen ha richiesto di stoppare il bot per eseguire della manutenzione\nOra non è più possibile cominciare nuove partite."));

            bot.execute(editMessageText);
            return;
        }

        String category = chosenInlineResult.getResultId().substring(6);
        Hangman hangman = new Hangman(getRandomWord(category), category, 5);
        String message = handlePlaceholder(generalMessage, hangman);

        EditMessageText editMessageText = new EditMessageText()
                .inlineMessage(chosenInlineResult.getInlineMessageId().get())
                .replyMarkup(hangman.generateKeyboard())
                .text(Text.parseHtml(message));

        bot.execute(editMessageText);
        matches.put(chosenInlineResult.getInlineMessageId().get(), hangman);
        statsManager.increaseStats(chosenInlineResult.getSender(), GuessResult.MATCH_STARTED);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        System.out.format("\nOrario: %s\nCategoria: %s\nParola: %s\nUserID: %s\nUsername: %s\nNome: %s\n", formatter.format(date), category, hangman.getWord(), chosenInlineResult.getSender().getId(), chosenInlineResult.getSender().getUsername().orElse("none"), chosenInlineResult.getSender().getName());
    }

    @Override
    public void onInlineQuery(InlineQuery inlineQuery) throws Exception {
        if (inlineQuery.getQuery().equalsIgnoreCase("stats")) {
            StringBuilder message = new StringBuilder("<b>Statistiche:</b>\n\n");

            for (UserStats user : statsManager.getBestUsers()) {
                message.append(String.format("<b>%s:</b>\n<i>Lettere indovinate:</i> %d\n<i>Lettere sbagliate:</i> %d\n\n", user.getUsername(), user.getGuessedLetters(), user.getWrongLetters()));
            }

            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                    .inlineQuery(inlineQuery)
                    .cacheTime(0)
                    .results(
                            newInlineQueryResult("stats", "Mosta le statistiche", message.toString())
                    );
            bot.execute(answerInlineQuery);

        } else {
            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                    .inlineQuery(inlineQuery)
                    .cacheTime(0)
                    .results(
                            getInlineQueryResults().toArray(new InlineQueryResult[0])
                    );
            bot.execute(answerInlineQuery);
        }

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
        inlineQueryResults.add(newInlineQueryResult("match_random", "Categoria casuale", "Caricamento"));
        wordCategory.forEach((category, wordList) -> inlineQueryResults.add(newInlineQueryResult("match_" + category, "Categoria: " + category, "Caricamento")));
        return inlineQueryResults;
    }

    private InlineQueryResult newInlineQueryResult(String id, String title, String message) {
        return new InlineQueryResultArticle(
                id,
                title,
                new InputTextMessageContent(Text.parseHtml(message), null),
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
