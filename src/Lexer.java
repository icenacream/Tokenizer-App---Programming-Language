import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private static final String[] KEYWORDS = {
        "abstract", "assert", "boolean", "break", "byte", "case", "catch",
        "char", "class", "const", "continue", "default", "do", "double",
        "else", "enum", "extends", "final", "finally", "float", "for",
        "goto", "if", "implements", "import", "instanceof", "int", "interface",
        "long", "native", "new", "package", "private", "protected",
        "public", "return", "short", "static", "strictfp", "super", "switch",
        "synchronized", "this", "throw", "throws", "transient", "try",
        "void", "volatile", "while", "String"
    };

    private static final char[] OPERATOR_CHARS = {
        '+', '-', '*', '/', '%', '=', '<', '>', '!', '&', '|', '^', '~'
    };

    private static final char[] DELIMITERS = {
        ';', ',', '.', '(', ')', '{', '}', '[', ']', ':'
    };

    private static final String[] TWO_CHAR_OPS = {
        "==", "!=", "<=", ">=", "&&", "||", "++", "--",
        "+=", "-=", "*=", "/=", "%=", "<<", ">>", "->"
    };

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            // 1. Skip whitespace
            if (Character.isWhitespace(c)) { i++; continue; }

            // 2. Number literal — int or float
            if (Character.isDigit(c)) {
                StringBuilder num = new StringBuilder();
                boolean isFloat = false;
                while (i < input.length() &&
                       (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                    if (input.charAt(i) == '.') isFloat = true;
                    num.append(input.charAt(i++));
                }
                tokens.add(new Token(num.toString(),
                        isFloat ? TokenType.FLOAT_LITERAL : TokenType.INT_LITERAL));
                continue;
            }

            // 3. String literal "..."
            if (c == '"') {
                StringBuilder str = new StringBuilder();
                str.append(c); i++;
                while (i < input.length() && input.charAt(i) != '"') {
                    if (input.charAt(i) == '\\' && i + 1 < input.length())
                        str.append(input.charAt(i++));
                    str.append(input.charAt(i++));
                }
                if (i < input.length()) str.append(input.charAt(i++));
                tokens.add(new Token(str.toString(), TokenType.STRING_LITERAL));
                continue;
            }

            // 4. Char literal '...'
            if (c == '\'') {
                StringBuilder ch = new StringBuilder();
                ch.append(c); i++;
                while (i < input.length() && input.charAt(i) != '\'')
                    ch.append(input.charAt(i++));
                if (i < input.length()) { ch.append(input.charAt(i)); i++; }
                tokens.add(new Token(ch.toString(), TokenType.CHAR_LITERAL));
                continue;
            }

            // 5. Identifier, Keyword, or Boolean/Null literal
            if (Character.isLetter(c) || c == '_') {
                StringBuilder word = new StringBuilder();
                while (i < input.length() &&
                       (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_'))
                    word.append(input.charAt(i++));
                String w = word.toString();
                TokenType type;
                if (w.equals("true") || w.equals("false")) type = TokenType.BOOLEAN_LITERAL;
                else if (w.equals("null"))                  type = TokenType.NULL_LITERAL;
                else if (isKeyword(w))                      type = TokenType.KEYWORD;
                else                                        type = TokenType.IDENTIFIER;
                tokens.add(new Token(w, type));
                continue;
            }

            // 6. Two-char or single-char operator
            if (isOperatorChar(c)) {
                if (i + 1 < input.length()) {
                    String two = "" + c + input.charAt(i + 1);
                    if (isTwoCharOp(two)) {
                        tokens.add(new Token(two, TokenType.OPERATOR));
                        i += 2; continue;
                    }
                }
                tokens.add(new Token(String.valueOf(c), TokenType.OPERATOR));
                i++; continue;
            }

            // 7. Delimiter
            if (isDelimiter(c)) {
                tokens.add(new Token(String.valueOf(c), TokenType.DELIMITER));
                i++; continue;
            }

            // 8. Unknown
            tokens.add(new Token(String.valueOf(c), TokenType.UNKNOWN));
            i++;
        }

        return tokens;
    }

    private boolean isKeyword(String w) {
        for (String k : KEYWORDS) if (k.equals(w)) return true;
        return false;
    }

    private boolean isOperatorChar(char c) {
        for (char o : OPERATOR_CHARS) if (o == c) return true;
        return false;
    }

    private boolean isDelimiter(char c) {
        for (char d : DELIMITERS) if (d == c) return true;
        return false;
    }

    private boolean isTwoCharOp(String s) {
        for (String op : TWO_CHAR_OPS) if (op.equals(s)) return true;
        return false;
    }
}