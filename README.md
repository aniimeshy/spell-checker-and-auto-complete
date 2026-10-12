# Spell Checker & Auto-Complete

A Java project that combines a trie-based dictionary with a polished Swing interface for word checking and prefix auto-completion. It includes both a desktop GUI and a console demo that loads dictionary data and tests correction suggestions.

## Features

- Dictionary lookup for exact words
- Prefix-based auto-complete suggestions
- Misspelling detection with ranked correction suggestions
- Inline suggestion preview in the text field
- Clickable result words that insert directly into the input box
- Desktop UI built with Swing and custom glass-style panels

## Project structure

- `Trie.java` – trie implementation for insert/search/auto-complete operations
- `SpellChecker.java` – typo suggestion engine using edit-distance-based ranking
- `SpellCheckerGUI.java` – desktop application with the GUI
- `Main.java` – console demo for dictionary loading and testing
- `dictionary.txt` – main dictionary file used by the GUI
- `Dictionary_eng/` – CSV dictionary files used by the console demo

## Requirements

- Java Development Kit (JDK) 8 or later

## Run the desktop app

From the project root, compile and launch the Swing interface:

```powershell
javac -source 8 -target 8 Trie.java SpellChecker.java SpellCheckerGUI.java
java SpellCheckerGUI
```

Notes:

- The GUI expects `dictionary.txt` in the working directory.
- Type a word to check spelling or a prefix to trigger auto-complete.
- Press `Tab` to accept the displayed inline suggestion.

## Run the console demo

```powershell
javac -source 8 -target 8 Main.java Trie.java SpellChecker.java
java Main
```

The console demo loads the CSV dictionary files from `Dictionary_eng/`, prints auto-complete results for `app`, and checks the sample word `aple`.

## Dictionary formats

### GUI dictionary

`dictionary.txt` should contain one word per line, for example:

```text
apple
apricot
banana
```

The GUI reads the file, trims whitespace, lowercases each entry, and inserts it into the trie.

### Console dictionary

`Main.java` reads all `*.csv` files in `Dictionary_eng/`, splits entries by commas, normalizes them to lowercase, removes punctuation, and inserts valid words into the trie.

## Example usage

1. Start the GUI.
2. Enter a prefix such as `ap`.
3. Press `Tab` to accept the suggestion or use the auto-complete button.
4. Check a misspelled word such as `aple` to view corrections.
5. Click any word in the results panel to place it back into the input field.
