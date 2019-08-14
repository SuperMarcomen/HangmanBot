package it.marcodemartino.hangmanbot.inline;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.inline.*;
import io.github.ageofwar.telejam.methods.AnswerInlineQuery;
import io.github.ageofwar.telejam.text.Text;
import it.marcodemartino.hangmanbot.stats.StatsManager;

public class AdminUtilities implements InlineQueryHandler {

    private final Bot bot;

    public AdminUtilities(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onInlineQuery(InlineQuery inlineQuery) throws Throwable {
        if (inlineQuery.getSender().getId() != 229856560L) return;

        if (inlineQuery.getQuery().equalsIgnoreCase("stop")) {
            InlineResults.setStartMatch(false);
            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                    .inlineQuery(inlineQuery)
                    .cacheTime(1)
                    .results(
                            newInlineQueryResult("stop", "Stoppa il bot", "Hai richiesto con successo di stoppare il bot.\nAspetto che tutte le partite siano finite e poi mi spengo")
                    );
            bot.execute(answerInlineQuery);
            return;
        }

        if (inlineQuery.getQuery().equalsIgnoreCase("reload")) {
            StatsManager.reloadUsers();
            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                    .inlineQuery(inlineQuery)
                    .cacheTime(1)
                    .results(
                            newInlineQueryResult("reload", "Ricarica i dati dal database", "Dati utente ricaricati con successo!")
                    );
            bot.execute(answerInlineQuery);
        }
    }

    private InlineQueryResult newInlineQueryResult(String id, String title, String message) {
        return new InlineQueryResultArticle(
                id,
                title,
                new InputTextMessageContent(new Text(message), null),
                null,
                ""
        );
    }
}
