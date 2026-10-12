
import java.util.List;

public class TrieTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void test(String name, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + name);
            passed++;
        } else {
            System.out.println("[FAIL] " + name);
            failed++;
        }
    }

    public static void main(String[] args) {

        Trie trie = new Trie();

        // Basic insertion and searching
        trie.insert("apple");
        trie.insert("application");
        trie.insert("apply");
        trie.insert("cat");

        test("Search existing word",
                trie.search("apple"));

        test("Search missing word",
                !trie.search("aple"));

        test("Search is case-insensitive",
                trie.search("APPLE"));

        test("Find existing prefix",
                trie.startsWith("app"));

        test("Reject missing prefix",
                !trie.startsWith("xyz"));

        test("Autocomplete returns matching words",
                trie.autoComplete("app").contains("apple")
                && trie.autoComplete("app").contains("apply")
                && trie.autoComplete("app").contains("application"));

        test("Autocomplete excludes unrelated words",
                !trie.autoComplete("app").contains("cat"));

        test("Missing prefix returns no suggestions",
                trie.autoComplete("xyz").isEmpty());

        // Check complete-word boundary
        test("Prefix alone is not a complete word",
                !trie.search("appl"));

        test("Empty prefix returns all inserted words",
                trie.autoComplete("").size() == 4);

        // Spell correction
        SpellChecker checker = new SpellChecker(trie);
        List<String> suggestions = checker.suggestCorrections("aple");

        test("Spell checker suggests apple",
                suggestions.contains("apple"));

        // Summary
        System.out.println("\n==========================");
        System.out.println("TEST SUMMARY");
        System.out.println("==========================");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total:  " + (passed + failed));

        if (failed > 0) {
            System.out.println("Some tests need attention.");
        } else {
            System.out.println("All tests passed!");
        }
    }
}
