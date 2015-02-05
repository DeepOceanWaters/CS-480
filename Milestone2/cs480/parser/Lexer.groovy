package cs480.parser

import cs480.grammar.Terminal
import cs480.grammar.Grammar
import cs480.parser.Parser

class Lexer extends Parser {
    def tokenStates = [:]
    def defaultAction

    Lexer(grammar) {
        super(grammar)
    }

    Lexer(grammar, defaultAction) {
        super(grammar)
        this.defaultAction = defaultAction
        FileInputStream.metaClass.eachByteWithPeek = { closure ->
            def current = null
            delegate?.eachByte { next ->
                if (current) closure(current, next)
                current = next
            }
            if (current) closure(current, null)
        }
    }

    def tokenizeFile(filename) {
        def byteStream = new FileInputStream(filename) 
        def tokens = parse(byteStream)
        byteStream.close()
        return tokens
    }

    def parse(stream) {
        stream.eachByteWithPeek { cur, next -> 
            //def curChar = cur ? (cur as char) as String : null
            //def nextChar = next ? (next as char) as String : null
            def curChar = cur ? grammar.terminals[(cur as char) as String] : null
            def nextChar = next ? grammar.terminals[(next as char) as String] : null
            curChar = curChar ? curChar : new Terminal(' ')
            def action = parseToken(curChar, nextChar)
            if (!action) {
                action = getTokenAction()
                if (!action) throw new Exception("Unknown token \n\t'$curChar'\n\t${outputStack[-1].tag}\n\t$stateMachine.stateStack")
                action.perform(stateMachine.stateStack, outputStack)
            }
        }
        return outputStack
    }

    //def setNextExpression(next) { nextExpr = next ? next : Grammar.EOF.tag }
//
    //def getCurExprTag () { outputStack[-1] }
//
    //def getNextExprTag() { nextExpr }

    def getTokenAction() {
        def curExpr = outputStack[-1]
        def curState = stateMachine.getCurrentState()
        def action = curState.actions[curExprTag][Grammar.EOF.tag]
        if (!action && curState.id == 0) return defaultAction
        else return action
    }
}