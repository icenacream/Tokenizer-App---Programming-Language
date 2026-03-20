# Lexemes Tokenizer

A desktop application built in pure Java that performs **lexical analysis** — the process of reading a line of code and breaking it down into its smallest meaningful components called **tokens**.

This is the first and most fundamental stage of how any compiler or interpreter understands source code. Before a program can be executed or checked for errors, it must first be broken apart into recognizable pieces — and that is exactly what this application does.

---

## What It Does

The application accepts any line of Java-like source code as input and analyzes it character by character. As it reads through the input, it groups characters together into tokens and identifies what each one represents. The results are then displayed as color-coded cards, each showing the token's value and its exact type.

For example, given the input `int x = 5 + 3;`, the application produces:

| Token | Type |
|-------|------|
| `int` | Keyword |
| `x` | Identifier |
| `=` | Operator |
| `5` | Int Literal |
| `+` | Operator |
| `3` | Int Literal |
| `;` | Delimiter |

---

## Token Types

The application recognizes the following token categories:

| Type | Description | Examples |
|------|-------------|---------|
| **Keyword** | Reserved words that have a fixed meaning in Java | `int`, `if`, `while`, `return`, `class`, `void` |
| **Identifier** | Names defined by the programmer for variables, methods, or classes | `x`, `myVar`, `total`, `getName` |
| **Operator** | Symbols that perform operations, including two-character operators | `+`, `-`, `=`, `==`, `!=`, `&&`, `++` |
| **Int Literal** | A whole number value | `0`, `42`, `100` |
| **Float Literal** | A decimal number value | `3.14`, `0.5`, `2.0` |
| **String Literal** | Text enclosed in double quotes | `"hello"`, `"Alice"` |
| **Char Literal** | A single character enclosed in single quotes | `'a'`, `'\n'` |
| **Boolean Literal** | A true or false value | `true`, `false` |
| **Null Literal** | The absence of a value | `null` |
| **Delimiter** | Punctuation that separates or groups parts of code | `;`, `,`, `(`, `)`, `{`, `}`, `[`, `]` |
| **Unknown** | Any character that does not fit a recognized category | `@`, `#`, `?` |

---

## How It Works

The core of the application is a hand-written **lexer** — built entirely from scratch without any external libraries or tools. It reads the input one character at a time and uses a set of rules to decide where each token begins and ends, and what type it belongs to. This includes handling edge cases like two-character operators (`==`, `&&`, `++`), escape characters inside strings, and correctly separating boolean and null literals from regular identifiers.

The user interface is built with **Java Swing**, which is included in the Java Development Kit and requires no additional installation. It features a dark-themed code editor aesthetic with each token type assigned its own distinct color, making it easy to visually scan and understand the structure of any input at a glance.

---

## Project Structure

| File | Role |
|------|------|
| `TokenType.java` | Defines all possible token categories as an enum |
| `Token.java` | Represents a single token — stores its value and type |
| `Lexer.java` | The tokenizer logic — reads input and produces a list of tokens |
| `Main.java` | The Swing GUI — displays the interface and handles user interaction |