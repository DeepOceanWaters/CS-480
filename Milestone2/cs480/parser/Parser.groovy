package cs480.parser

import cs480.parser.StateMachine
import cs480.grammar.Grammar

class Parser {
    def grammar
    def stateMachine = new StateMachine()
    def outputStack = []
    def nextExpr

    Parser(grammar) {
        this.grammar = grammar
        grammar.init()
    }

    def reset() {
        stateMachine.stateStack = [0]
        outputStack = []
        nextExpr = null
    }

    def parse(tokens) {
        reset()
        tokens.eachWithPeek parseToken
        return outputStack
    }

    def parseToken = { cur, next ->
        outputStack.push(cur)
        setNextExpression(next)
        def action = getAction()
        while(action && !action.eats) {
            if (action instanceof Reduce) println action.rhsExpr.tag
            else println action
            action.perform(stateMachine.stateStack, outputStack)
            action = getAction()
        }
        if (action) action.perform(stateMachine.stateStack, outputStack)
        return action
    }

    def getAction() {
        def curExprTag = getCurExprTag()
        def nextExprTag = getNextExprTag()
        def actions = stateMachine.getCurrentState().actions[curExprTag]
        def action = actions[nextExprTag]
        if (!action) action = actions.action
        return action 
    }

    def setNextExpression(next) { nextExpr = next ? next : Grammar.EOF }

    def getCurExprTag() { 
        println outputStack.tag
        println stateMachine.stateStack
        outputStack[-1].tag }

    def getNextExprTag() { nextExpr.tag }

    def pop(stack, num) {
        outList = []
        num.times { outList.push(stack.pop()) }
        return outList.reverse()
    }

    def removePop(stack, num) { num.times { stack.pop() } }

    def createStateTable() { 
        if (!grammar.ready) throw new Exception("Grammar not setup.")
        def startExpr = grammar.getStartExpr()
        stateMachine.createState(startExpr)
    }
}