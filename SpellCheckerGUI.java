import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SpellCheckerGUI extends JFrame {

    private final Trie trie = new Trie();
    private final SpellChecker checker = new SpellChecker(trie);

    private final AutoCompleteTextField inputField = new AutoCompleteTextField(28);
    private final JTextArea outputArea = new JTextArea();
    private final JLabel statusLabel = new JLabel("Loading dictionary...");
    private int wordCount = 0;

    private final Color textColor = new Color(241, 246, 255);
    private final Color mutedColor = new Color(190, 204, 232);
    private final Color glassFill = new Color(20, 38, 83, 155);
    private final Color glassBorder = new Color(220, 234, 255, 100);

    public SpellCheckerGUI() {
        setTitle("Spell Checker & Auto-Complete");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 680);
        setMinimumSize(new Dimension(680, 540));
        setLocationRelativeTo(null);

        BackgroundPanel background = new BackgroundPanel();
        background.setLayout(new BorderLayout(0, 18));
        background.setBorder(new EmptyBorder(24, 28, 20, 28));
        setContentPane(background);

        // Header
        JLabel title = new JLabel("✦  Spell Checker & Auto-Complete", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Powered by Trie Data Structure", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(mutedColor);

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setOpaque(false);
        header.add(title);
        header.add(subtitle);

        // Search/input glass card
        RoundedPanel searchCard = new RoundedPanel(glassFill, glassBorder, 24);
        searchCard.setLayout(new BorderLayout(0, 12));
        searchCard.setBorder(new EmptyBorder(18, 20, 16, 20));

        JLabel inputLabel = new JLabel("Enter a word or prefix");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        inputLabel.setForeground(textColor);

        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setSelectionColor(new Color(86, 113, 255, 170));
        inputField.setMargin(new Insets(11, 14, 11, 14));
        inputField.setOpaque(false);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(184, 202, 255, 170), 18),
                new EmptyBorder(2, 3, 2, 3)));

        JPanel fieldWrap = new JPanel(new BorderLayout());
        fieldWrap.setOpaque(false);
        fieldWrap.add(inputField, BorderLayout.CENTER);

        JButton checkButton = makeButton("✓  Check Spelling", new Color(75, 91, 255), new Color(75, 168, 255));
        JButton autoButton = makeButton("✧  Auto-Complete", new Color(0, 170, 178), new Color(27, 202, 161));
        JButton clearButton = makeButton("Clear", new Color(99, 119, 158), new Color(130, 145, 177));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 3));
        buttons.setOpaque(false);
        buttons.add(checkButton);
        buttons.add(autoButton);
        buttons.add(clearButton);

        JPanel inputStack = new JPanel(new BorderLayout(0, 8));
        inputStack.setOpaque(false);
        inputStack.add(inputLabel, BorderLayout.NORTH);
        inputStack.add(fieldWrap, BorderLayout.CENTER);

        searchCard.add(inputStack, BorderLayout.NORTH);
        searchCard.add(buttons, BorderLayout.SOUTH);

        // Results glass card
        RoundedPanel resultsCard = new RoundedPanel(new Color(18, 34, 74, 178), glassBorder, 24);
        resultsCard.setLayout(new BorderLayout(0, 10));
        resultsCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel resultsHeader = new JPanel(new BorderLayout());
        resultsHeader.setOpaque(false);
        JLabel resultsTitle = new JLabel("✧  Results");
        resultsTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        resultsTitle.setForeground(textColor);
        JLabel clickHint = new JLabel("Click a dictionary word to select it");
        clickHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clickHint.setForeground(mutedColor);
        resultsHeader.add(resultsTitle, BorderLayout.WEST);
        resultsHeader.add(clickHint, BorderLayout.EAST);

        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        outputArea.setForeground(new Color(237, 243, 255));
        outputArea.setCaretColor(Color.WHITE);
        outputArea.setBackground(new Color(10, 22, 52, 190));
        outputArea.setSelectionColor(new Color(82, 114, 255, 150));
        outputArea.setMargin(new Insets(14, 14, 14, 14));
        outputArea.setText("Welcome!\n\nType a prefix to see an inline suggestion in grey.\nPress Tab to accept the suggestion.\n\nUse the buttons to check spelling or list matching words.\nClick any listed dictionary word to put it in the input field.");

        outputArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int offset = outputArea.viewToModel2D(e.getPoint());
                    if (offset < 0) return;
                    int line = outputArea.getLineOfOffset(offset);
                    int start = outputArea.getLineStartOffset(line);
                    int end = outputArea.getLineEndOffset(line);
                    String all = outputArea.getText();
                    // Find the word under the mouse, rather than requiring the whole line
                    // to be a bare word.
                    int safeEnd = Math.min(end, all.length());
                    int wordStart = Math.min(Math.max(offset, start), safeEnd);
                    int wordEnd = wordStart;
                    while (wordStart > start && Character.isLetter(all.charAt(wordStart - 1))) wordStart--;
                    while (wordEnd < safeEnd && Character.isLetter(all.charAt(wordEnd))) wordEnd++;
                    String clicked = all.substring(wordStart, wordEnd).trim().toLowerCase();

                    if (!clicked.isEmpty() && clicked.matches("[a-z]+") && trie.search(clicked)) {
                        inputField.setText(clicked);
                        inputField.setCaretPosition(clicked.length());
                        inputField.requestFocusInWindow();
                        statusLabel.setText("Selected: " + clicked + "  •  Dictionary loaded: " + wordCount + " words");
                    }
                } catch (javax.swing.text.BadLocationException ignored) {
                    // Ignore clicks outside the text.
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(new RoundedLineBorder(new Color(174, 195, 240, 100), 16));
        scrollPane.getViewport().setBackground(new Color(10, 22, 52));
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        resultsCard.add(resultsHeader, BorderLayout.NORTH);
        resultsCard.add(scrollPane, BorderLayout.CENTER);

        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(mutedColor);
        statusLabel.setBorder(new EmptyBorder(0, 4, 0, 4));

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(searchCard, BorderLayout.NORTH);
        center.add(resultsCard, BorderLayout.CENTER);

        background.add(header, BorderLayout.NORTH);
        background.add(center, BorderLayout.CENTER);
        background.add(statusLabel, BorderLayout.SOUTH);

        checkButton.addActionListener(e -> checkSpelling());
        autoButton.addActionListener(e -> autoComplete());
        clearButton.addActionListener(e -> {
            inputField.setText("");
            outputArea.setText("");
            statusLabel.setText("Dictionary loaded: " + wordCount + " words");
            inputField.requestFocusInWindow();
        });
        inputField.addActionListener(e -> checkSpelling());

        loadDictionary();
        inputField.setSuggestionProvider(prefix -> {
            if (prefix.length() < 2) return "";
            List<String> matches = trie.autoComplete(prefix);
            for (String word : matches) {
                if (word.length() > prefix.length()) return word;
            }
            return "";
        });
    }

    private JButton makeButton(String text, Color start, Color end) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color a = getModel().isPressed() ? start.darker() : start;
                Color b = getModel().isPressed() ? end.darker() : end;
                g2.setPaint(new GradientPaint(0, 0, a, getWidth(), getHeight(), b));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 18, 11, 18));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadDictionary() {
        try (BufferedReader reader = new BufferedReader(new FileReader("dictionary.txt"))) {
            String word;
            while ((word = reader.readLine()) != null) {
                word = word.trim().toLowerCase();
                if (!word.isEmpty()) {
                    trie.insert(word);
                    wordCount++;
                }
            }
            statusLabel.setText("●  Dictionary loaded  •  Words read: " + wordCount);
        } catch (IOException ex) {
            statusLabel.setText("Dictionary could not be loaded.");
            JOptionPane.showMessageDialog(this,
                    "Could not read dictionary.txt.\nPlace it in the project working directory.",
                    "Dictionary Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkSpelling() {
        String word = inputField.getText().trim().toLowerCase();
        if (word.isEmpty()) {
            outputArea.setText("Please enter a word.");
            return;
        }

        if (trie.search(word)) {
            statusLabel.setText("Correct spelling  •  Dictionary loaded: " + wordCount + " words");
            outputArea.setText("SPELLING CHECK RESULT\n\n✓  \"" + word + "\" is in the dictionary.\n\nClick a listed dictionary word to select it.");
        } else {
            statusLabel.setText("Checking likely corrections for: " + word);
            List<String> suggestions = checker.suggestCorrections(word);
            outputArea.setText("SPELLING CHECK RESULT\n\n✕  \"" + word + "\" was not found.\n\nSuggested corrections:\n"
                    + (suggestions.isEmpty() ? "No close matches found." : String.join("\n", suggestions))
                    + "\n\nClick a suggested dictionary word to select it.");
        }
    }

    private void autoComplete() {
        String prefix = inputField.getText().trim().toLowerCase();
        if (prefix.isEmpty()) {
            outputArea.setText("Please enter a prefix.");
            return;
        }

        List<String> suggestions = trie.autoComplete(prefix);
        if (suggestions.size() > 80) {
            suggestions = suggestions.subList(0, 80);
        }
        outputArea.setText("AUTO-COMPLETE RESULTS\n\nPrefix: " + prefix + "\n\n"
                + (suggestions.isEmpty() ? "No matching words found." : String.join("\n", suggestions))
                + "\n\nClick any word above to insert it into the input field.");
    }

    private class BackgroundPanel extends JPanel {
        BackgroundPanel() { setOpaque(true); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, new Color(19, 31, 79),
                    w, h, new Color(63, 52, 121)));
            g2.fillRect(0, 0, w, h);

            // Soft, blurred-looking ambient color blooms, drawn as translucent ellipses.
            g2.setColor(new Color(60, 117, 255, 48));
            g2.fillOval(-w / 5, -h / 4, w / 2, h / 2);
            g2.setColor(new Color(180, 80, 200, 36));
            g2.fillOval(w / 2, h / 3, w / 2, h / 2);
            g2.setColor(new Color(0, 205, 200, 24));
            g2.fillOval(w / 3, -h / 5, w / 2, h / 2);

            // Dark translucent overlay improves text contrast.
            g2.setColor(new Color(7, 15, 40, 75));
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color outline;
        private final int arc;

        RoundedPanel(Color fill, Color outline, int arc) {
            this.fill = fill;
            this.outline = outline;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(outline);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedLineBorder extends javax.swing.border.AbstractBorder {
        private final Color color;
        private final int arc;

        RoundedLineBorder(Color color, int arc) {
            this.color = color;
            this.arc = arc;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(8, 10, 8, 10); }
    }

    private static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(111, 153, 255, 190);
            trackColor = new Color(10, 22, 52);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) { return zeroButton(); }

        @Override
        protected JButton createIncreaseButton(int orientation) { return zeroButton(); }

        private JButton zeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
            if (bounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4, 8, 8);
            g2.dispose();
        }
    }

    private static class AutoCompleteTextField extends JTextField {
        private Function<String, String> suggestionProvider;
        private String suggestion = "";

        AutoCompleteTextField(int columns) {
            super(columns);
            setFocusTraversalKeysEnabled(false);

            getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { updateLater(); }
                @Override public void removeUpdate(DocumentEvent e) { updateLater(); }
                @Override public void changedUpdate(DocumentEvent e) { updateLater(); }
            });

            getInputMap().put(KeyStroke.getKeyStroke("TAB"), "acceptSuggestion");
            getActionMap().put("acceptSuggestion", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!suggestion.isEmpty()) {
                        setText(suggestion);
                        setCaretPosition(getText().length());
                    } else {
                        transferFocus();
                    }
                }
            });
        }

        void setSuggestionProvider(Function<String, String> provider) {
            suggestionProvider = provider;
            updateLater();
        }

        private void updateLater() {
            SwingUtilities.invokeLater(() -> {
                String typed = getText();
                if (suggestionProvider == null || typed.trim().isEmpty() || typed.contains(" ")) {
                    suggestion = "";
                } else {
                    String candidate = suggestionProvider.apply(typed.toLowerCase());
                    suggestion = candidate != null
                            && candidate.toLowerCase().startsWith(typed.toLowerCase())
                            && candidate.length() > typed.length()
                            ? candidate : "";
                }
                repaint();
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            String typed = getText();
            if (suggestion.isEmpty() || typed.isEmpty()
                    || !suggestion.toLowerCase().startsWith(typed.toLowerCase())
                    || suggestion.length() <= typed.length()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont());
            g2.setColor(new Color(190, 202, 231, 190));
            FontMetrics fm = g2.getFontMetrics();
            Insets insets = getInsets();
            Insets margin = getMargin();
            int x = insets.left + margin.left + fm.stringWidth(typed);
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(suggestion.substring(typed.length()), x, y);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SpellCheckerGUI gui = new SpellCheckerGUI();
            gui.setVisible(true);
        });
    }
}
