package cs480.grammar

import cs480.grammar.LexerParser

class Parser extends LexerParser {
    def lexer
    def recovering = false

    Parser() { }

    Parser(lexer, parseTable, rules) {
        this.lexer = lexer
        this.parseTable = parseTable
        this.rules = rules
    }

    Expression getNext() { 
//        if (lexer.finished) { 
//parseTable.each {
//println "$it.key:"
//it.value.each {
//    println "\t$it.key:"
//    it.value.each {
//        println "\t\t${it.key}: ($it.value.type, $it.value.actionId)"
//        //else it.value.each { println "\t\t$it.key: ($it.value.type, $it.value.actionId)" }
//    }
//}}
//        }
        lexer.getNextToken()
    }

    void performDefaultAction() { 
 /*       parseTable.each {
println "$it.key:"
it.value.each {
    println "\t$it.key:"
    it.value.each {
        println "\t\t${it.key}: ($it.value.type, $it.value.actionId)"
        //else it.value.each { println "\t\t$it.key: ($it.value.type, $it.value.actionId)" }
    }
}}*/
        println "$outputStack.tag, $lookahead.tag"
        if (!tryRecovery()) {
            parseTable.each {
println "$it.key:"
it.value.each {
    println "\t$it.key:"
    it.value.each {
        println "\t\t${it.key}: ($it.value.type, $it.value.actionId)"
        //else it.value.each { println "\t\t$it.key: ($it.value.type, $it.value.actionId)" }
    }
}}
            throw new Exception("[Parser] No action found at: line $lexer.line, column $lexer.column") 
        }
    }

    boolean tryRecovery() {
        if (recovering) return false
        recovering = true
        def recovered = false
        def curOut = outputStack.pop()
        if (curOut.tag == 'name') {
            def tempNext = lookahead
            println "recovering name"
            lookahead = new Terminal(curOut.value, curOut.value)
            println stateStack.pop()
            println "peformingAction1"
            performAction()
            println "state: ${stateStack[-1]}"
            println "state: ${parseTable[stateStack[-1]]}"
            outputStack << lookahead
            lookahead = tempNext
            println "performAction2"
            performAction()
            recovered = true
        }
        return recovered
    }
}
