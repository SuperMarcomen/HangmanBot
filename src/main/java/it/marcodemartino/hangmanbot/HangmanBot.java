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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class HangmanBot extends LongPollingBot {

    public static final List<Locale> SUPPORTED_LANGUAGES = new ArrayList<>();

    public HangmanBot(Localization localization, LocalizedWord localizedWord, Bot bot, Map<String, Hangman> matches) {
        super(bot);
        SUPPORTED_LANGUAGES.add(Locale.ENGLISH);
        SUPPORTED_LANGUAGES.add(Locale.ITALIAN);
        SUPPORTED_LANGUAGES.add(new Locale("fa"));

        events.registerCommand(
                new Start(localization, bot), "start"
        );

        DatabaseManager databaseManager = new DatabaseManager();
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

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Pass the bot token as unique program argument.");
            System.exit(1);
        }

        String token = args[0];
        Bot bot = Bot.fromToken(token);
        System.setOut(new DatePrintStream(System.out, new PrintStream(new FileOutputStream("out.log"))));

        Map<String, Hangman> matches = new HashMap<>();

        new HangmanBot(new Localization(), new LocalizedWord(), bot, matches).run();

    }

    @Override
    public void onError(Throwable t) {
        super.onError(t);
        t.printStackTrace();
    }

}
