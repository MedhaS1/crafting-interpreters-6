package com.craftinginterpreters.lox;

import java.util.Map;

// challenge 13.1: a trait is just a named bag of methods.
// its not a class so you cant call it to make an instance.
class LoxTrait {
  final Token name;
  final Map<String, LoxFunction> methods;

  LoxTrait(Token name, Map<String, LoxFunction> methods) {
    this.name = name;
    this.methods = methods;
  }

  @Override
  public String toString() {
    return name.lexeme;
  }
}
