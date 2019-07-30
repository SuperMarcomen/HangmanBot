package it.marcodemartino.hangmanbot.callback;

import io.github.ageofwar.telejam.Bot;
import io.github.ageofwar.telejam.callbacks.CallbackDataHandler;
import io.github.ageofwar.telejam.callbacks.CallbackQuery;
import io.github.ageofwar.telejam.methods.DeleteMessage;

public class CancelMessage implements CallbackDataHandler {

    private final Bot bot;

    @Override
    public void onCallbackData(CallbackQuery callbackQuery, String s, String s1) throws Throwable {
        if(!s.equalsIgnoreCase("cancel_message")) return;
        if(!callbackQuery.getMessage().isPresent()) return;
        DeleteMessage deleteMessage = new DeleteMessage()
                .message(callbackQuery.getMessage().get());
        bot.execute(deleteMessage);
    }

    public CancelMessage(Bot bot) {
        this.bot = bot;
    }

}
