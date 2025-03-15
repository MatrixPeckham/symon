/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.loomcom.symon.assembler;

/**
 *
 * @author matri
 */
public class Token {

    public Token(Type type, String lexeme, String literal, int number,
            int line, int absSourceOffset, int length) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.number = number;
        this.line = line;
        this.absSourceOffset = absSourceOffset;
        this.length = length;
    }

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }

    public static enum Type {
        IDENTIFIER,
        OPCODE,
        NUMBER,
        POUND,
        DOLLAR,
        LEFT_PAREN,
        RIGHT_PAREN,
        LEFT_BRACE,
        RIGHT_BRACE,
        COMMA,
        PLUS,
        MINUS,
        EQUAL,
        EQUAL_EQUAL,
        NOT_EQUAL,
        PERCENT,
        STAR,
        SLASH,
        GT,
        LT,
        GE,
        LE,
        COLON,
        EXCLAMATION,
        STRING,
        COMMENT,
        ERROR,
        ORG,
        ALIGN,
        BYTE,
        CONST,
        WORD,
        DWORD,
        TEXT,
        FILL,
        FILLWORD,
        IMPORT,
        EOF;

        public static boolean isSymbol(Type t) {
            return t == LEFT_BRACE || t == LEFT_PAREN || t == RIGHT_BRACE || t
                    == RIGHT_PAREN || t == GT || t == GE || t == LT || t == LE
                    || t == EXCLAMATION || t == PLUS || t == MINUS || t == SLASH
                    || t == STAR || t == NOT_EQUAL || t == COMMA || t == EQUAL
                    || t == EQUAL_EQUAL || t == COLON;
        }

    }

    public final Type type;

    public final String lexeme;

    public final String literal;

    public final int line;

    public final int number;

    public final int absSourceOffset;

    public final int length;

    public boolean error = false;

    public String tooltip = null;

}
