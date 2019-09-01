package it.marcodemartino.hangmanbot.inline;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.inline.*;
import io.github.ageofwar.telejam.methods.AnswerInlineQuery;
import io.github.ageofwar.telejam.methods.EditMessageText;
import io.github.ageofwar.telejam.methods.SendMessage;
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup;
import io.github.ageofwar.telejam.text.Text;
import it.marcodemartino.hangmanbot.languages.Localization;
import it.marcodemartino.hangmanbot.languages.LocalizedWord;
import it.marcodemartino.hangmanbot.logic.GuessResult;
import it.marcodemartino.hangmanbot.logic.Hangman;
import it.marcodemartino.hangmanbot.stats.StatsManager;
import it.marcodemartino.hangmanbot.stats.UserStats;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InlineResults implements InlineQueryHandler {

    @Getter
    @Setter
    private static boolean startMatch = true;
    private final Localization localization;
    private final Bot bot;
    private StatsManager statsManager;
    private Map<String, Hangman> matches;
    private InlineKeyboardMarkup cancelButton;
    private final LocalizedWord localizedWord;

    public InlineResults(Localization localization, LocalizedWord localizedWord, Bot bot, StatsManager statsManager, Map<String, Hangman> matches) {
        this.localization = localization;
        this.localizedWord = localizedWord;
        this.bot = bot;
        this.statsManager = statsManager;
        this.matches = matches;
        cancelButton = new InlineKeyboardMarkup(new CallbackDataInlineKeyboardButton("Annulla", "cancel_message"));
    }

    @Override
    public void onChosenInlineResult(ChosenInlineResult chosenInlineResult) throws IOException {
        if(!chosenInlineResult.getInlineMessageId().isPresent()) return;
        if (chosenInlineResult.getSender().getId() == 229856560L && (chosenInlineResult.getQuery().equalsIgnoreCase("stop")))
            return;

        Locale locale = chosenInlineResult.getSender().getLocale();

        if (!InlineResults.startMatch) {
            EditMessageText editMessageText = new EditMessageText()
                    .inlineMessage(chosenInlineResult.getInlineMessageId().get())
                    .text(Text.parseHtml(localization.getString("waiting_for_stop", locale)));

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
                .text(localization.getString("choose_play_mode", locale));

        bot.execute(editMessageText);

        String category = chosenInlineResult.getResultId().split("_")[1];

        Hangman hangman = new Hangman(localizedWord.getRandomWordFromCategory(category, locale), chosenInlineResult.getSender().getId(), category, 5);
        matches.put(chosenInlineResult.getInlineMessageId().get(), hangman);
        statsManager.increaseStats(chosenInlineResult.getSender(), GuessResult.MATCH_STARTED);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        String message = String.format("\nOrario: %s\nCategoria: %s\nParola: %s\nUserID: %s\nUsername: %s\nNome: %s\n",
                formatter.format(date),
                category,
                hangman.getWord(),
                chosenInlineResult.getSender().getId(),
                chosenInlineResult.getSender().getUsername().orElse("none"),
                chosenInlineResult.getSender().getName()
        );

        SendMessage sendMessage = new SendMessage()
                .text(message)
                .chat(-1001296534897L);
        bot.execute(sendMessage);
    }

    @Override
    public void onInlineQuery(InlineQuery inlineQuery) throws Exception {
        if (inlineQuery.getQuery().equalsIgnoreCase("stats")) {
            Locale locale = inlineQuery.getSender().getLocale();
            StringBuilder message = new StringBuilder(localization.getString("stats_message_title", locale));

            for (UserStats user : statsManager.getBestUsers()) {
                message.append(localization.handlePlaceholder(localization.getString("stats_message_body", locale), user));
            }

            UserStats userStats = statsManager.getUserStats(inlineQuery.getSender().getId());
            message.append(localization.handlePlaceholder(localization.getString("stats_message_sender", locale), userStats));

            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                    .inlineQuery(inlineQuery)
                    .cacheTime(0)
                    .results(
                            new InlineQueryResultArticle(
                                    "stats",
                                    localization.getString("stats_title_result", locale),
                                    new InputTextMessageContent(Text.parseHtml(message.toString()), null),
                                    null,
                                    localization.getString("stats_description_result", locale)
                            ));
            bot.execute(answerInlineQuery);

        } else {
            Locale locale = inlineQuery.getSender().getLocale();

            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                    .inlineQuery(inlineQuery)
                    .cacheTime(0)
                    .results(
                            getInlineQueryResults(locale).toArray(new InlineQueryResult[0])
                    );
            bot.execute(answerInlineQuery);
        }

    }

    private List<InlineQueryResult> getInlineQueryResults(Locale locale) throws IOException {
        List<InlineQueryResult> inlineQueryResults = new ArrayList<>();
        inlineQueryResults.add(new InlineQueryResultArticle(
                "match_random",
                localization.getString("random_category", locale),
                new InputTextMessageContent(Text.parseHtml(localization.getString("loading", locale)), null),
                cancelButton,
                localization.getString("category_result_description", locale)
        ));

        for (String category : localizedWord.getCategoriesFromLocale(locale)) {
            inlineQueryResults.add(new InlineQueryResultArticle(
                    "match_" + category,
                    localization.getString("category", locale).replace("%category", category),
                    new InputTextMessageContent(Text.parseHtml(localization.getString("loading", locale)), null),
                    cancelButton,
                    localization.getString("category_result_description", locale)
            ));
        }

        return inlineQueryResults;
    }

}
