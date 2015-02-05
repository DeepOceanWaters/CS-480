package cs480.grammar

abstract class Expression {
    def tag
    def baseState
    def isLambda = false

    abstract boolean isTerminal()

    def isLambda() { isLambda }

    def printExpr(tabs) {
        println "$tabs$tag"
    }
}
