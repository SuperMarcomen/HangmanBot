package it.marcodemartino.hangmanbot.languages;

import it.marcodemartino.hangmanbot.HangmanBot;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalizedWord {

    private final Random random = new Random();
    private final List<String> RANDOM_CATEGORIES_BLACKLIST = Arrays.asList("Java.txt", "Hearthstone.txt");

    public List<String> getAlphabetFromLocale(Locale locale) throws IOException {
        if (!HangmanBot.SUPPORTED_LANGUAGES.contains(locale)) locale = Locale.ENGLISH;

        return Files.readAllLines(
                Paths.get("alphabet/" + locale.getLanguage() + ".txt"),
                Charset.defaultCharset()
        );
    }

    public List<String> getCategoriesFromLocale(Locale locale) throws IOException {
        if (!HangmanBot.SUPPORTED_LANGUAGES.contains(locale)) locale = Locale.ENGLISH;

        Stream<Path> walk = Files.walk(Paths.get("words/" + locale.getLanguage() + "/"));

        return walk.filter(Files::isRegularFile)
                .map(x -> x.getFileName().toString().replace(".txt", "")).collect(Collectors.toList());
    }

    public String getRandomWordFromCategory(String category, Locale locale) throws IOException {
        if (!HangmanBot.SUPPORTED_LANGUAGES.contains(locale)) locale = Locale.ENGLISH;

        return getRandomWord(category, locale);
    }

    private String getRandomWord(String category, Locale locale) throws IOException {
        Path path = getRandomFile(category, locale);

        List<String> list = Files.readAllLines(path, Charset.defaultCharset());

        return list.get(random.nextInt(list.size()));
    }

    private Path getRandomFile(String category, Locale locale) throws IOException {
        if (!category.equals("random")) return Paths.get("words/" + locale.getLanguage() + "/" + category + ".txt");

        Stream<Path> walk = Files.walk(Paths.get("words/" + locale.getLanguage() + "/"));

        List<Path> result = walk.filter(Files::isRegularFile)
                .filter(path -> !RANDOM_CATEGORIES_BLACKLIST.contains(path.getFileName().toString()))
                .collect(Collectors.toList());

        return result.get(random.nextInt(result.size()));

    }
}
