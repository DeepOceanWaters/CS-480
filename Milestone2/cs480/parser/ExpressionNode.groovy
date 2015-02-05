package cs480.parser

import cs480.grammar.Expression
import cs480.grammar.NonTerminal
import cs480.grammar.Terminal

class ExpressionNode extends Expression {
    def expr
    def children
    
    ExpressionNode(expr, children) {
        this.expr = expr
        this.children = children
        this.tag = this.expr.tag
        this.baseState = this.expr.baseState
    }

    boolean isTerminal() { false }

    def printExpr(tabs) {
        println "$tabs$tag:"
        tabs += "    "
        children.each { it.printExpr(tabs) }
    }
}