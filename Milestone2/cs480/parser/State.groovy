package cs480.parser

class State {
    def id
    def actions = [:].withDefault { [:] }

    State() { }

    State(id) {
        this.id = id
    }

    def addAction(rhsExpr, expr, next) {
        def newAction
        if (actions[expr.tag].action) newAction = actions[expr.tag].action
        else if (!next.isTerminal() && rhsExpr.tag == next.tag) newAction = new Shift(rhsExpr.baseState)
        else if (expr.isTerminal()) newAction = new Shift()
        else newAction = new Goto()
        actions[expr.tag].action = newAction
        return newAction.state
    }

    def addReduceActions(expr, cur, rule) { expr.follows.each { actions[cur.tag][it.tag] = new Reduce(expr, rule) } }
}