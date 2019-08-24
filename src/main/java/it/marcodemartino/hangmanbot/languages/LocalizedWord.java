package it.marcodemartino.hangmanbot.languages;

import it.marcodemartino.hangmanbot.HangmanBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class LocalizedWord {

    public List<String> getAlphabetFromLocale(Locale locale) throws FileNotFoundException {
        if (!HangmanBot.SUPPORTED_LANGUAGES.contains(locale)) locale = Locale.ENGLISH;

        File file = new File("alphabet/" + locale.getLanguage() + ".txt");
        Scanner s = new Scanner(file, "UTF-8");
        List<String> list = new ArrayList<>();

        while (s.hasNextLine()) {
            list.add(s.nextLine());
        }
        s.close();
        return list;
    }

    public List<String> getCategoriesFromLocale(Locale locale) {
        if (!HangmanBot.SUPPORTED_LANGUAGES.contains(locale)) locale = Locale.ENGLISH;

        List<String> categories = new ArrayList<>();

        File folder = new File("words/" + locale.getLanguage() + "/");
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) continue;
            categories.add(file.getName().replace(".txt", ""));
        }

        return categories;
    }

    public String getRandomWordFromCategory(String category, Locale locale) throws FileNotFoundException {
        if (!HangmanBot.SUPPORTED_LANGUAGES.contains(locale)) locale = Locale.ENGLISH;

        return getRandomWord(category, locale);
    }

    private String getRandomWord(String category, Locale locale) throws FileNotFoundException {
        File file = getRandomFile(category, locale);

        Scanner s = new Scanner(file, "UTF-8");
        List<String> list = new ArrayList<>();
        while (s.hasNextLine()) {
            list.add(s.nextLine());
        }
        s.close();

        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    private File getRandomFile(String category, Locale locale) {
        if (!category.equals("random")) return new File("words/" + locale.getLanguage() + "/" + category + ".txt");

        File directory = new File("words/" + locale.getLanguage() + "/");
        List<File> files = Arrays.asList(directory.listFiles());
        files.remove("Java.txt");

        Random rand = new Random();
        return files.get(rand.nextInt(files.size()));
    }
}
