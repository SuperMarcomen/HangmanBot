package it.marcodemartino.hangmanbot.languages;

import it.marcodemartino.hangmanbot.HangmanBot;
import it.marcodemartino.hangmanbot.logic.Hangman;
import it.marcodemartino.hangmanbot.stats.UserStats;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {

    public String getString(String key, Locale locale) throws IOException {
        if (!HangmanBot.SUPPORTED_LANGUAGES.contains(locale)) locale = Locale.ENGLISH;

        ResourceBundle resourceBundle = new UTF8ResourceBundle().newBundle("languages/lang", locale, getClass().getClassLoader(), false);
        return resourceBundle.getString(key);
    }

    public String handlePlaceholder(String string, Hangman hangman) {
        string = string.replace("%word_state", hangman.getCurrentState());
        string = string.replace("%word", hangman.getWord());
        string = string.replace("%current_errors", String.valueOf(hangman.getErrors()));
        string = string.replace("%max_errors", String.valueOf(hangman.getMaxErrors()));
        string = string.replace("%category", hangman.getCategory());
        return string;
    }

    public String handlePlaceholder(String string, UserStats user) {
        string = string.replace("%username", user.getUsername());
        string = string.replace("%guessed_letters", String.valueOf(user.getGuessedLetters()));
        string = string.replace("%wrong_letters", String.valueOf(user.getWrongLetters()));
        string = string.replace("%ratio", String.format("%.2f", user.getRatio()));
        return string;
    }

}
