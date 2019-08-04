package it.marcodemartino.hangmanbot.logic;

import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton;
import io.github.ageofwar.telejam.inline.InlineKeyboardButton;
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

public class Hangman {

    private String word;
    private String category;
    private List<Character> guessedLetters;
    private List<Character> wrongLetters;
    private int errors;
    private int maxErrors;

    public Hangman(String word, String category, int maxErrors) {
        this.word = word;
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
            errors++;
            return GuessResult.LETTER_WRONG;
        }
    }

    public GuessResult getStatus() {
        if (areLetterRight()) return GuessResult.MATCH_WIN;
        if(errors >= maxErrors) return GuessResult.MATCH_LOSE;
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
        for (Character character : guessedLetters)
            alphabet = alphabet.replace(String.valueOf(character), String.valueOf(character).toUpperCase());

        for (Character character : wrongLetters)
            alphabet = alphabet.replace(String.valueOf(character), String.valueOf(character).toUpperCase());

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (char c : alphabet.toCharArray()) {
            CallbackDataInlineKeyboardButton button;

            if(c != Character.toLowerCase(c)) //letter is guessed
                button = new CallbackDataInlineKeyboardButton("-", "letter_" + Character.toLowerCase(c));
            else
                button = new CallbackDataInlineKeyboardButton(String.valueOf(c).toUpperCase(), "letter_" + Character.toLowerCase(c));


            buttons.add(button);
        }

        return InlineKeyboardMarkup.fromColumns(6, buttons);
    }

    public String getWord() {
        return word;
    }

    public String getCategory() {
        return category;
    }

    public int getErrors() {
        return errors;
    }

    public int getMaxErrors() {
        return maxErrors;
    }
}
