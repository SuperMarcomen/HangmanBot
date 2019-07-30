package it.marcodemartino.hangmanbot.logic;

import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton;
import io.github.ageofwar.telejam.inline.InlineKeyboardButton;
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup;
import jdk.nashorn.internal.objects.annotations.Setter;

import java.util.ArrayList;
import java.util.List;

public class Hangman {

    private String word;
    private List<Character> guessedLetters;
    private List<Character> wrongLetters;
    private List<String> saidWords;
    private int errors;
    private int maxErrors;

    public Hangman(String word) {
        this.word = word;
        guessedLetters = new ArrayList<>();
        wrongLetters = new ArrayList<>();
        saidWords = new ArrayList<>();
    }

    public String getCurrentState() {
        StringBuilder currentWord = new StringBuilder();
        for (char c : word.toCharArray()) {
            if(guessedLetters.contains(c))
                currentWord.append(c);
            else
                currentWord.append('-');
        }

        return currentWord.toString();
    }

    public GuessResult guessLetter(char c) {
        if(guessedLetters.contains(c) || wrongLetters.contains(c))
            return GuessResult.LETTER_ALREADY_SAID;

        if (word.indexOf(c) != -1) {
            guessedLetters.add(c);
            return GuessResult.LETTER_GUESSED;
        } else {
            wrongLetters.add(c);
            errors++;
            return GuessResult.LETTER_WRONG;
        }
    }

    public GuessResult guessWord(String word) {
        if(saidWords.contains(word))
            return GuessResult.WORD_ALREADY_SAID;

        if(this.word.equalsIgnoreCase(word)) {
            return GuessResult.WORD_GUESSED;
        } else {
            saidWords.add(word.toLowerCase());
            errors++;
            return GuessResult.WORD_WRONG;
        }
    }

    public GuessResult getStatus() {
        if(areLetterRight() || saidWords.contains(word)) return GuessResult.MATCH_WIN;
        if(errors >= maxErrors) return GuessResult.MATCH_LOSE;
        return null;
    }

    private boolean areLetterRight() {
        for (char c : word.toCharArray())
            if(!guessedLetters.contains(c))
                return false;

        return true;
    }

    public InlineKeyboardMarkup generateKeyboard() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        for (Character character : guessedLetters)
            alphabet = alphabet.replaceAll(String.valueOf(character), "-");

        for (Character character : wrongLetters)
            alphabet = alphabet.replaceAll(String.valueOf(character), "-");

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (char c : alphabet.toCharArray()) {
            CallbackDataInlineKeyboardButton button = new CallbackDataInlineKeyboardButton(String.valueOf(c), "letter_" + c);
            buttons.add(button);
        }

        return InlineKeyboardMarkup.fromColumns(6, buttons);
    }

}
