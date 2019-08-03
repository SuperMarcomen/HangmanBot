package it.marcodemartino.hangmanbot.commands;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.commands.Command;
import io.github.ageofwar.telejam.commands.CommandHandler;
import io.github.ageofwar.telejam.messages.TextMessage;
import io.github.ageofwar.telejam.methods.SendMessage;

import java.io.IOException;

public class Start implements CommandHandler {

    private final Bot bot;

    public void onCommand(Command command, TextMessage textMessage) throws IOException {
        SendMessage sendMessage = new SendMessage()
                .text("Ciao, usami inline scrivendo in chat @SuperMarcomenBOT e cliccando sul buttone che comparirà sopra!")
                .chat(textMessage.getChat());
        bot.execute(sendMessage);
    }

    public Start(Bot bot) {
        this.bot = bot;
    }

}
