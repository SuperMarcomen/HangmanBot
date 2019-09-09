package it.marcodemartino.hangmanbot.languages;

import io.github.ageofwar.telejam.text.Text;
import io.github.ageofwar.telejam.users.User;
import it.marcodemartino.hangmanbot.HangmanBot;
import it.marcodemartino.hangmanbot.logic.Hangman;
import it.marcodemartino.hangmanbot.stats.DatabaseManager;
import it.marcodemartino.hangmanbot.stats.UserStats;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

@AllArgsConstructor
public class Localization {

    private DatabaseManager database;

    public String getString(String key, User user) throws IOException {
        Locale locale = getUserLocale(user);

        return getString(key, locale);
    }

    public String getString(String key, Locale locale) throws IOException {
        ResourceBundle resourceBundle = new UTF8ResourceBundle().newBundle("languages/lang", locale, getClass().getClassLoader(), false);
        return resourceBundle.getString(key);
    }

    public Locale getUserLocale(User user) {
        Locale locale = database.getUserLanguage(user);
        if (!HangmanBot.SUPPORTED_LANGUAGES.contains(locale)) locale = Locale.ENGLISH;

        return locale;
    }

    public String handlePlaceholder(String string, Hangman hangman) {
        string = string.replace("%word_state", new Text(hangman.getCurrentState()).toHtmlString());
        string = string.replace("%word", new Text(hangman.getWord()).toHtmlString());
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
