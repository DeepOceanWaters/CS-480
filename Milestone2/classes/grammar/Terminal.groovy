package classes.grammar

import classes.grammar.Expression

class Terminal extends Expression {

    Terminal(tag) {
        this.tag = tag
    }

    boolean isTerminal() { true }
}