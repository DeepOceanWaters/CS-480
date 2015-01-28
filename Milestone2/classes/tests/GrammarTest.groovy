package classes.tests

@Grab(group='org.spockframework', module='spock-core', version='0.7-groovy-2.0')

import spock.lang.*
import classes.grammar.Grammar
import classes.grammar.Terminal
import classes.grammar.NonTerminal

class GrammarTest extends Specification {
    void testGetFirsts() {
        when:

        def firsts = [:]
        
        /* Test 1 */
        def test_1_terminals = ['a': new Terminal('a'), 'b': new Terminal('b'), 'c': new Terminal('c')]

        def test_1_START = new NonTerminal('START')
        def test_1_E = new NonTerminal('E')
        def test_1_A = new NonTerminal('A')
        def test_1_B = new NonTerminal('B')

        test_1_E.rules.add( [test_1_terminals.a, test_1_E, test_1_terminals.b] )
        test_1_E.rules.add( [test_1_terminals.b, test_1_A, test_1_terminals.b] )
        test_1_E.rules.add( [test_1_terminals.b, test_1_B, test_1_terminals.c] )
        test_1_E.rules.add( [test_1_terminals.c] )

        test_1_A.rules.add( [test_1_terminals.a] )

        test_1_B.rules.add( [test_1_terminals.a] )
        
        def test_1_nonTerminals = ['START': test_1_START, 'E': test_1_E, 'A': test_1_A, 'B': test_1_B]

        def test_1_firsts = [
            'START': [test_1_terminals.a, test_1_terminals.b, test_1_terminals.c] as Set, 
            'E': [test_1_terminals.a, test_1_terminals.b, test_1_terminals.c] as Set, 
            'A': [test_1_terminals.a] as Set,
            'B': [test_1_terminals.a] as Set, 
        ]

        firsts = test_1_firsts

        /* etc */
        Grammar testGram = new Grammar(test_1_terminals, test_1_nonTerminals)
        testGram.getFirsts()

        testGram.nonTerminals.each { println "$it.key: $it.value.firsts.tag" }

        then:
        firsts.each { assert testGram.nonTerminals[it.key].firsts.tag == it.value.tag }
    }
}