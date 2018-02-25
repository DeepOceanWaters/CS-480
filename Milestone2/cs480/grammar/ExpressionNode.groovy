package cs480.grammar

import cs480.grammar.Expression
import cs480.grammar.NonTerminal
import cs480.grammar.Terminal

class ExpressionNode extends Expression {
    def expr
    def children = []
    
    ExpressionNode() { }

    ExpressionNode(expr) {
        this.expr = expr
        this.tag = this.expr.tag
        this.baseState = this.expr.baseState
    }

    ExpressionNode(expr, children) {
        this.expr = expr
        this.children = children
        this.tag = this.expr.tag
        this.baseState = this.expr.baseState
    }

    boolean isTerminal() { false }

    def prepend(expr) { children.add(0, expr) }

    def toTerminal() {
        def tokenValue = ""
        children.each { tokenValue += it.isTerminal() ? it.value : it.toTerminal().value }
        return new Terminal(tag, tokenValue)
    }

    def printExpr(tabs) {
        println "$tabs$tag:"
        tabs += "    "
        children.each { it.printExpr(tabs) }
    }
}