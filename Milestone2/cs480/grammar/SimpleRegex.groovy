package cs480.grammar

import cs480.grammar.Grammar

class SimpleRegex {
    static {
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

    static Grammar createLexerGrammar() {
        /**
         * ruleDivider -> '->'
         * ruleStart -> 'RULE:'
         * or -> '|'
         * terminal -> character | range
         * nonTerminal -> name
         * name -> alphaChar id
         * id -> 'a-Z' id | '0-9' id | lambda
         * alphaChar -> 'a-z' | 'A-Z'
         * character -> ' . ' | " . "
         * range -> character - character
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
    }
}