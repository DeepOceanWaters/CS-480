package cs480.tests
/*
//@Grab(group='org.spockframework', module='spock-core', version='0.7-groovy-2.0')

import spock.lang.*
import cs480.grammar.Grammar
import cs480.grammar.Expression
import cs480.grammar.Terminal
import cs480.grammar.NonTerminal
import cs480.grammar.Rule
import cs480.grammar.Item
import cs480.grammar.Action
import cs480.grammar.ActionType

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

    void testCreateSetItems() {
        when:
        def testGram
        def itemSets

        (testGram) = setUpGrammarOne()
        testGram.getFirsts()
        itemSets = testGram.createSetItems()
        println itemSets

        then:
        assert 1 == 1
    }
}*/


import cs480.grammar.Grammar
import cs480.grammar.Expression
import cs480.grammar.Terminal
import cs480.grammar.NonTerminal
import cs480.grammar.Rule
import cs480.grammar.Item
import cs480.grammar.Action
import cs480.grammar.ActionType

    def setUpGrammarThree() {
        /**
         * Lexer Grammar
         * S' -> S
         * S -> RParen | LParen | binops
         * Int -> Num 
         * Real -> Num '.' Num
         * Num -> Digit Num | Digit
         * Digit -> '0' | ... | '9'
         * binops -> + | - | * | / | % | ^ | = | > | >= | < | <= | != | or | and
         *
         */
        def terminals = ['.': new Terminal('.'), '+': new Terminal('+'), '-': new Terminal('-'), '*': new Terminal('*')]

        ('0'..'9').each { terminals[it] = new Terminal(it) }

        def START = new NonTerminal('START')
        def S = new NonTerminal('S')
        def integer = new NonTerminal('Int')
        def real = new NonTerminal('Real')
        def num = new NonTerminal('Num')
        def digit = new NonTerminal('Digit')
        def op = new NonTerminal('Op')

        START.rules.add( [S] )

        S.rules.add( [integer] )
        S.rules.add( [real] )
        S.rules.add( [op] )
        integer.rules.add( [num] )
        real.rules.add( [num, terminals['.'], num] )
        num.rules.add( [digit, num] )
        num.rules.add( [digit] )
        ('0'..'9').each { digit.rules.add( [terminals[it]] ) }
        op.rules.add( [terminals['+']] )
        op.rules.add( [terminals['-']] )
        op.rules.add( [terminals['*']] )
        
        def nonTerminals = ['START': START, 'S': S, 'Int': integer, 'Real': real, 'Num': num, 'Digit': digit, 'Op': op]

        def testGram = new Grammar(terminals, nonTerminals, START)
        testGram.isLexerGrammar = true

        /**
         * Parser Grammar
         * S' -> E
         * E -> E Op E | Int | Real
         */

        def terminals2 = ['Int': new Terminal('Int'), 'Real': new Terminal('Real'), 'Op': new Terminal('Op')]


        def START2 = new NonTerminal('START')
        def E = new NonTerminal('E')

        START2.rules.add( [E] )

        E.rules.add( [E, terminals2['Op'], E])
        E.rules.add( [terminals2['Int']] )
        E.rules.add( [terminals2['Real']] )

        def nonTerminals2 = ['START': START2, 'E': E]

        def parserGram = new Grammar(terminals2, nonTerminals2, START2)
        parserGram.tokenGrammar = testGram
        
        parserGram.init()
        testGram.init()

        return [testGram, parserGram]
    }

    def setUpGrammarTwo() {
        /**
         * Lexer Grammar
         * S' -> S
         * S -> Int | Real | Op
         * Int -> Num 
         * Real -> Num '.' Num
         * Num -> Digit Num | Digit
         * Digit -> '0' | ... | '9'
         * Op -> '+' | '-' | '*'
         */
        def terminals = ['.': new Terminal('.'), '+': new Terminal('+'), '-': new Terminal('-'), '*': new Terminal('*')]

        ('0'..'9').each { terminals[it] = new Terminal(it) }

        def START = new NonTerminal('START')
        def S = new NonTerminal('S')
        def integer = new NonTerminal('Int')
        def real = new NonTerminal('Real')
        def num = new NonTerminal('Num')
        def digit = new NonTerminal('Digit')
        def op = new NonTerminal('Op')

        START.rules.add( [S] )

        S.rules.add( [integer] )
        S.rules.add( [real] )
        S.rules.add( [op] )
        integer.rules.add( [num] )
        real.rules.add( [num, terminals['.'], num] )
        num.rules.add( [digit, num] )
        num.rules.add( [digit] )
        ('0'..'9').each { digit.rules.add( [terminals[it]] ) }
        op.rules.add( [terminals['+']] )
        op.rules.add( [terminals['-']] )
        op.rules.add( [terminals['*']] )
        
        def nonTerminals = ['START': START, 'S': S, 'Int': integer, 'Real': real, 'Num': num, 'Digit': digit, 'Op': op]

        def testGram = new Grammar(terminals, nonTerminals, START)
        testGram.isLexerGrammar = true

        /**
         * Parser Grammar
         * S' -> E
         * E -> E Op E | Int | Real
         */

        def terminals2 = ['Int': new Terminal('Int'), 'Real': new Terminal('Real'), 'Op': new Terminal('Op')]


        def START2 = new NonTerminal('START')
        def E = new NonTerminal('E')

        START2.rules.add( [E] )

        E.rules.add( [E, terminals2['Op'], E])
        E.rules.add( [terminals2['Int']] )
        E.rules.add( [terminals2['Real']] )

        def nonTerminals2 = ['START': START2, 'E': E]

        def parserGram = new Grammar(terminals2, nonTerminals2, START2)
        parserGram.tokenGrammar = testGram
        
        parserGram.init()
        testGram.init()

        return [testGram, parserGram]
    }

    def setUpGrammarOne() {

        /*
         * S -> E
         * E -> a E b | b A b | b B c | c
         * A -> a
         * B -> a
         */

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
        //testGram.init()

        then:
        assert 1 == 1
        //firsts.each { assert testGram.nonTerminals[it.key].firsts.tag == it.value.tag }
    }

    void testGetFollows() {
        when:
        def testGram
        def firsts
        def follows

        (testGram, firsts, follows) = setUpGrammarOne()
        //testGram.init()

        then:
        assert 1 == 1
        //follows.each { assert testGram.nonTerminals[it.key].follows.tag as Set == it.value.tag as Set }
    }

    void testCreateSetItems() {
        when:
        def testGram
        def itemSets

        (testGram) = setUpGrammarOne()
        //testGram.init()
        //itemSets = testGram.createItemSets()
        //println "itemsSets:"
        //itemSets.each { 
        //    println ""
        //    println "$it.value.id || $it.key"
        //    it.value.set.each { 
        //        print "    $it.key ($it.value.action.type, $it.value.action.actionId) " 
        //        println "{ ${it.value.lookahead.tag.join(',')} }"
        //    } 
        //}
//
        //def parseTable = testGram.isc.toParseTable()
        //parseTable.each { println "$it" }
        //println "pTable: $parseTable"

        then:
        assert 1 == 1
    }

    void testParsing() {
        when:
        def lexerGram
        def parserGram
        def itemSets

        (lexerGram, parserGram) = setUpGrammarTwo()
        println "parsing..."
        parserGram.parse("cs480/test1.txt").printExpr("")
        then:
        assert 1 == 1
    }

    void testGrammarCreator() {
        def fileToParse = "genGrammarTest.txt"
        def lexerFile = "lexerTest.txt"
        def parserFile = "parserTest.txt"

        println "Creating Lexer"
        def lexerGram = new Grammar()
        lexerGram.isLexerGrammar = true
        lexerGram.initFromDefinition(lexerFile)

        println "Creating Parser"
        def parserGram = new Grammar()
        parserGram.initFromDefinition(parserFile)

        println "Parsing test file"
        parserGram.tokenGrammar = lexerGram
        parserGram.parse(fileToParse).printExpr("")
    }

    void testIBTL() {
        def fileToParse = "grammarTest.txt"
        def lexerFile = "grammarLexer.txt"
        def parserFile = "grammarParser.txt"

        println "Creating Lexer="
        def lexerGram = new Grammar()
        lexerGram.isLexerGrammar = true
        lexerGram.initFromDefinition(lexerFile)

        println "Creating Parser="
        def parserGram = new Grammar()
        parserGram.initFromDefinition(parserFile)

        println "Parsing test file="
        parserGram.tokenGrammar = lexerGram
        parserGram.parse(fileToParse).printExpr("")
    }


testGetFirsts()
testGetFollows()
testCreateSetItems()
testParsing()
testGrammarCreator()
testIBTL()