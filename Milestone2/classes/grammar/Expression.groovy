package classes.grammar

abstract class Expression {
    def tag
    def baseState

    abstract boolean isTerminal()

    def printExpr(tabs) {
        println "$tabs$tag"
    }
}
