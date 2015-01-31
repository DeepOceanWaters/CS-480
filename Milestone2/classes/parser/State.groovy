package classes.parser

class State {
    static def sequence = 0
    def id
    def actions = [:].withDefault { [:] }

    State() {
        id = sequence
        sequence++
    }

    static def resetSequence() {
        sequence = 0
    }

    def addAction(rhsExpr, expr, next) {
        if (actions[expr.tag].action) return actions[expr.tag].action.state
        if (!next.isTerminal() && rhsExpr.tag == next.tag) return ( actions[expr.tag].action = new Shift(rhsExpr.baseState) ).state
        if (expr.isTerminal()) return ( actions[expr.tag].action = new Shift() ).state
        return ( actions[expr.tag].action = new Goto() ).state
    }

    def addReduceActions(expr, cur, rule) { expr.follows.each { actions[cur.tag][it.tag] = new Reduce(expr, rule) } }

    def compare(state) { id == state.id }
}