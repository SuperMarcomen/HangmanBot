package it.marcodemartino.hangmanbot;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.LongPollingBot;
import it.marcodemartino.hangmanbot.callback.CancelMessage;
import it.marcodemartino.hangmanbot.callback.LetterClick;
import it.marcodemartino.hangmanbot.commands.Start;
import it.marcodemartino.hangmanbot.inline.StartInlineMatch;
import it.marcodemartino.hangmanbot.logic.Hangman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HangmanBot extends LongPollingBot {

    public HangmanBot(Bot bot, Map<String, Hangman> matches) throws IOException {
        super(bot);
        events.registerCommand(
                new Start(bot), "start"
        );

        String generalMessage = "<b>Categoria:</b> category\n\n<b>Parola da indovinare:</b>\nword_state \n❌ <b>Errori:</b> current_errors/max_errors";
        events.registerUpdateHandler(
                new StartInlineMatch(bot, matches, generalMessage, getWordsFiles())
        );

        events.registerUpdateHandler(
                new LetterClick(bot, matches, generalMessage)
        );

        events.registerUpdateHandler(
                new CancelMessage(bot)
        );

    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Pass the bot token as unique program argument");
            System.exit(1);
        }
        String token = args[0];
        Bot bot = Bot.fromToken(token);

        Map<String, Hangman> matches = new HashMap<>();
        HangmanBot hangmanBOT = new HangmanBot(bot, matches);
        hangmanBOT.run();
    }

    private Map<String, List<String>> getWordsFiles() throws IOException {
        Map<String, List<String>> wordCategory = new HashMap<>();

        URL url = HangmanBot.class
                .getClassLoader().getResource("words");
        File folder = new File(url.getFile());

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) continue;
            wordCategory.put(fileEntry.getName().replace(".txt", ""), getWordsFromFile(fileEntry));

        }

        return wordCategory;
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
