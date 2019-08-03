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

    public HangmanBot(Bot bot) throws IOException {
        super(bot);

        events.registerCommand(
                new Start(bot), "start"
        );
        Map<String, Hangman> matches = new HashMap<>();

        String generalMessage = "<b>Parola da indovinare:</b> word_state \n❌ <b>Errori:</b> current_errors/max_errors";
        events.registerUpdateHandler(
                new StartInlineMatch(bot, matches, generalMessage, getWordsFromFile("words.txt"))
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
        HangmanBot hangmanBOT = new HangmanBot(bot);
        hangmanBOT.run();
    }


    private List<String> getWordsFromFile(String fileName) throws IOException {
        File file = getFileFromResources(fileName);
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

    private File getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }

}
