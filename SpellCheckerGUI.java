
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

public class SpellCheckerGUI extends JFrame {

    private final Trie trie = new Trie();
    private final SpellChecker checker = new SpellChecker(trie);

    private final JTextField inputField = new JTextField(25);
    private final JTextArea outputArea = new JTextArea();
    private final JLabel statusLabel =
            new JLabel("Loading dictionary...");

    private int wordCount = 0;

    public SpellCheckerGUI() {
        setTitle("Spell Checker & Auto-Complete");
        setSize(700, 520);
        setMinimumSize(new Dimension(550, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 24, 16, 24)
        );
        mainPanel.setBackground(new Color(245, 247, 250));

        // Heading
        JLabel title = new JLabel(
                "Spell Checker & Auto-Complete",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 23));
        title.setForeground(new Color(35, 65, 110));

        JLabel subtitle = new JLabel(
                "Powered by Trie Data Structure",
                SwingConstants.CENTER
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setOpaque(false);
        header.add(title);
        header.add(subtitle);

        // Search field
        JLabel inputLabel = new JLabel("Enter a word or prefix:");
        inputLabel.setFont(new Font("Arial", Font.BOLD, 14));

        inputField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputField.setMargin(new Insets(8, 10, 8, 10));

        JPanel inputPanel = new JPanel(new BorderLayout(0, 7));
        inputPanel.setOpaque(false);
        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(inputField, BorderLayout.CENTER);

        // Buttons
        JButton checkButton = new JButton("Check Spelling");
        JButton autoButton = new JButton("Auto-Complete");
        JButton clearButton = new JButton("Clear");

        checkButton.setBackground(new Color(45, 100, 190));
        checkButton.setForeground(Color.WHITE);
        checkButton.setFocusPainted(false);

        autoButton.setBackground(new Color(35, 140, 105));
        autoButton.setForeground(Color.WHITE);
        autoButton.setFocusPainted(false);

        clearButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 5)
        );
        buttonPanel.setOpaque(false);
        buttonPanel.add(checkButton);
        buttonPanel.add(autoButton);
        buttonPanel.add(clearButton);

        JPanel searchPanel = new JPanel(new BorderLayout(0, 10));
        searchPanel.setOpaque(false);
        searchPanel.add(inputPanel, BorderLayout.NORTH);
        searchPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Results area
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        outputArea.setMargin(new Insets(12, 12, 12, 12));
        outputArea.setText(
                "Welcome!\n\n"
                + "Enter a word to check its spelling,\n"
                + "or enter a prefix to find suggestions."
        );

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 215, 225)
                ),
                "Results"
        ));

        // Footer status
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(80, 90, 105));

        mainPanel.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.add(searchPanel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Button actions
        checkButton.addActionListener(e -> checkSpelling());
        autoButton.addActionListener(e -> autoComplete());

        clearButton.addActionListener(e -> {
            inputField.setText("");
            outputArea.setText("");
            statusLabel.setText(
                    "Dictionary words loaded: " + wordCount
            );
            inputField.requestFocusInWindow();
        });

        inputField.addActionListener(e -> checkSpelling());

        loadDictionary();
    }

    private void loadDictionary() {
        try (BufferedReader reader =
                new BufferedReader(new FileReader("dictionary.txt"))) {

            String word;

            while ((word = reader.readLine()) != null) {
                word = word.trim().toLowerCase();

                if (!word.isEmpty()) {
                    trie.insert(word);
                    wordCount++;
                }
            }

            statusLabel.setText(
                    "Dictionary loaded | Words read: " + wordCount
            );

        } catch (IOException e) {
            statusLabel.setText("Dictionary could not be loaded.");

            JOptionPane.showMessageDialog(
                    this,
                    "Could not read dictionary.txt.\n"
                    + "Place it in the working directory.",
                    "Dictionary Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void checkSpelling() {
        String word = inputField.getText().trim().toLowerCase();

        if (word.isEmpty()) {
            outputArea.setText("Please enter a word.");
            return;
        }

        if (trie.search(word)) {
            outputArea.setText(
                    "SPELLING RESULT\n\n"
                    + "'" + word + "' is in the dictionary."
            );
        } else {
            List<String> suggestions =
                    checker.suggestCorrections(word);

            outputArea.setText(
                    "SPELLING RESULT\n\n"
                    + "'" + word + "' was not found.\n\n"
                    + "Suggested corrections:\n"
                    + (suggestions.isEmpty()
                        ? "No close matches found."
                        : String.join("\n", suggestions))
            );
        }
    }

    private void autoComplete() {
        String prefix = inputField.getText().trim().toLowerCase();

        if (prefix.isEmpty()) {
            outputArea.setText("Please enter a prefix.");
            return;
        }

        List<String> suggestions = trie.autoComplete(prefix);

        outputArea.setText(
                "AUTO-COMPLETE RESULTS\n\n"
                + "Prefix: " + prefix + "\n\n"
                + (suggestions.isEmpty()
                    ? "No matching words found."
                    : String.join("\n", suggestions))
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SpellCheckerGUI gui = new SpellCheckerGUI();
            gui.setVisible(true);
        });
    }
}
