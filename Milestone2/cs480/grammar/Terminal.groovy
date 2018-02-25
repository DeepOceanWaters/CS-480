package cs480.grammar

import cs480.grammar.Expression

class Terminal extends Expression {
    String value

    Terminal(tag) {
        this.tag = tag
    }

    Terminal(tag, value) {
        this.tag = tag
        this.value = value
    }

    boolean isTerminal() { true }

    def printExpr(tabs) {
        println "$tabs$tag ($value)"
    }
}