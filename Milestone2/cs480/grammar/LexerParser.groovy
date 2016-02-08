package cs480.grammar

import cs480.grammar.ActionType
import cs480.grammar.Rule
import cs480.grammar.ExpressionNode

abstract class LexerParser {
    def complete = false
    def parseTable
    def rules
    def outputStack = []
    def stateStack = ["0"]
    def lookahead

    def getCurToken() { outputStack[-1] }

    void parse() {
        if (!lookahead) lookahead = getNext()
        while (!complete) {
            outputStack << lookahead
            lookahead = getNext()
            performAction()
        }
    }

    def getAction() {
        def curTag = getCurToken().tag
        def curState = stateStack[-1]
        def curStateTable = parseTable[curState]
        if (!curStateTable[curTag]) return null
        if(curStateTable[curTag][lookahead.tag]) return curStateTable[curTag][lookahead.tag]
        else return curStateTable[curTag].action
    }

    def performAction() {
        Action action = getAction()
        boolean shouldContinue
        switch(action?.type) {
        case ActionType.Accept:
            complete = true
        case ActionType.Reduce:
            Rule rule = rules[action.actionId as int]
            ExpressionNode reduced = new ExpressionNode(rule.lhs)
            rule.rhs.size().times { reduced.prepend(outputStack.pop()) }
            (rule.rhs.size() - 1).times { stateStack.pop() }
            outputStack << reduced
            shouldContinue = true
            break
        case ActionType.Goto: 
        case ActionType.Shift:
            stateStack << action.actionId
            shouldContinue = false
            break
        default:
            performDefaultAction()
            break
        }
        if (!complete && shouldContinue) performAction()
    }

    abstract Expression getNext()

    abstract void performDefaultAction()
}
