package classes.grammar

import classes.grammar.Expression

class NonTerminal extends Expression {
    def rules = []           // List<ArrayList<Expression>>
    def firsts = [] as Set   // Set<Terminal>
    def follows = [] as Set  // Set<Terminal>

    NonTerminal(tag) {
        this.tag = tag
    }

    NonTerminal(tag, rules) {
        this.tag = tag
        this.rules = rules
    }

    boolean isTerminal() { false }
}