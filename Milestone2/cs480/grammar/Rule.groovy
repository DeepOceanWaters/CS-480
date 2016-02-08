package cs480.grammar

import cs480.grammar.Expression
import cs480.grammar.Terminal
import cs480.grammar.NonTerminal

class Rule {
    int id
    NonTerminal lhs
    List<Expression> rhs = []
    Integer level
}