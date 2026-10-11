# Spell Checker and Auto-Complete

A Java desktop spell checker and word auto-complete tool backed by a Trie. The project also includes a small console demo.

## Features

- Look up words in a dictionary.
- Suggest close spellings using Levenshtein edit distance.
- Find dictionary words matching a prefix.
- Load dictionary entries from `dictionary.txt`.

## Requirements

- Java Development Kit (JDK) 8 or later

## Run the desktop app

From the repository root, compile the source files and launch the Swing interface:

```powershell
javac -source 8 -target 8 Main.java Trie.java SpellChecker.java SpellCheckerGUI.java
java SpellCheckerGUI
```

Enter a word and select **Check Spelling**, or enter a prefix and select **Auto-Complete**. The dictionary file must remain in the working directory (`dictionary.txt` in the repository root).

## Run the console demo

```powershell
javac -source 8 -target 8 Main.java Trie.java SpellChecker.java
java Main
```

The demo loads the dictionary, prints auto-complete results for `app`, and checks the sample word `aple`.

## Dictionary format

`dictionary.txt` contains one word per line. Blank lines are ignored, and words are normalized to lowercase when loaded.
