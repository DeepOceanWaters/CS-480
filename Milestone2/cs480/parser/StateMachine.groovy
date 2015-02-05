package cs480.parser


class StateMachine {
    def sequence = 0
    def stateStack = [0]
    def stateTable = [:]

    def getCurrentState() { stateTable[stateStack[-1]] }

    def createState(expr) {
        createState(expr, new State(sequence++))
    }

    def createState(expr, baseState) {
        println "$baseState.id: $expr.tag"
        expr.baseState = baseState
        stateTable[baseState.id] = baseState
    
        expr.rules.each { createStatesForRule(expr, it, baseState) }
    }

    def createStatesForRule(expr, rule, curState) {
        rule.eachWithPeek { cur, next ->
            if (!cur.isTerminal() && !cur.baseState) createState(cur, curState)
            if (!next) curState.addReduceActions(expr, cur, rule)
            else curState = curState.addAction(expr, cur, next)
            if (curState.id == null) curState.id = sequence++
            stateTable[curState.id] = curState
        }
    }
}