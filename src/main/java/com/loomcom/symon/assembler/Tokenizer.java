/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.loomcom.symon.assembler;

import static com.loomcom.symon.assembler.Token.Type.*;
import static java.util.logging.Level.WARNING;

import com.loomcom.symon.InstructionTable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author matri
 */
public class Tokenizer {

    private String source;

    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;

    private int current = 0;

    private int line = 0;

    private boolean cullComments = true;

    public void shouldCullComments(boolean cull) {
        cullComments = cull;
    }

    public Tokenizer(String source) {
        this.source = source;
    }

    public void setString(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        start = 0;
        current = 0;
        line = 0;
        tokens.clear();
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", "\u0000", 0, line, current, 0)
        );
        return tokens;
    }

    private boolean isAtEnd() {
        return current == source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                identifier();
                break;
            case '#':
                addToken(POUND);
                break;
            case ':':
                addToken(COLON);
                break;
            case '%':
                if (isDigit(peek())) {
                    binaryNumber();
                } else {
                    addToken(PERCENT);
                }
                break;
            case '$':
                if (isDigit(peek())) {
                    hexNumber();
                } else {
                    addToken(DOLLAR);
                }
                break;
            case '+':
                addToken(PLUS);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(match('=') ? NOT_EQUAL : EXCLAMATION);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LE : LT);
                break;
            case '>':
                addToken(match('=') ? GE : GT);
                break;
            case '/':
                if (match('/')) {
                    comment();
                } else {
                    addToken(SLASH);
                }
                break;
            case ';':
                comment();
                break;
            case ' ':
            case '\t':
            case '\r':
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if (isDigit(c)) {
                    decNumber();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Logger.getLogger(getClass().getCanonicalName()).log(WARNING,
                            "Error Tokenizing unexpected symbol:" + c
                            + " at line " + line);
                    addToken(ERROR);
                    tokens.getLast().error = true;
                    tokens.getLast().tooltip = "Symbol not recognized";
                }
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        Token.Type type = IDENTIFIER;
        String text = source.substring(start, current);
        String tooltip = null;
        boolean err = false;
        if (InstructionTable.pneumonics.containsKey(text)) {
            type = OPCODE;
            tooltip = InstructionTable.pneumonics.get(text);
        }
        if (text.charAt(0) == '.') {
            String directive = text.substring(1);
            if (Parser.directives.containsKey(directive)) {
                switch (directive) {
                    case "org":
                        type = ORG;
                        break;
                    case "align":
                        type = ALIGN;
                        break;
                    case "by":
                    case "byte":
                        type = BYTE;
                        break;
                    case "const":
                        type = CONST;
                        break;
                    case "dw":
                    case "dword":
                        type = DWORD;
                        break;
                    case "fill":
                        type = FILL;
                        break;
                    case "fillword":
                        type = FILLWORD;
                        break;
                    case "te":
                    case "text":
                        type = TEXT;
                        break;
                    case "wo":
                    case "word":
                        type = WORD;
                        break;
                    case "import":
                        type = IMPORT;
                        break;
                }
                tooltip = Parser.directives.get(directive);
            } else {
                err = true;
                tooltip = "Unknown Directive";
            }
        }

        addToken(type);

        tokens.getLast().error = err;
        tokens.getLast().tooltip = tooltip;
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isDigit(c) || isAlpha(c);
    }

    private void decNumber() {
        number();
        int num = Integer.parseInt(source.substring(start, current), 10);
        addToken(NUMBER, source.substring(start, current), num);
    }

    private void hexNumber() {
        number();
        int num = Integer.parseInt(source.substring(start + 1, current), 16);
        addToken(NUMBER, source.substring(start, current), num);
    }

    private void binaryNumber() {
        number();
        int num = Integer.parseInt(source.substring(start + 1, current), 2);
        addToken(NUMBER, source.substring(start, current), num);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }
        if (!isAtEnd()) {
            advance();
            String value = source.substring(start + 1, current - 1);
            addToken(STRING, value);
        } else {
            String value = source.substring(start + 1, current);
            addToken(STRING, value);
            Logger.getLogger(getClass().getCanonicalName()).log(WARNING,
                    "Error Tokenizing: unclosed String");
            tokens.getLast().error = true;
            tokens.getLast().tooltip = "Unclosed String";
        }
    }

    private void comment() {
        while (peek() != '\n' && !isAtEnd()) {
            advance();
        }
        if (!cullComments) {
            addToken(COMMENT);
        }
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(Token.Type type) {
        addToken(type, null);
    }

    private void addToken(Token.Type type, String literal) {
        addToken(type, literal, 0);
    }

    private void addToken(Token.Type type, String literal, int number) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, 0, line, start, text.
                length()));
    }

}
