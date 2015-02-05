package cs480.grammar

import cs480.grammar.Expression

class NonTerminal extends Expression {
    def rules = []           // List<ArrayList<Expression>>
    def firsts = [] as Set   // Set<Terminal>
    def follows = [] as Set  // Set<Terminal>

    NonTerminal() { }

    NonTerminal(tag) {
        this.tag = tag
    }

    NonTerminal(tag, isLambda) {
        this.tag = tag
        this.isLambda  = isLambda
    }

    NonTerminal(tag, rules, isLambda) {
        this.tag = tag
        this.rules = rules
        this.isLambda  = isLambda
    }

    boolean isTerminal() { false }
}