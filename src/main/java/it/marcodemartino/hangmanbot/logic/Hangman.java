package it.marcodemartino.hangmanbot.logic;

import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton;
import io.github.ageofwar.telejam.inline.InlineKeyboardButton;
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Hangman {

    @Getter
    private String word;
    @Getter
    private long senderId;
    @Getter
    @Setter
    private boolean multiplayer;
    @Getter
    private String category;
    private List<Character> guessedLetters;
    private List<Character> wrongLetters;
    @Getter
    private int errors;
    @Getter
    private int maxErrors;

    public Hangman(String word, long senderId, String category, int maxErrors) {
        this.word = word;
        this.senderId = senderId;
        this.category = category;
        this.maxErrors = maxErrors;
        guessedLetters = new ArrayList<>();
        wrongLetters = new ArrayList<>();
    }

    public String getCurrentState() {
        StringBuilder currentWord = new StringBuilder();
        for (char c : word.toCharArray()) {
            if(guessedLetters.contains(Character.toLowerCase(c)))
                currentWord.append(c).append(" ");
            else if (c == ' ')
                currentWord.append("  ");
            else
                currentWord.append("- ");
        }

        return currentWord.toString();
    }

    public GuessResult guessLetter(char c) {
        if (guessedLetters.contains(c) || wrongLetters.contains(c))
            return GuessResult.LETTER_ALREADY_SAID;

        if (word.toLowerCase().indexOf(c) != -1) {
            guessedLetters.add(c);
            return GuessResult.LETTER_GUESSED;
        } else {
            wrongLetters.add(c);
            if (errors < maxErrors) errors++;
            return GuessResult.LETTER_WRONG;
        }
    }

    public GuessResult getStatus() {
        if (areLetterRight()) return GuessResult.MATCH_WIN;
        if (errors >= maxErrors) return GuessResult.MATCH_LOSE;
        return null;
    }

    private boolean areLetterRight() {
        for (char c : word.toLowerCase().toCharArray())
            if (c != ' ' && !guessedLetters.contains(c))
                return false;

        return true;
    }

    public InlineKeyboardMarkup generateKeyboard() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (char c : alphabet.toCharArray()) {
            CallbackDataInlineKeyboardButton button;

            if (guessedLetters.contains(c) || wrongLetters.contains(c)) //letter was already said
                button = new CallbackDataInlineKeyboardButton("-", "letter_" + c);
            else
                button = new CallbackDataInlineKeyboardButton(String.valueOf(c).toUpperCase(), "letter_" + c);

            buttons.add(button);
        }

        return InlineKeyboardMarkup.fromColumns(6, buttons);
    }

}
