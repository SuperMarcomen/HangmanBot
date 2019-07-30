package it.marcodemartino.hangmanbot;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.LongPollingBot;
import it.marcodemartino.hangmanbot.callback.LetterClick;
import it.marcodemartino.hangmanbot.commands.Start;
import it.marcodemartino.hangmanbot.inline.StartInlineMatch;
import it.marcodemartino.hangmanbot.logic.Hangman;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HangmanBot extends LongPollingBot {

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

    public HangmanBot(Bot bot) {
        super(bot);
        events.registerCommand(
                new Start(bot), "start"
        );
        Map<String, Hangman> matches = new HashMap<>();
        events.registerUpdateHandler(
                new StartInlineMatch(bot, matches)
        );

        events.registerUpdateHandler(
                new LetterClick(bot, matches)
        );
    }
    
}
