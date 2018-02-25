package cs480.tests

@Grab(group='org.spockframework', module='spock-core', version='0.7-groovy-2.0')

import spock.lang.*
import cs480.parser.State
import cs480.parser.Parser
import cs480.parser.Goto
import cs480.parser.Reduce
import cs480.parser.Shift
import cs480.parser.Discard
import cs480.parser.Lexer
import cs480.grammar.Grammar
import cs480.grammar.Terminal
import cs480.grammar.NonTerminal

class LexerTest extends Specification {
    def setUpGrammarOne() {
        /*
            S -> E
            E -> F | FG
            F -> DF | D // d+
            G -> H | lambda
            H -> .F
            D -> 0-9
            0-9+(.0-9+)?
        */
        def terminals = [:]

        (0..9).each { terminals[it as String] = new Terminal(it as String) }
        //['a'..'z'].each { terminals[it] = new Terminal(it) }
        //['A'..'Z'].each { terminals[it] = new Terminal(it) }
        terminals['.'] = new Terminal('.')

        def START = new NonTerminal('START')
        def E = new NonTerminal('E')
        def F = new NonTerminal('F')
        def G = new NonTerminal('G', true)
        def H = new NonTerminal('H')
        def D = new NonTerminal('D')

        START.rules << [E]

        E.rules << [F]
        E.rules << [F, G]

        F.rules << [D]
        F.rules << [F, D]

        G.rules << [H]

        H.rules << [terminals['.'], F]

        
        (0..9).each { D.rules << [terminals[it as String]] }



        
        
        def nonTerminals = ['START': START, 'E': E, 'F': F, 'G': G, 'H': H, 'D': D]

        nonTerminals.each { println "$it.key $it.value.rules.tag" }


        def testGram = new Grammar(terminals, nonTerminals, START)

        return testGram
    }

    void testParse() {
        when:
        def testGram = setUpGrammarOne()

        def testLexer = new Lexer(testGram, new Discard())
        testLexer.createStateTable()
        testLexer.grammar.nonTerminals.each { println "$it.value.tag: $it.value.firsts.tag | $it.value.follows.tag"}

        def tabs = "    "
        testLexer.stateMachine.stateTable.each {
            println "$it.key:"
            it.value.actions.each {
                println "$tabs$it.key:"
                it.value.each{ 
                    def out
                    if (it.value instanceof Shift) out = "Shift: $it.value.state.id" 
                    else if (it.value instanceof Goto) out = "Goto: $it.value.state.id" 
                    else if (it.value instanceof Reduce) out ="Reduce: $it.value.rhsExpr.tag $it.value.rule.tag"
                    else out = "$it.value"
                    println "$tabs$tabs$it.key : $out"
                }
            }
        }

        //testLexer.tokenizeFile("cs480/tests/tokenTest1.txt").each { println it.children }
        ('0'..'9').each { println it }
        testLexer.tokenizeFile("cs480/tests/tokenTest1.txt").printExpr("")
        ('0'..'9').each { println it }

        then:
        assert 1 == 1
    }
}