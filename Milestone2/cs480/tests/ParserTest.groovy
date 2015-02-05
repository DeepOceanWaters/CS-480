package cs480.tests

@Grab(group='org.spockframework', module='spock-core', version='0.7-groovy-2.0')

import spock.lang.*
import cs480.parser.State
import cs480.parser.Parser
import cs480.parser.Goto
import cs480.parser.Reduce
import cs480.parser.Shift
import cs480.grammar.Grammar
import cs480.grammar.Terminal
import cs480.grammar.NonTerminal

class ParserTest extends Specification {
    def setUpGrammarOne() {
        /*
            S -> E
            E -> aEb | bAb | bBc | c | lambda
            A -> a
            B -> a  aacbb,
        */
        def terminals = ['$': new Terminal('$'), 'a': new Terminal('a'), 'b': new Terminal('b'), 'c': new Terminal('c')]

        def START = new NonTerminal('START')
        def E = new NonTerminal('E', true)
        def A = new NonTerminal('A')
        def B = new NonTerminal('B')

        START.rules.add( [E] )

        E.rules.add( [terminals.a, E, terminals.b] )
        E.rules.add( [terminals.b, A, terminals.b] )
        E.rules.add( [terminals.b, B, terminals.c] )
        E.rules.add( [terminals.c] )

        A.rules.add( [terminals.a] )

        B.rules.add( [terminals.a] )
        
        def nonTerminals = ['START': START, 'E': E, 'A': A, 'B': B]

        def firsts = [
            'START': [terminals.a, terminals.b, terminals.c] as Set, 
            'E': [terminals.a, terminals.b, terminals.c] as Set, 
            'A': [terminals.a] as Set,
            'B': [terminals.a] as Set, 
        ]

        def follows = [
            'START': [terminals.$] as Set,
            'E': [terminals.$, terminals.b] as Set,
            'A': [terminals.b] as Set,
            'B': [terminals.c] as Set
        ]

        def testGram = new Grammar(terminals, nonTerminals, START)

        return [testGram, firsts, follows]
    }

    void testCreateState() {
        when:
        def testGram
        (testGram) = setUpGrammarOne()
        def testParser = new Parser(testGram)
        testParser.createStateTable()
        def tabs = "    "
        testParser.stateMachine.stateTable.each {
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

        then:
        assert 1 == 1
    }

    void testParse() {
        when:
        def testGram
        (testGram) = setUpGrammarOne()

        def testParser = new Parser(testGram)
        testParser.createStateTable()

        testGram.nonTerminals.values().each { println "$it.tag: $it.rules.tag" }

        def terms = testGram.terminals

        def outList = testParser.parse( [terms.a, terms.a, terms.c, terms.b, terms.b, terms.$] )
        outList[0].printExpr("")

        def outList2 = testParser.parse( [terms.a, terms.a, terms.b, terms.a, terms.b, terms.b, terms.b, terms.$] )
        outList2[0].printExpr("")

        def outList3 = testParser.parse( [terms.a, terms.a, terms.b, terms.b, terms.$] )
        outList3[0].printExpr("")

        then:
        assert 1 == 1
    }
}