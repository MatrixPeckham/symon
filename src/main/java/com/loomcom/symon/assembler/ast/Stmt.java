package com.loomcom.symon.assembler.ast;

import java.util.List;
import com.loomcom.symon.assembler.Token;

public abstract class Stmt {
  public interface Visitor<R> {
    public R visitExpressionStmt(Expression stmt);
    public R visitConstStmt(Const stmt);
  }

  // Nested Stmt classes here...
  public static class Expression extends Stmt {
    public Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    public final Expr expression;
  }
  public static class Const extends Stmt {
    public Const(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitConstStmt(this);
    }

    public final Token name;
    public final Expr initializer;
  }

 public abstract <R> R accept(Visitor<R> visitor);
}
