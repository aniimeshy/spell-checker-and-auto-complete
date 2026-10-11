
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        Trie trie = new Trie();

        // Load words from dictionary.txt
        try (BufferedReader br = new BufferedReader(
                new FileReader("dictionary.txt"))) {

            String word;

            while ((word = br.readLine()) != null) {
                word = word.trim().toLowerCase();

                if (!word.isEmpty()) {
                    trie.insert(word);
                }
            }

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
