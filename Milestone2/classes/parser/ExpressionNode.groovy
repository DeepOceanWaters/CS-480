package classes.parser

import classes.grammar.Expression
import classes.grammar.NonTerminal
import classes.grammar.Terminal

class ExpressionNode extends NonTerminal {
    def expr
    def children
    
    ExpressionNode(expr, children) {
        this.expr = expr
        this.children = children
        this.tag = this.expr.tag
        this.rules = this.expr.rules
        this.baseState = this.expr.baseState
        this.firsts = this.expr.firsts
        this.follows = this.expr.follows
    }

    boolean isTerminal() { false }

    def printExpr(tabs) {
        println "$tabs$tag:"
        tabs += "    "
        children.each { it.printExpr(tabs) }
    }
}