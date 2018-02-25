package cs480.grammar

import cs480.grammar.LexerParser
import cs480.grammar.Grammar

class Lexer extends LexerParser {
    List<String> discardables
    boolean finished = false
    FileInputStream stream
    byte[] curByte = new byte[1]
    int column = 1
    int line = 1
    String falseTerm = " "
    int tryFalseTerm = 0

    Lexer() { }

    Lexer(parseTable, rules, discardables, filename) {
        this.parseTable = parseTable
        this.rules = rules
        this.discardables = discardables
        this.stream = new FileInputStream(filename)
    }

    Terminal getNextToken() {
        println "getting next token..."
        //if (finished) println Grammar.EOF.tag.toCharArray()[0] as byte
        if (lookahead == null) lookahead = getNext()
        while(discardables.contains(lookahead.tag)) lookahead = getNext()
        if (finished) return Grammar.EOF
        
        
        parse()
        stateStack = ["0"]
        complete = false
        
        //if (outputStack[0].children[0].isTerminal()) {
        //    parseTable.each {
        //    println "$it.key:"
        //    it.value.each {
        //        println "\t$it.key:"
        //        it.value.each {
        //            byte a = (it.key.toCharArray()[0]) as byte
        //            println "\t\t${a}: ($it.value.type, $it.value.actionId)"
        //            //else it.value.each { println "\t\t$it.key: ($it.value.type, $it.value.actionId)" }
        //        }
        //    }
        //}
        //}
        tryFalseTerm = 0
        println "done: ${outputStack[-1].children[0].tag}"
        return outputStack.pop().children[0].children[0].toTerminal()
    }

    Expression getNext() {
        String curChar
        //if (tryFalseTerm >= 2) tryFalseTerm = 0
        //if (tryFalseTerm == 1) {
        //    tryFalseTerm++
        //    curChar = falseTerm
        //}
        //else {
        //    if (tempLookahead != null) {
        //        
        //    }
            tryFalseTerm = 0
            curChar = readNext()
        //}
        if (finished) curChar = Grammar.EOF.tag
        println outputStack.tag
        return new Terminal(curChar, curChar)
    }

    String readNext() {
        if (stream.read(curByte) == -1) finished = true
        def curChar = (curByte[0] as char) as String
        if (curChar == '\n') {
            line++
            column = 1
        }
        else if (curChar == (13 as char) as String) column = 1
        else column++
        return curChar
    }

    void performDefaultAction() { 
        def tempLookahead = lookahead
        lookahead = Grammar.EOF
        if (getAction()) {
            performAction()
            lookahead = tempLookahead
        }
        else  {
            println outputStack.tag
            println "printing lexerTable"
                    parseTable.each {
println "$it.key:"
it.value.each {
    println "\t$it.key:"
    it.value.each {
        byte a = (it.key.toCharArray()[0]) as byte
        println "\t\t${a}: ($it.value.type, $it.value.actionId)"
        //else it.value.each { println "\t\t$it.key: ($it.value.type, $it.value.actionId)" }
    }
}}
            throw new Exception("[Lexer] No action found for (${lookahead.tag}): line $line, column $column") 
        }
    }
}