package it.marcodemartino.hangmanbot;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.LongPollingBot;
import it.marcodemartino.hangmanbot.callback.LetterClick;
import it.marcodemartino.hangmanbot.callback.StartMatch;
import it.marcodemartino.hangmanbot.commands.Start;
import it.marcodemartino.hangmanbot.inline.AdminUtilities;
import it.marcodemartino.hangmanbot.inline.InlineResult;
import it.marcodemartino.hangmanbot.logic.Hangman;
import it.marcodemartino.hangmanbot.logic.Words;
import it.marcodemartino.hangmanbot.stats.DatabaseManager;
import it.marcodemartino.hangmanbot.stats.StatsManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class HangmanBot extends LongPollingBot {

    public HangmanBot(String username, String password, Bot bot, Map<String, Hangman> matches) throws IOException, SQLException {
        super(bot);
        events.registerCommand(
                new Start(bot), "start"
        );

        String generalMessage = "<b>Categoria:</b> category\n<b>Parola da indovinare:</b>\nword_state\n<b>Errori:</b> current_errors/max_errors";

        DatabaseManager databaseManager = new DatabaseManager("localhost", username, password, "test", "bot", 3306);
        databaseManager.start();
        StatsManager statsManager = new StatsManager(databaseManager);

        events.registerUpdateHandler(
                new LetterClick(bot, statsManager, matches, generalMessage)
        );

        events.registerUpdateHandler(
                new AdminUtilities(bot)
        );

        events.registerUpdateHandler(
                new StartMatch(bot, statsManager, matches, generalMessage)
        );

        events.registerUpdateHandler(
                new InlineResult(bot, statsManager, matches, getWordsFiles())
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
        new Thread(new HangmanBot(args[1], password, bot, matches)).start();

        Scanner in = new Scanner(System.in);
        String message = in.nextLine();
        if (message.equalsIgnoreCase("stop")) {
            System.out.println("Ok, mi spengo");
            System.exit(0);
        }

    }

    private Map<Locale, Words> getWordsFiles() throws IOException {
        Map<Locale, Words> words = new HashMap<>();

        File folder = new File("words");

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                Locale locale = Locale.forLanguageTag(fileEntry.getName());
                Map<String, List<String>> wordCategory = new HashMap<>();

                for (final File file : fileEntry.listFiles()) {
                    wordCategory.put(file.getName().replace(".txt", ""), getWordsFromFile(file));
                }

                words.put(locale, new Words(wordCategory));

            }

        }

        return words;
    }

    private List<String> getWordsFromFile(File file) throws IOException {
        List<String> wordList = new ArrayList<>();

        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            while ((line = br.readLine()) != null) {
                wordList.add(line);
            }
        }

        return wordList;
    }

}
