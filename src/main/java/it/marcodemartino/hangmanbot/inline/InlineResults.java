package it.marcodemartino.hangmanbot.inline;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.inline.*;
import io.github.ageofwar.telejam.methods.AnswerInlineQuery;
import io.github.ageofwar.telejam.methods.EditMessageText;
import io.github.ageofwar.telejam.methods.SendMessage;
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup;
import io.github.ageofwar.telejam.text.Text;
import io.github.ageofwar.telejam.users.User;
import it.marcodemartino.hangmanbot.languages.Localization;
import it.marcodemartino.hangmanbot.languages.LocalizedWord;
import it.marcodemartino.hangmanbot.logic.GuessResult;
import it.marcodemartino.hangmanbot.logic.Hangman;
import it.marcodemartino.hangmanbot.stats.StatsManager;
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
        cancelButton = new InlineKeyboardMarkup(new CallbackDataInlineKeyboardButton("❌", "cancel_message"));
    }

    @Override
    public void onChosenInlineResult(ChosenInlineResult chosenInlineResult) throws IOException {
        if(!chosenInlineResult.getInlineMessageId().isPresent()) return;

        User user = chosenInlineResult.getSender();
        Locale locale = localization.getUserLocale(user);

        if (!InlineResults.startMatch) {
            EditMessageText editMessageText = new EditMessageText()
                    .inlineMessage(chosenInlineResult.getInlineMessageId().get())
                    .text(Text.parseHtml(localization.getString("waiting_for_stop", user)));

            bot.execute(editMessageText);
            return;
        }

        if (chosenInlineResult.getResultId().equals("menu")) {
            InlineKeyboardButton[] buttons = {
                    new CallbackDataInlineKeyboardButton(localization.getString("change_language_button", user), "choose_language"),
                    new CallbackDataInlineKeyboardButton(localization.getString("show_statistics", user), "stats"),
            };

            EditMessageText editMessageText = new EditMessageText()
                    .inlineMessage(chosenInlineResult.getInlineMessageId().get())
                    .replyMarkup(new InlineKeyboardMarkup(buttons))
                    .text(localization.getString("menu_message", user));

            bot.execute(editMessageText);
            return;
        }

        String[] queryArguments = chosenInlineResult.getResultId().split("_");

        statsManager.increaseStats(chosenInlineResult.getSender(), GuessResult.MATCH_STARTED);

        if (queryArguments[0].equals("custom")) {
            Hangman hangman = new Hangman(queryArguments[1], locale, chosenInlineResult.getSender().getId(), "custom", 5);
            hangman.setCustomMatch(true);
            hangman.setMultiplayer(true);
            matches.put(chosenInlineResult.getInlineMessageId().get(), hangman);

            EditMessageText editMessageText = new EditMessageText()
                    .inlineMessage(chosenInlineResult.getInlineMessageId().get())
                    .replyMarkup(hangman.generateKeyboard(localizedWord.getAlphabetFromLocale(locale)))
                    .text(Text.parseHtml(localization.handlePlaceholder(localization.getString("general_match_message", user), hangman)));

            bot.execute(editMessageText);

            logMatch(hangman, queryArguments[1], chosenInlineResult);

            return;
        }

        Hangman hangman = new Hangman(localizedWord.getRandomWordFromCategory(queryArguments[1], locale), locale, chosenInlineResult.getSender().getId(), queryArguments[1], 5);
        matches.put(chosenInlineResult.getInlineMessageId().get(), hangman);

        InlineKeyboardButton[] buttons = {
                new CallbackDataInlineKeyboardButton("Singleplayer", "player_1"),
                new CallbackDataInlineKeyboardButton("Multiplayer", "player_2")
        };

        EditMessageText editMessageText = new EditMessageText()
                .inlineMessage(chosenInlineResult.getInlineMessageId().get())
                .replyMarkup(new InlineKeyboardMarkup(buttons))
                .text(localization.getString("choose_play_mode", user));

        bot.execute(editMessageText);

        logMatch(hangman, queryArguments[1], chosenInlineResult);

    }

    @Override
    public void onInlineQuery(InlineQuery inlineQuery) throws Exception {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery()
                .inlineQuery(inlineQuery)
                .cacheTime(0)
                .results(
                        getInlineQueryResults(inlineQuery.getSender(), inlineQuery.getQuery()).toArray(new InlineQueryResult[0])
                );
        bot.execute(answerInlineQuery);
    }

    private List<InlineQueryResult> getInlineQueryResults(User user, String query) throws IOException {
        List<InlineQueryResult> inlineQueryResults = new ArrayList<>();

        if (!query.isEmpty()) {
            inlineQueryResults.add(new InlineQueryResultArticle(
                    "custom_" + query,
                    localization.getString("custom_match", user).replace("%word", query),
                    new InputTextMessageContent(Text.parseHtml(localization.getString("loading", user)), null),
                    cancelButton,
                    localization.getString("custom_match_description", user)
            ));
            return inlineQueryResults;
        }

        inlineQueryResults.add(new InlineQueryResultArticle(
                "menu",
                localization.getString("menu_button", user),
                new InputTextMessageContent(Text.parseHtml(localization.getString("loading", user)), null),
                cancelButton,
                localization.getString("menu_button_description", user)
        ));

        inlineQueryResults.add(new InlineQueryResultArticle(
                "match_random",
                localization.getString("random_category", user),
                new InputTextMessageContent(Text.parseHtml(localization.getString("loading", user)), null),
                cancelButton,
                localization.getString("category_result_description", user)
        ));

        for (String category : localizedWord.getCategoriesFromLocale(localization.getUserLocale(user))) {
            inlineQueryResults.add(new InlineQueryResultArticle(
                    "match_" + category,
                    localization.getString("category", user).replace("%category", category),
                    new InputTextMessageContent(Text.parseHtml(localization.getString("loading", user)), null),
                    cancelButton,
                    localization.getString("category_result_description", user)
            ));
        }

        return inlineQueryResults;
    }

    private void logMatch(Hangman hangman, String category, ChosenInlineResult result) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        String message = String.format("\nOrario: %s\nCategoria: %s\nParola: %s\nUserID: %s\nUsername: %s\nNome: %s\n",
                formatter.format(date),
                category,
                hangman.getWord(),
                result.getSender().getId(),
                result.getSender().getUsername().orElse("none"),
                result.getSender().getName()
        );

        SendMessage sendMessage = new SendMessage()
                .text(message)
                .chat(-1001296534897L);
        bot.execute(sendMessage);
    }

}
