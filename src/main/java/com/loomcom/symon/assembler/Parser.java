/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.loomcom.symon.assembler;

import static com.loomcom.symon.assembler.Token.Type.*;

import com.loomcom.symon.InstructionTable;
import com.loomcom.symon.InstructionTable.Mode;
import com.loomcom.symon.assembler.ast.Expr;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author matri
 */
public class Parser {

    public static final HashMap<String, String> directives = new HashMap<>(
            Stream.of(new String[][]{
        {"org", "Location of next instruction"},
        //{"align", "Aligns memory position"},
        {"by", "Outputs bystes"},
        {"byte", "Outputs bystes"},
        {"const", "Defines a constant"},
        {"dw", "outputs doublewords 4 bytes"},
        {"dword", "outputs doublewords 4 bytes"},
        {"fill", "outputs a number of bytes"},
        {"fillword", "outputs a number of words"},
        {"te", "outputs text"},
        {"text", "outputs text"},
        {"wo", "outputs words"},
        {"word", "outputs words"}, //{"import", "outputs a number of words"}, //end
    }).collect(Collectors.toMap(
                    data ->
                    data[0], data -> data[1])));

    /*
     * (label:)? opcode args?
     */
    public static final HashMap<String, HashMap<InstructionTable.Mode, Integer>> machineCodes;

    static {
        machineCodes = new HashMap<>();
        HashMap<InstructionTable.Mode, Integer> temp = new HashMap<>();
        machineCodes.put("ADC", temp);
        temp.put(Mode.IMM, 0x69);
        temp.put(Mode.ZPG, 0x65);
        temp.put(Mode.ZPX, 0x75);
        temp.put(Mode.ABS, 0x6D);
        temp.put(Mode.ABX, 0x7D);
        temp.put(Mode.ABY, 0x79);
        temp.put(Mode.XIN, 0x61);
        temp.put(Mode.INY, 0x71);
        temp.put(Mode.ZPI, 0x72);

        temp = new HashMap<>();
        machineCodes.put("AND", temp);
        temp.put(Mode.IMM, 0x29);
        temp.put(Mode.ZPG, 0x25);
        temp.put(Mode.ZPX, 0x35);
        temp.put(Mode.ABS, 0x2D);
        temp.put(Mode.ABX, 0x3D);
        temp.put(Mode.ABY, 0x39);
        temp.put(Mode.XIN, 0x21);
        temp.put(Mode.INY, 0x31);
        temp.put(Mode.ZPI, 0x32);

        temp = new HashMap<>();
        machineCodes.put("ASL", temp);
        temp.put(Mode.IMP, 0x0A);
        temp.put(Mode.ZPG, 0x06);
        temp.put(Mode.ZPX, 0x16);
        temp.put(Mode.ABS, 0x0E);
        temp.put(Mode.ABX, 0x1E);

        temp = new HashMap<>();
        machineCodes.put("BBR0", temp);
        temp.put(Mode.ZPR, 0x0F);
        temp = new HashMap<>();
        machineCodes.put("BBR1", temp);
        temp.put(Mode.ZPR, 0x1F);
        temp = new HashMap<>();
        machineCodes.put("BBR2", temp);
        temp.put(Mode.ZPR, 0x2F);
        temp = new HashMap<>();
        machineCodes.put("BBR3", temp);
        temp.put(Mode.ZPR, 0x3F);
        temp = new HashMap<>();
        machineCodes.put("BBR4", temp);
        temp.put(Mode.ZPR, 0x4F);
        temp = new HashMap<>();
        machineCodes.put("BBR5", temp);
        temp.put(Mode.ZPR, 0x5F);
        temp = new HashMap<>();
        machineCodes.put("BBR6", temp);
        temp.put(Mode.ZPR, 0x6F);
        temp = new HashMap<>();
        machineCodes.put("BBR7", temp);
        temp.put(Mode.ZPR, 0x7F);
        temp = new HashMap<>();
        machineCodes.put("BBS0", temp);
        temp.put(Mode.ZPR, 0x8F);
        temp = new HashMap<>();
        machineCodes.put("BBS1", temp);
        temp.put(Mode.ZPR, 0x9F);
        temp = new HashMap<>();
        machineCodes.put("BBS2", temp);
        temp.put(Mode.ZPR, 0xAF);
        temp = new HashMap<>();
        machineCodes.put("BBS3", temp);
        temp.put(Mode.ZPR, 0xBF);
        temp = new HashMap<>();
        machineCodes.put("BBS4", temp);
        temp.put(Mode.ZPR, 0xCF);
        temp = new HashMap<>();
        machineCodes.put("BBS5", temp);
        temp.put(Mode.ZPR, 0xDF);
        temp = new HashMap<>();
        machineCodes.put("BBS6", temp);
        temp.put(Mode.ZPR, 0xEF);
        temp = new HashMap<>();
        machineCodes.put("BBS7", temp);
        temp.put(Mode.ZPR, 0xFF);

        temp = new HashMap<>();
        machineCodes.put("BCC", temp);
        temp.put(Mode.REL, 0x90);

        temp = new HashMap<>();
        machineCodes.put("BCS", temp);
        temp.put(Mode.REL, 0xB0);

        temp = new HashMap<>();
        machineCodes.put("BEQ", temp);
        temp.put(Mode.REL, 0xF0);

        temp = new HashMap<>();
        machineCodes.put("BIT", temp);
        temp.put(Mode.IMM, 0x89);
        temp.put(Mode.ZPG, 0x24);
        temp.put(Mode.ZPX, 0x34);
        temp.put(Mode.ABS, 0x2C);
        temp.put(Mode.ABX, 0x3C);

        temp = new HashMap<>();
        machineCodes.put("BMI", temp);
        temp.put(Mode.REL, 0x30);

        temp = new HashMap<>();
        machineCodes.put("BNE", temp);
        temp.put(Mode.REL, 0xD0);

        temp = new HashMap<>();
        machineCodes.put("BPL", temp);
        temp.put(Mode.REL, 0x10);

        temp = new HashMap<>();
        machineCodes.put("BRA", temp);
        temp.put(Mode.REL, 0x80);

        temp = new HashMap<>();
        machineCodes.put("BRK", temp);
        temp.put(Mode.IMP, 0x00);

        temp = new HashMap<>();
        machineCodes.put("BVC", temp);
        temp.put(Mode.REL, 0x50);
        temp = new HashMap<>();
        machineCodes.put("BVS", temp);
        temp.put(Mode.REL, 0x70);
        temp = new HashMap<>();
        machineCodes.put("CLC", temp);
        temp.put(Mode.IMP, 0x18);
        temp = new HashMap<>();
        machineCodes.put("CLD", temp);
        temp.put(Mode.IMP, 0xD8);
        temp = new HashMap<>();
        machineCodes.put("CLI", temp);
        temp.put(Mode.IMP, 0x58);
        temp = new HashMap<>();
        machineCodes.put("CLV", temp);
        temp.put(Mode.IMP, 0xB8);

        temp = new HashMap<>();
        machineCodes.put("CMP", temp);
        temp.put(Mode.IMM, 0xC9);
        temp.put(Mode.ZPG, 0xC5);
        temp.put(Mode.ZPX, 0xD5);
        temp.put(Mode.ABS, 0xCD);
        temp.put(Mode.ABX, 0xDD);
        temp.put(Mode.ABY, 0xD9);
        temp.put(Mode.XIN, 0xC1);
        temp.put(Mode.INY, 0xD1);
        temp.put(Mode.ZPI, 0xD2);

        temp = new HashMap<>();
        machineCodes.put("CPX", temp);
        temp.put(Mode.IMM, 0xE0);
        temp.put(Mode.ZPG, 0xE4);
        temp.put(Mode.ABS, 0xEC);

        temp = new HashMap<>();
        machineCodes.put("CPY", temp);
        temp.put(Mode.IMM, 0xC0);
        temp.put(Mode.ZPG, 0xC4);
        temp.put(Mode.ABS, 0xCC);

        temp = new HashMap<>();
        machineCodes.put("DEA", temp);
        temp.put(Mode.IMP, 0x3A);

        temp = new HashMap<>();
        machineCodes.put("DEC", temp);
        temp.put(Mode.ZPG, 0xC6);
        temp.put(Mode.ZPX, 0xD6);
        temp.put(Mode.ABS, 0xCE);
        temp.put(Mode.ABX, 0xDE);

        temp = new HashMap<>();
        machineCodes.put("DEX", temp);
        temp.put(Mode.IMP, 0xCA);

        temp = new HashMap<>();
        machineCodes.put("DEY", temp);
        temp.put(Mode.IMP, 0x88);

        temp = new HashMap<>();
        machineCodes.put("EOR", temp);
        temp.put(Mode.IMM, 0x49);
        temp.put(Mode.ZPG, 0x45);
        temp.put(Mode.ZPX, 0x55);
        temp.put(Mode.ABS, 0x4D);
        temp.put(Mode.ABX, 0x5D);
        temp.put(Mode.ABY, 0x59);
        temp.put(Mode.XIN, 0x41);
        temp.put(Mode.INY, 0x51);
        temp.put(Mode.ZPI, 0x52);

        temp = new HashMap<>();
        machineCodes.put("INA", temp);
        temp.put(Mode.IMP, 0x1A);

        temp = new HashMap<>();
        machineCodes.put("INC", temp);
        temp.put(Mode.ZPG, 0xE6);
        temp.put(Mode.ZPX, 0xF6);
        temp.put(Mode.ABS, 0xEE);
        temp.put(Mode.ABX, 0xFE);

        temp = new HashMap<>();
        machineCodes.put("INX", temp);
        temp.put(Mode.IMP, 0xE8);

        temp = new HashMap<>();
        machineCodes.put("INY", temp);
        temp.put(Mode.IMP, 0xC8);

        temp = new HashMap<>();
        machineCodes.put("JMP", temp);
        temp.put(Mode.ABS, 0x4C);
        temp.put(Mode.IND, 0x6C);
        temp.put(Mode.AIX, 0x7C);

        temp = new HashMap<>();
        machineCodes.put("JSR", temp);
        temp.put(Mode.ABS, 0x20);

        temp = new HashMap<>();
        machineCodes.put("LDA", temp);
        temp.put(Mode.IMM, 0xA9);
        temp.put(Mode.ZPG, 0xA5);
        temp.put(Mode.ZPX, 0xB5);
        temp.put(Mode.ABS, 0xAD);
        temp.put(Mode.ABX, 0xBD);
        temp.put(Mode.ABY, 0xB9);
        temp.put(Mode.XIN, 0xA1);
        temp.put(Mode.INY, 0xB1);
        temp.put(Mode.ZPI, 0xB2);

        temp = new HashMap<>();
        machineCodes.put("LDX", temp);
        temp.put(Mode.IMM, 0xA2);
        temp.put(Mode.ZPG, 0xA6);
        temp.put(Mode.ZPY, 0xB6);
        temp.put(Mode.ABS, 0xAE);
        temp.put(Mode.ABY, 0xBE);

        temp = new HashMap<>();
        machineCodes.put("LDY", temp);
        temp.put(Mode.IMM, 0xA0);
        temp.put(Mode.ZPG, 0xA4);
        temp.put(Mode.ZPX, 0xB4);
        temp.put(Mode.ABS, 0xAC);
        temp.put(Mode.ABX, 0xBC);

        temp = new HashMap<>();
        machineCodes.put("LSR", temp);
        temp.put(Mode.ACC, 0x4A);
        temp.put(Mode.ZPG, 0x46);
        temp.put(Mode.ZPX, 0x56);
        temp.put(Mode.ABS, 0x4E);
        temp.put(Mode.ABX, 0x5E);

        temp = new HashMap<>();
        machineCodes.put("NOP", temp);
        temp.put(Mode.IMP, 0xEA);

        temp = new HashMap<>();
        machineCodes.put("ORA", temp);
        temp.put(Mode.IMM, 0x09);
        temp.put(Mode.ZPG, 0x05);
        temp.put(Mode.ZPX, 0x15);
        temp.put(Mode.ABS, 0x0D);
        temp.put(Mode.ABX, 0x1D);
        temp.put(Mode.ABY, 0x19);
        temp.put(Mode.XIN, 0x01);
        temp.put(Mode.INY, 0x11);
        temp.put(Mode.ZPI, 0x12);

        temp = new HashMap<>();
        machineCodes.put("PHA", temp);
        temp.put(Mode.IMP, 0x48);
        temp = new HashMap<>();
        machineCodes.put("PHX", temp);
        temp.put(Mode.IMP, 0xDA);
        temp = new HashMap<>();
        machineCodes.put("PHY", temp);
        temp.put(Mode.IMP, 0x5A);

        temp = new HashMap<>();
        machineCodes.put("PLA", temp);
        temp.put(Mode.IMP, 0x68);
        temp = new HashMap<>();
        machineCodes.put("PLX", temp);
        temp.put(Mode.IMP, 0xFA);
        temp = new HashMap<>();
        machineCodes.put("PLY", temp);
        temp.put(Mode.IMP, 0x7A);

        temp = new HashMap<>();
        machineCodes.put("ROL", temp);
        temp.put(Mode.ACC, 0x2A);
        temp.put(Mode.ZPG, 0x26);
        temp.put(Mode.ZPX, 0x36);
        temp.put(Mode.ABS, 0x2E);
        temp.put(Mode.ABX, 0x3E);

        temp = new HashMap<>();
        machineCodes.put("ROR", temp);
        temp.put(Mode.ACC, 0x6A);
        temp.put(Mode.ZPG, 0x66);
        temp.put(Mode.ZPX, 0x76);
        temp.put(Mode.ABS, 0x6E);
        temp.put(Mode.ABX, 0x7E);

        temp = new HashMap<>();
        machineCodes.put("RTI", temp);
        temp.put(Mode.IMP, 0x40);

        temp = new HashMap<>();
        machineCodes.put("RTS", temp);
        temp.put(Mode.IMP, 0x60);

        temp = new HashMap<>();
        machineCodes.put("SBC", temp);
        temp.put(Mode.IMM, 0xE9);
        temp.put(Mode.ZPG, 0xE5);
        temp.put(Mode.ZPX, 0xF5);
        temp.put(Mode.ABS, 0xED);
        temp.put(Mode.ABX, 0xFD);
        temp.put(Mode.ABY, 0xF9);
        temp.put(Mode.XIN, 0xE1);
        temp.put(Mode.INY, 0xF1);
        temp.put(Mode.ZPI, 0xF2);

        temp = new HashMap<>();
        machineCodes.put("SEC", temp);
        temp.put(Mode.IMP, 0x38);

        temp = new HashMap<>();
        machineCodes.put("SED", temp);
        temp.put(Mode.IMP, 0xF8);

        temp = new HashMap<>();
        machineCodes.put("SEI", temp);
        temp.put(Mode.IMP, 0x78);

        temp = new HashMap<>();
        machineCodes.put("STA", temp);
        temp.put(Mode.ZPG, 0x85);
        temp.put(Mode.ZPX, 0x95);
        temp.put(Mode.ABS, 0x8D);
        temp.put(Mode.ABX, 0x9D);
        temp.put(Mode.ABY, 0x99);
        temp.put(Mode.XIN, 0x81);
        temp.put(Mode.INY, 0x91);
        temp.put(Mode.ZPI, 0x92);

        temp = new HashMap<>();
        machineCodes.put("STX", temp);
        temp.put(Mode.ZPG, 0x86);
        temp.put(Mode.ZPY, 0x96);
        temp.put(Mode.ABS, 0x8E);

        temp = new HashMap<>();
        machineCodes.put("STY", temp);
        temp.put(Mode.ZPG, 0x84);
        temp.put(Mode.ZPX, 0x94);
        temp.put(Mode.ABS, 0x8C);

        temp = new HashMap<>();
        machineCodes.put("STZ", temp);
        temp.put(Mode.ZPG, 0x64);
        temp.put(Mode.ZPX, 0x74);
        temp.put(Mode.ABS, 0x9C);
        temp.put(Mode.ABX, 0x9E);

        temp = new HashMap<>();
        machineCodes.put("TAX", temp);
        temp.put(Mode.IMP, 0xAA);

        temp = new HashMap<>();
        machineCodes.put("TAY", temp);
        temp.put(Mode.IMP, 0xA8);

        temp = new HashMap<>();
        machineCodes.put("TRB", temp);
        temp.put(Mode.ZPG, 0x14);
        temp.put(Mode.ABS, 0x1C);

        temp = new HashMap<>();
        machineCodes.put("TSB", temp);
        temp.put(Mode.ZPG, 0x04);
        temp.put(Mode.ABS, 0x0C);

        temp = new HashMap<>();
        machineCodes.put("TSX", temp);
        temp.put(Mode.IMP, 0xBA);

        temp = new HashMap<>();
        machineCodes.put("TXA", temp);
        temp.put(Mode.IMP, 0x8A);

        temp = new HashMap<>();
        machineCodes.put("TXS", temp);
        temp.put(Mode.IMP, 0x9A);

        temp = new HashMap<>();
        machineCodes.put("TYA", temp);
        temp.put(Mode.IMP, 0x98);
    }

    List<Token> tokens;

    int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void assemble() {
        current = 0;

    }

    private Expr expression() {
        return term();
    }

    private Expr term() {
        Expr expr = factor();
        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();
        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(NUMBER)) {
            return new Expr.Literal(previous().number);
        }
        if (match(STRING)) {
            return new Expr.LitString(previous().literal);
        }
        if (match(IDENTIFIER)) {
            if (check(COLON)) {
                Token name = previous();
                advance();//consume colon
                Expr exp = expression();
                return new Expr.Assign(name, exp);
            } else {
                return new Expr.Identifier(previous());
            }
        }
        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            Token t = consume(RIGHT_PAREN, "Expect ')' after expression.");
            if (t.error) {
                return new Expr.Error(t, t.tooltip);
            }
            return new Expr.Grouping(expr);
        }

        return new Expr.Error(peek(), "Unknown Terminal Token");
    }

    private Token consume(Token.Type type, String message) {
        if (check(type)) {
            return advance();
        }
        peek().error = true;
        peek().tooltip = message;
        return peek();

    }

    private boolean match(Token.Type... types) {
        for (Token.Type type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(Token.Type type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == Token.Type.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

}
