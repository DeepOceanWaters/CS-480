package cs480.tests

@Grab(group='org.spockframework', module='spock-core', version='0.7-groovy-2.0')

import spock.lang.*
import cs480.grammar.Grammar
import cs480.grammar.Terminal
import cs480.grammar.NonTerminal

class GrammarTest extends Specification {
    def setUpGrammarOne() {
        def terminals = ['$': new Terminal('$'), 'a': new Terminal('a'), 'b': new Terminal('b'), 'c': new Terminal('c')]

        def START = new NonTerminal('START')
        def E = new NonTerminal('E')
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

    void testGetFirsts() {
        when:
        def testGram
        def firsts

        (testGram, firsts) = setUpGrammarOne()
        testGram.getFirsts()

        then:
        firsts.each { assert testGram.nonTerminals[it.key].firsts.tag == it.value.tag }
    }

    void testGetFollows() {
        when:
        def testGram
        def firsts
        def follows

        (testGram, firsts, follows) = setUpGrammarOne()
        testGram.getFirsts()
        testGram.getFollows()

        then:
        follows.each { assert testGram.nonTerminals[it.key].follows.tag as Set == it.value.tag as Set }
    }
}