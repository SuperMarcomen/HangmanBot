package it.marcodemartino.hangmanbot;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.LongPollingBot;
import it.marcodemartino.hangmanbot.callback.LetterClick;
import it.marcodemartino.hangmanbot.callback.StartMatch;
import it.marcodemartino.hangmanbot.commands.Start;
import it.marcodemartino.hangmanbot.inline.AdminUtilities;
import it.marcodemartino.hangmanbot.inline.InlineResults;
import it.marcodemartino.hangmanbot.languages.Localization;
import it.marcodemartino.hangmanbot.languages.LocalizedWord;
import it.marcodemartino.hangmanbot.logic.Hangman;
import it.marcodemartino.hangmanbot.stats.DatabaseManager;
import it.marcodemartino.hangmanbot.stats.StatsManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class HangmanBot extends LongPollingBot {

    public static final List<Locale> SUPPORTED_LANGUAGES = new ArrayList<>();

    public HangmanBot(Localization localization, LocalizedWord localizedWord, String username, String password, Bot bot, Map<String, Hangman> matches) throws IOException, SQLException {
        super(bot);
        SUPPORTED_LANGUAGES.add(Locale.ENGLISH);
        SUPPORTED_LANGUAGES.add(Locale.ITALIAN);
        SUPPORTED_LANGUAGES.add(new Locale("fa"));

        events.registerCommand(
                new Start(localization, bot), "start"
        );

        DatabaseManager databaseManager = new DatabaseManager("localhost", username, password, "test", "bot", 3306);
        StatsManager statsManager = new StatsManager(databaseManager);

        events.registerUpdateHandler(
                new LetterClick(localization, localizedWord, bot, statsManager, matches)
        );

        events.registerUpdateHandler(
                new AdminUtilities(bot)
        );

        events.registerUpdateHandler(
                new InlineResults(localization, localizedWord, bot, statsManager, matches)
        );

        events.registerUpdateHandler(
                new StartMatch(localization, localizedWord, bot, matches)
        );

    }

    public static void main(String[] args) throws IOException, SQLException {
        if (args.length < 2) {
            System.err.println("Pass the bot token as unique program argument. And also the database username and password");
            System.exit(1);
        }
        String token = args[0];
        Bot bot = Bot.fromToken(token);

        Map<String, Hangman> matches = new HashMap<>();

        String password = args.length < 3 ? null : args[2];
        new HangmanBot(new Localization(), new LocalizedWord(), args[1], password, bot, matches).run();

    }

    @Override
    public void onError(Throwable t) {
        super.onError(t);
        t.printStackTrace();
    }

}
