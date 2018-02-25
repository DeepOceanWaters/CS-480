package cs480.grammar

import cs480.grammar.Grammar

class GrammarCreator {
    Grammar parserGrammar
    Grammar lexerGrammar

    static {
        createLexerGrammar()
        createParserGrammar()
        parserGram.tokenGrammar = lexerGrammar
    }

    static void createLexerGrammar() {
        /**
         * S -> ruleDivider | ruleStart | or | terminal | nonTerminal | lambda
         * ruleDivider -> '->'
         * ruleStart -> 'RULE:'
         * or -> '|'
         * terminal -> character | range
         * nonTerminal -> name
         * name -> alphaChar name | alphaChar
         * alphaChar -> 'a-z' | 'A-Z'
         * character -> ' any ' | " any "
         * any -> .
         * lambda -> 'LAMBDA'
         */
        def terminals = [:]
        // add ASCII to terminals
        (0..255).each { terminals[it as char] = new Terminal(it as char) }

        def START = new NonTerminal('START')
        def S = new NonTerminal('S')
        def ruleDivider = new NonTerminal('ruleDivider')
        def ruleStart = new NonTerminal('ruleStart')
        def orDivider = new NonTerminal('orDivider')
        def terminal = new NonTerminal('terminal')
        def nonTerminal = new NonTerminal('nonTerminal')
        def lambda = new NonTerminal('lambda')
        def name = new NonTerminal('name')
        def any = new NonTerminal('any')
        def alphaChar = new NonTerminal('alphaChar')

        START.rules.add( [S] )

        S.rules.add( [ruleDivider] )
        S.rules.add( [ruleStart] )
        S.rules.add( [orDivider] )
        S.rules.add( [terminal] )
        S.rules.add( [nonTerminal] )

        ruleDivider.rules.add( [terminals['-'], terminals['>']] )

        ruleStart.rules.add( [terminals['R'], terminals['U'], terminals['L'], terminals['E'], terminals[':']] )

        orDivider.rules.add( [terminals['|']] )

        terminal.rules.add( [terminals["'"], any, terminals["'"]] )
        terminal.rules.add( [terminals['"'], any, terminals['"']] )

        nonTerminal.rules.add( [name] )

        lambda.rules.add( [terminals['L'], terminals['A'], terminals['M'], terminals['B'], terminals['D'], terminals['A']] )

        name.rules.add( [alphaChar, name] )
        name.rules.add( [alphaChar] )

        ('a'..'z').each { alphaChar.rules.add( [terminals[it]] ) }
        ('A'..'Z').each { alphaChar.rules.add( [terminals[it]] ) }

        terminals.values().each { any.rules.add( [it] ) }
        
        def nonTerminals = [
            'START': START, 'S': S, 'ruleDivider': ruleDivider, 'ruleStart': ruleStart, 
            'orDivider': orDivider, 'terminal': terminal, 'nonTerminal': nonTerminal, 
            'any': any, 'alphaChar': alphaChar, 'name': name, 'lambda': lambda
        ]

        def lexerGram = new Grammar(terminals, nonTerminals, START)
        lexerGram.isLexerGrammar = true
        lexerGram.init()

        this.lexerGrammar = lexerGram
    }

    static void createParserGrammar() {
        /**
         * S -> S S | rule
         * rule -> exprRhs exprLhs
         * exprRhs -> ruleStart nonTerminal ruleDivider
         * exprLhs -> terminal exprLhs | nonTerminal exprLhs | exprLhsList | lambda
         * exprLhsList -> exprLhs Or exprLhs 
         */
        def terminals = [
            'ruleStart': new Terminal('ruleStart'), 
            'nonTerminal': new Terminal('nonTerminal'),
            'ruleDivider': new Terminal('ruleDivider'), 
            'terminal': new Terminal('terminal'),
            'orDivider': new Terminal('orDivider'),
            'lambda': new Terminal('lambda')
        ]

        def START = new NonTerminal('START')
        def S = new NonTerminal('S')
        def rule = new NonTerminal('rule')
        def exprRhs = new NonTerminal('exprRhs')
        def exprLhs = new NonTerminal('exprLhs')
        def exprLhsList = new NonTerminal('exprLhsList')

        START.rules.add( [S] )

        S.rules.add( [S, S] )
        S.rules.add( [rule] )

        rule.rules.add( [exprRhs, exprLhs] )

        exprRhs.rules.add( [terminals['ruleStart'], terminals['nonTerminal'], terminals['ruleDivider']] )

        exprLhs.rules.add( [terminals['terminal'], exprLhs] )
        exprLhs.rules.add( [terminals['nonTerminal'], exprLhs] )
        exprLhs.rules.add( [exprLhsList] )
        exprLhs.rules.add( [terminals['lambda']] )

        exprLhsList.rules.add( [exprLhs, terminals['orDivider'], exprLhs] )
        
        def nonTerminals = ['START': START, 'S': S, 'rule': rule, 'exprRhs': exprRhs, 'exprLhs': exprLhs]

        def parserGram = new Grammar(terminals, nonTerminals, START)
        parserGram.init()

        this.parserGrammar = parserGram
    }

    static Grammar fromFile(filename) {
        def terminals = [:]
        def nonTerminals = [:]
        ExpressionNode node = parserGrammar.parse(filename).children[0]
        processNode(node, terminals, nonTerminals)
    }

    static processNode(node, terminals, nonTerminals) {
        if (node.tag == "S") node.chilren.each { processNode(it, terminals, nonTerminals) }
        else if (node.tag == "rule") rule addRule node
        else throw new Exception("Parsing error, cannot process nodes: $node.tag")

    }

    static addRule(node) {
        // node.children[0] == exprRhs
        // exprRhs.children[1] == rhs (nonTerminal)
        def rhs = node.children[0][1]
        // node.children[1] == exprLhs
        def rules = getRules(node.children[1])

    }



}