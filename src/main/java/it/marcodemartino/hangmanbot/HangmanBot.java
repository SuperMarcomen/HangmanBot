package it.marcodemartino.hangmanbot;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.LongPollingBot;

import java.io.IOException;

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
    }
    
}
