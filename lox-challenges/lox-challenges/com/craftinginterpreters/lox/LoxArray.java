package com.craftinginterpreters.lox;

import java.util.List;

// challenge 13.3: fixed size arrays
// supports array.get(i), array.set(i, value), and array.length
class LoxArray extends LoxInstance {
  private final Object[] elements;

  LoxArray(int size) {
    super(null);
    elements = new Object[size];
  }

  @Override
  Object get(Token name) {
    if (name.lexeme.equals("get")) {
      return new LoxCallable() {
        @Override
        public int arity() { return 1; }

        @Override
        public Object call(Interpreter interpreter,
                           List<Object> arguments) {
          int index = checkIndex(name, arguments.get(0));
          return elements[index];
        }
      };
    } else if (name.lexeme.equals("set")) {
      return new LoxCallable() {
        @Override
        public int arity() { return 2; }

        @Override
        public Object call(Interpreter interpreter,
                           List<Object> arguments) {
          int index = checkIndex(name, arguments.get(0));
          Object value = arguments.get(1);
          elements[index] = value;
          return value;
        }
      };
    } else if (name.lexeme.equals("length")) {
      return (double) elements.length;
    }

    throw new RuntimeError(name,
        "Undefined property '" + name.lexeme + "'.");
  }

  @Override
  void set(Token name, Object value) {
    throw new RuntimeError(name, "Can't add properties to arrays.");
  }

  // makes sure the index is a number and inside the array
  // so java doesnt crash with an out of bounds exception
  private int checkIndex(Token name, Object index) {
    if (!(index instanceof Double)) {
      throw new RuntimeError(name, "Array index must be a number.");
    }

    int i = (int)(double) index;
    if (i < 0 || i >= elements.length) {
      throw new RuntimeError(name, "Array index out of bounds.");
    }
    return i;
  }

  // prints values the same way lox does (5 instead of 5.0)
  private String show(Object value) {
    if (value == null) return "nil";
    if (value instanceof Double) {
      String text = value.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }
    return value.toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    for (int i = 0; i < elements.length; i++) {
      if (i != 0) builder.append(", ");
      builder.append(show(elements[i]));
    }
    builder.append("]");
    return builder.toString();
  }
}
