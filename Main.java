
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static void loadDictionaryFromCsvFolder(Trie trie) throws IOException {
        Path dictionaryFolder = Paths.get("Dictionary_eng");

        if (!Files.isDirectory(dictionaryFolder)) {
            throw new IOException("Dictionary_eng folder not found.");
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                dictionaryFolder, "*.csv")) {

            for (Path csvFile : stream) {
                try (BufferedReader br = new BufferedReader(
                        new FileReader(csvFile.toFile()))) {

                    String line;

                    while ((line = br.readLine()) != null) {
                        addWordsFromLine(trie, line);
                    }
                }
            }
        }
    }

    private static void addWordsFromLine(Trie trie, String line) {
        if (line == null) {
            return;
        }

        String[] parts = line.split(",");

        for (String part : parts) {
            String word = part.trim().toLowerCase().replace("\"", "");
            word = word.replaceAll("[^a-z'\\-]", "");

            if (!word.isEmpty()) {
                trie.insert(word);
            }
        }
    }

    public static void main(String[] args) {

        Trie trie = new Trie();

        try {
            loadDictionaryFromCsvFolder(trie);
            System.out.println("Dictionary loaded successfully!");

        } catch (IOException e) {
            System.out.println("Error loading dictionary: "
                    + e.getMessage());
            return;
        }

        // Test auto-complete
        System.out.println("\nAuto-complete for 'app':");
        System.out.println(trie.autoComplete("app"));

        // Test spell checker
        SpellChecker checker = new SpellChecker(trie);
        String word = "aple";

        System.out.println("\nChecking word: " + word);

        if (trie.search(word)) {
            System.out.println("Correct spelling!");
        } else {
            System.out.println("Misspelled word!");
            System.out.println(
                    "Suggestions: "
                    + checker.suggestCorrections(word));
        }
    }
}
