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

public class InlineResult implements InlineQueryHandler {

    private final Bot bot;
    private StatsManager statsManager;
    private Map<String, Hangman> matches;
    private Map<String, List<String>> wordCategory;
    private InlineKeyboardMarkup cancelButton;
    @Setter
    @Getter
    private static boolean startMatch = true;

    public InlineResult(Bot bot, StatsManager statsManager, Map<String, Hangman> matches, Map<String, List<String>> wordCategory) {
        this.bot = bot;
        this.statsManager = statsManager;
        this.matches = matches;
        this.wordCategory = wordCategory;
        cancelButton = new InlineKeyboardMarkup(new CallbackDataInlineKeyboardButton("Annulla", "cancel_message"));
    }

    @Override
    public void onChosenInlineResult(ChosenInlineResult chosenInlineResult) throws IOException, SQLException {
        if(!chosenInlineResult.getInlineMessageId().isPresent()) return;
        if (chosenInlineResult.getSender().getId() == 229856560L && (chosenInlineResult.getQuery().equalsIgnoreCase("stop") || chosenInlineResult.getQuery().equalsIgnoreCase("reload")))
            return;

        if (!InlineResult.startMatch) {
            EditMessageText editMessageText = new EditMessageText()
                    .inlineMessage(chosenInlineResult.getInlineMessageId().get())
                    .text(Text.parseHtml("Sto aspettando che le partite in corso per Telegram finiscano.\nL'autore del bot @SuperMarcomen ha richiesto di stoppare il bot per eseguire della manutenzione\nOra non è più possibile cominciare nuove partite."));

            bot.execute(editMessageText);
            return;
        }

        InlineKeyboardButton[] buttons = {
                new CallbackDataInlineKeyboardButton("Singleplayer", "player_1"),
                new CallbackDataInlineKeyboardButton("Multiplayer", "player_2")
        };

        EditMessageText editMessageText = new EditMessageText()
                .inlineMessage(chosenInlineResult.getInlineMessageId().get())
                .replyMarkup(new InlineKeyboardMarkup(buttons))
                .text("Come vuoi giocare?");

        bot.execute(editMessageText);

        String category = chosenInlineResult.getResultId().substring(6);
        Hangman hangman = new Hangman(getRandomWord(category), chosenInlineResult.getSender().getId(), category, 5);
        matches.put(chosenInlineResult.getInlineMessageId().get(), hangman);
        statsManager.increaseStats(chosenInlineResult.getSender(), GuessResult.MATCH_STARTED);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        System.out.format("\nOrario: %s\nCategoria: %s\nParola: %s\nUserID: %s\nUsername: %s\nNome: %s\n",
                formatter.format(date),
                category,
                hangman.getWord(),
                chosenInlineResult.getSender().getId(),
                chosenInlineResult.getSender().getUsername().orElse("none"),
                chosenInlineResult.getSender().getName()
        );
    }

    @Override
    public void onInlineQuery(InlineQuery inlineQuery) throws Exception {
        if (inlineQuery.getQuery().equalsIgnoreCase("stats")) {
            StringBuilder message = new StringBuilder("<b>Statistiche:</b>\n\n");

            for (UserStats user : statsManager.getBestUsers()) {
                message.append(String.format("<b>%s:</b>\n<i>Lettere indovinate:</i> %d\n<i>Lettere sbagliate:</i> %d\n\n", user.getUsername(), user.getGuessedLetters(), user.getWrongLetters()));
            }

            UserStats userStats = statsManager.getUserStats(inlineQuery.getSender().getId());
            message.append(String.format("\n<b>Le tue statistiche:</b>\n<i>Lettere indovinate:</i> %d\n<i>Lettere sbagliate:</i> %d", userStats.getGuessedLetters(), userStats.getWrongLetters()));

            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                    .inlineQuery(inlineQuery)
                    .cacheTime(0)
                    .results(
                            newInlineQueryResult("stats", "Mostra le statistiche", "Clicca per mostrare le statistiche", message.toString(), null)
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

    private List<InlineQueryResult> getInlineQueryResults() {
        List<InlineQueryResult> inlineQueryResults = new ArrayList<>();
        inlineQueryResults.add(newInlineQueryResult("match_random", "Categoria casuale", "Clicca per cominciare una nuova partita", "Caricamento", cancelButton));
        wordCategory.forEach((category, wordList) -> inlineQueryResults.add(newInlineQueryResult("match_" + category, "Categoria: " + category, "Clicca per cominciare una nuova partita", "Caricamento", cancelButton)));
        return inlineQueryResults;
    }

    private InlineQueryResult newInlineQueryResult(String id, String title, String description, String message, InlineKeyboardMarkup cancelButton) {
        return new InlineQueryResultArticle(
                id,
                title,
                new InputTextMessageContent(Text.parseHtml(message), null),
                cancelButton,
                description
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
