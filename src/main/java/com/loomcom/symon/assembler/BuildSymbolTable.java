/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.loomcom.symon.assembler;

import com.loomcom.symon.assembler.ast.Expr;
import com.loomcom.symon.assembler.ast.Expr.Assign;
import com.loomcom.symon.assembler.ast.Expr.Binary;
import com.loomcom.symon.assembler.ast.Expr.Error;
import com.loomcom.symon.assembler.ast.Expr.Grouping;
import com.loomcom.symon.assembler.ast.Expr.Identifier;
import com.loomcom.symon.assembler.ast.Expr.LitString;
import com.loomcom.symon.assembler.ast.Expr.Literal;
import com.loomcom.symon.assembler.ast.Expr.Unary;
import com.loomcom.symon.assembler.ast.Stmt;
import com.loomcom.symon.assembler.ast.Stmt.Const;
import com.loomcom.symon.assembler.ast.Stmt.Expression;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author matri
 */
public class BuildSymbolTable implements Expr.Visitor<Integer>, Stmt.Visitor<Void> {

    class SymbolEntry {

        public String name;

        public boolean initialized = false;

        public int value = -1;

        ArrayList<Integer> referencedLocations = new ArrayList<>();

        public SymbolEntry(String name) {
            this.name = name;
        }

    }

    HashMap<String, SymbolEntry> symbolTable = new HashMap<String, SymbolEntry>();

    HashMap<String, SymbolEntry> uninitialized = new HashMap<>();

    @Override
    public Integer visitAssignExpr(Assign expr) {
        String name = expr.name.literal;
        int eval = expr.value.accept(this);
        if (symbolTable.containsKey(name)) {
            SymbolEntry ent = symbolTable.get(name);
            ent.initialized = true;
            ent.value = eval;
        } else if (uninitialized.containsKey(name)) {
            SymbolEntry ent = uninitialized.remove(name);
            ent.initialized = true;
            ent.value = eval;
            resolveAll(ent);
            symbolTable.put(name, ent);
        } else {
            SymbolEntry ent = new SymbolEntry(name);
            ent.initialized = true;
            ent.value = eval;
            symbolTable.put(name, ent);
        }
        return eval;
    }

    private void resolveAll(SymbolEntry ent) {

    }

    @Override
    public Integer visitBinaryExpr(Binary expr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Void visitConstStmt(Const stmt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Integer visitErrorExpr(Error expr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Integer visitGroupingExpr(Grouping expr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Integer visitIdentifierExpr(Identifier expr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Integer visitLitStringExpr(LitString expr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Integer visitLiteralExpr(Literal expr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Integer visitUnaryExpr(Unary expr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
