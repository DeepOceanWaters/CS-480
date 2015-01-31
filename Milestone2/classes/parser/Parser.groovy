package classes.parser

import classes.grammar.Grammar

class Parser {
    def lexing = false
    def grammar
    def stateTable = [:]
    def stateStack = [0]
    def outputStack = []
    def nextExpr

    Parser(grammar) {
        this.grammar = grammar
        grammar.init()
    }

    Parser(grammar, lexing) {
        this.grammar = grammar
        this.lexing = lexing
        grammar.init()
    }

    def reset(full) {
        if (full) stateTable = [:]
        stateStack = [0]
        outputStack = []
    }

    def parse(tokens) {
        tokens.eachWithPeek { cur, next ->
            outputStack.push(cur)
            nextExpr = next
            def action = getAction()
            while( action && !(action.eats) ) {
                action.perform(stateStack, outputStack)
                action = getAction()
            }
            if (action) action.perform(stateStack, outputStack)
        }
        return outputStack
    }

    def getAction() {
        def curExpr = outputStack[-1]
        def actions = stateTable[stateStack[-1]].actions[curExpr.tag]
        def action = actions[nextExpr?.tag]
        if (!action) action = actions.action
        return action 
    }

    def pop(stack, num) {
        outList = []
        num.times { outList.push(stack.pop()) }
        return outList.reverse()
    }

    def removePop(stack, num) { num.times { stack.pop() } }

    def createStateTable() { 
        if (!grammar.ready) throw new Exception("Grammar not setup.")
        def startExpr = grammar.getStartExpr()
        createState(startExpr, new State()) 
    }

    def createState(expr, baseState) {
        expr.baseState = baseState
        println "$expr.tag: $baseState.id"
        stateTable[baseState.id] = baseState
    
        expr.rules.each { createStatesForRule(expr, it, baseState) }
    }

    def createStatesForRule(expr, rule, curState) {
        rule.eachWithPeek { cur, next ->
            if (!cur.isTerminal() && !cur.baseState) createState(cur, curState)
            if (!next) curState.addReduceActions(expr, cur, rule)
            else curState = curState.addAction(expr, cur, next)
            stateTable[curState.id] = curState
        }
    }
}