
import java.util.*;

public class Trie {

    // Each node stores its child characters
    // and whether a complete word ends here.
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
    }

    private final TrieNode root = new TrieNode();

    // Insert a word into the Trie
    public void insert(String word) {
        TrieNode current = root;

        for (char ch : word.toLowerCase().toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }

        current.isEndOfWord = true;
    }

    // Search for a complete word
    public boolean search(String word) {
        TrieNode current = root;

        for (char ch : word.toLowerCase().toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return false;
            }

            current = current.children.get(ch);
        }

        return current.isEndOfWord;
    }

    // Check whether a prefix exists
    public boolean startsWith(String prefix) {
        TrieNode current = root;

        for (char ch : prefix.toLowerCase().toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return false;
            }

            current = current.children.get(ch);
        }

        return true;
    }
    
    // Auto-complete words for a given prefix
    public List<String> autoComplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode current = root;

        prefix = prefix.toLowerCase();

        // Find the node corresponding to the prefix
        for (char ch : prefix.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return results;
            }

            current = current.children.get(ch);
        }

        // Collect all complete words below this node
        collectWords(current, new StringBuilder(prefix), results);

        Collections.sort(results);
        return results;
    }

    // DFS traversal to collect complete words
    private void collectWords(
            TrieNode node,
            StringBuilder word,
            List<String> results) {

        if (node.isEndOfWord) {
            results.add(word.toString());
        }

        for (Map.Entry<Character, TrieNode> entry
                : node.children.entrySet()) {

            word.append(entry.getKey());

            collectWords(entry.getValue(), word, results);

            word.deleteCharAt(word.length() - 1);
        }
    }

    // Collect every complete word in the Trie
    public List<String> getAllWords() {
        List<String> results = new ArrayList<>();

        collectWords(root, new StringBuilder(), results);

        Collections.sort(results);
        return results;
    }

}
