package it.marcodemartino.hangmanbot.inline;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.inline.InlineQuery;
import io.github.ageofwar.telejam.inline.InlineQueryHandler;
import io.github.ageofwar.telejam.inline.InlineQueryResultArticle;
import io.github.ageofwar.telejam.inline.InputTextMessageContent;
import io.github.ageofwar.telejam.methods.AnswerInlineQuery;
import io.github.ageofwar.telejam.text.Text;

public class AdminUtilities implements InlineQueryHandler {

    private final Bot bot;

    public AdminUtilities(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onInlineQuery(InlineQuery inlineQuery) throws Throwable {
        if (inlineQuery.getSender().getId() != 229856560L) return;

        if (!inlineQuery.getQuery().equalsIgnoreCase("stop")) return;

        InlineResults.setStartMatch(false);
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                .inlineQuery(inlineQuery)
                .cacheTime(1)
                .results(
                        new InlineQueryResultArticle(
                                "stop",
                                "Stoppa il bot",
                                new InputTextMessageContent(new Text("Hai richiesto con successo di stoppare il bot.\nAspetto che tutte le partite siano finite e poi mi spengo")),
                                null,
                                ""
                        )
                );

        bot.execute(answerInlineQuery);

    }

}
