package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final Stmt.Function declaration;
  private final Environment closure;

  private final boolean isInitializer;

  LoxFunction(Stmt.Function declaration, Environment closure,
              boolean isInitializer) {
    this.isInitializer = isInitializer;
    this.closure = closure;
    this.declaration = declaration;
  }

  // challenge 13.2: inner gets stored right next to this
  LoxFunction bind(LoxInstance instance, LoxFunction inner) {
    Environment environment = new Environment(closure);
    environment.define("this", instance);

    if (inner != null) {
      environment.define("inner", inner);
    } else {
      // no subclass method, so inner() just does nothing
      // getters have no params so their inner takes 0 args
    int count = declaration.params == null ? 0 : declaration.params.size();
      environment.define("inner", new LoxCallable() {
        @Override
        public int arity() { return count; }

        @Override
        public Object call(Interpreter interpreter,
                           List<Object> arguments) {
          return null;
        }

        @Override
        public String toString() { return "<fn inner>"; }
      });
    }

    return new LoxFunction(declaration, environment,
                           isInitializer);
  }

  // challenge 12.2: getters are methods with no param list
  boolean isGetter() {
    return declaration.params == null;
  }

  @Override
  public String toString() {
    return "<fn " + declaration.name.lexeme + ">";
  }

  @Override
  public int arity() {
    return declaration.params.size();
  }

  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    Environment environment = new Environment(closure);
    if (declaration.params != null) {
      for (int i = 0; i < declaration.params.size(); i++) {
        environment.define(declaration.params.get(i).lexeme,
            arguments.get(i));
      }
    }

    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      if (isInitializer) return closure.getAt(0, "this");

      return returnValue.value;
    }

    if (isInitializer) return closure.getAt(0, "this");
    return null;
  }
}
