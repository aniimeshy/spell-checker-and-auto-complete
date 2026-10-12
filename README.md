# Spell Checker and Auto-Complete

A Java desktop spell checker and word auto-complete tool backed by a Trie. The project also includes a small console demo.

## Features

- Look up words in a dictionary.
- Suggest close spellings using Levenshtein edit distance.
- Find dictionary words matching a prefix.
- Load dictionary entries from the Oxford CSV files in `Dictionary_eng/`.

## Requirements

- Java Development Kit (JDK) 8 or later

## Run the desktop app

From the repository root, compile the source files and launch the Swing interface:

```powershell
javac -source 8 -target 8 Main.java Trie.java SpellChecker.java SpellCheckerGUI.java
java SpellCheckerGUI
```

Enter a word and select **Check Spelling**, or enter a prefix and select **Auto-Complete**. The Oxford dictionary folder (`Dictionary_eng/`) must remain in the working directory.

## Run the console demo

```powershell
javac -source 8 -target 8 Main.java Trie.java SpellChecker.java
java Main
```

The demo loads the dictionary, prints auto-complete results for `app`, and checks the sample word `aple`.

## Dictionary format

The project reads all `*.csv` files inside `Dictionary_eng/`. Each line is normalized to lowercase and cleaned before being inserted into the trie.
