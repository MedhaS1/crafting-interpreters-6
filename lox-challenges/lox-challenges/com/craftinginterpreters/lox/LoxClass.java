package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// challenge 12.1: a class is also an instance (of its metaclass)
class LoxClass extends LoxInstance implements LoxCallable {
  final String name;
  final LoxClass superclass;
  private final Map<String, LoxFunction> methods;

  LoxClass(LoxClass metaclass, String name, LoxClass superclass,
           Map<String, LoxFunction> methods) {
    super(metaclass);
    this.superclass = superclass;
    this.name = name;
    this.methods = methods;
  }

  // challenge 13.2: BETA style lookup
  // walk all the way up the chain and grab every method with this name.
  // the one highest up wins, and each one's "inner" is the next one down.
  LoxFunction findMethod(LoxInstance instance, String name) {
    List<LoxFunction> found = new ArrayList<>();
    LoxClass klass = this;
    while (klass != null) {
      if (klass.methods.containsKey(name)) {
        found.add(klass.methods.get(name));
      }
      klass = klass.superclass;
    }

    if (found.isEmpty()) return null;

    // found goes from the subclass (index 0) up to the top superclass.
    // bind from the bottom up so every method gets the right inner.
    LoxFunction inner = null;
    for (LoxFunction method : found) {
      inner = method.bind(instance, inner);
    }

    return inner;
  }

  // same idea but no binding, just need the top one for arity
  private LoxFunction topMethod(String name) {
    LoxFunction top = null;
    LoxClass klass = this;
    while (klass != null) {
      if (klass.methods.containsKey(name)) {
        top = klass.methods.get(name);
      }
      klass = klass.superclass;
    }
    return top;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    LoxInstance instance = new LoxInstance(this);
    LoxFunction initializer = findMethod(instance, "init");
    if (initializer != null) {
      initializer.call(interpreter, arguments);
    }

    return instance;
  }

  @Override
  public int arity() {
    LoxFunction initializer = topMethod("init");
    if (initializer == null) return 0;
    return initializer.arity();
  }
}
