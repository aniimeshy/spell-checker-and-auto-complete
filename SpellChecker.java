import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Spell checker with typo-tolerant ranking.
 * Supports insertion, deletion, substitution, and adjacent transposition.
 */
public class SpellChecker {
    private final Trie dictionary;

    public SpellChecker(Trie dictionary) {
        this.dictionary = dictionary;
    }

    public List<String> suggestCorrections(String input) {
        List<String> results = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) return results;

        String word = normalize(input);
        List<String> allWords = dictionary.getAllWords();
        List<Candidate> matches = new ArrayList<>();

        // A little more tolerance for longer words, where several typos can occur.
        int maxDistance = word.length() <= 4 ? 1
                : word.length() <= 7 ? 2
                : word.length() <= 10 ? 3 : 4;

        for (String candidateRaw : allWords) {
            if (candidateRaw == null) continue;
            String candidate = normalize(candidateRaw);
            if (candidate.isEmpty() || candidate.equals(word)) continue;
            if (Math.abs(candidate.length() - word.length()) > maxDistance) continue;

            int distance = damerauLevenshtein(word, candidate);
            if (distance > maxDistance) continue;

            double overlap = bigramDice(word, candidate);
            int samePosition = samePositionLetters(word, candidate);
            int prefix = commonPrefixLength(word, candidate);

            // Lower score is better. Edit distance is the strongest signal;
            // overlap and matching positions break ties more intelligently.
            double score = distance
                    - overlap * 0.22
                    - ((double) samePosition / Math.max(word.length(), candidate.length())) * 0.12
                    - Math.min(prefix, 4) * 0.025;

            matches.add(new Candidate(candidateRaw, distance, score, overlap));
        }

        Collections.sort(matches, Comparator
                .comparingInt((Candidate c) -> c.distance)
                .thenComparingDouble(c -> c.score)
                .thenComparing((Candidate c) -> -c.overlap)
                .thenComparing(c -> c.word));

        int limit = Math.min(10, matches.size());
        for (int i = 0; i < limit; i++) results.add(matches.get(i).word);
        return results;
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private int damerauLevenshtein(String a, String b) {
        int n = a.length(), m = b.length();
        if (n == 0) return m;
        if (m == 0) return n;

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) d[i][0] = i;
        for (int j = 0; j <= m; j++) d[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                int best = Math.min(
                        Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1),
                        d[i - 1][j - 1] + cost);

                if (i > 1 && j > 1
                        && a.charAt(i - 1) == b.charAt(j - 2)
                        && a.charAt(i - 2) == b.charAt(j - 1)) {
                    best = Math.min(best, d[i - 2][j - 2] + 1);
                }
                d[i][j] = best;
            }
        }
        return d[n][m];
    }

    private double bigramDice(String a, String b) {
        if (a.length() < 2 || b.length() < 2) return a.equals(b) ? 1.0 : 0.0;
        List<String> pairs = new ArrayList<>();
        for (int i = 0; i < a.length() - 1; i++) pairs.add(a.substring(i, i + 2));
        int matched = 0;
        boolean[] used = new boolean[b.length() - 1];
        for (String pair : pairs) {
            for (int j = 0; j < b.length() - 1; j++) {
                if (!used[j] && pair.equals(b.substring(j, j + 2))) {
                    used[j] = true;
                    matched++;
                    break;
                }
            }
        }
        return (2.0 * matched) / ((a.length() - 1) + (b.length() - 1));
    }

    private int samePositionLetters(String a, String b) {
        int count = 0;
        for (int i = 0; i < Math.min(a.length(), b.length()); i++) {
            if (a.charAt(i) == b.charAt(i)) count++;
        }
        return count;
    }

    private int commonPrefixLength(String a, String b) {
        int i = 0;
        while (i < Math.min(a.length(), b.length()) && a.charAt(i) == b.charAt(i)) i++;
        return i;
    }

    private static class Candidate {
        final String word;
        final int distance;
        final double score;
        final double overlap;

        Candidate(String word, int distance, double score, double overlap) {
            this.word = word;
            this.distance = distance;
            this.score = score;
            this.overlap = overlap;
        }
    }
}
