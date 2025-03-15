package com.loomcom.symon.assembler.ast;

import java.util.List;
import com.loomcom.symon.assembler.Token;

public abstract class Expr {
  public interface Visitor<R> {
    public R visitAssignExpr(Assign expr);
    public R visitBinaryExpr(Binary expr);
    public R visitGroupingExpr(Grouping expr);
    public R visitLiteralExpr(Literal expr);
    public R visitLitStringExpr(LitString expr);
    public R visitUnaryExpr(Unary expr);
    public R visitIdentifierExpr(Identifier expr);
    public R visitErrorExpr(Error expr);
  }

  // Nested Expr classes here...
  public static class Assign extends Expr {
    public Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

    public final Token name;
    public final Expr value;
  }
  public static class Binary extends Expr {
    public Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    public final Expr left;
    public final Token operator;
    public final Expr right;
  }
  public static class Grouping extends Expr {
    public Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    public final Expr expression;
  }
  public static class Literal extends Expr {
    public Literal(Integer value) {
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    public final Integer value;
  }
  public static class LitString extends Expr {
    public LitString(String value) {
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLitStringExpr(this);
    }

    public final String value;
  }
  public static class Unary extends Expr {
    public Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    public final Token operator;
    public final Expr right;
  }
  public static class Identifier extends Expr {
    public Identifier(Token name) {
      this.name = name;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitIdentifierExpr(this);
    }

    public final Token name;
  }
  public static class Error extends Expr {
    public Error(Token problem, String message) {
      this.problem = problem;
      this.message = message;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitErrorExpr(this);
    }

    public final Token problem;
    public final String message;
  }

 public abstract <R> R accept(Visitor<R> visitor);
}
