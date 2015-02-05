package cs480.grammar

import cs480.grammar.Expression

class Terminal extends Expression {

    Terminal(tag) {
        this.tag = tag
    }

    boolean isTerminal() { true }
}