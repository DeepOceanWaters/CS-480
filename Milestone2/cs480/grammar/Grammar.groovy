package cs480.grammar

import cs480.grammar.Expression
import cs480.grammar.Terminal
import cs480.grammar.NonTerminal
import cs480.grammar.Rule
import cs480.grammar.Item
import cs480.grammar.Action
import cs480.grammar.ActionType
import cs480.grammar.Parser
import cs480.grammar.Lexer

class Grammar {
    static final def EOF = new Terminal('$end')
    def terminals = [:]    // Map<String, Terminal>
    def nonTerminals = [:] // Map<String, NonTerminal>
    def ready = false // boolean
    def setSeq = 0
    def rules = []
    def isc
    def tokenGrammar
    def parseTable
    def discardables = [' ', '\t', '\n', (13 as char) as String]
    def isLexerGrammar = false
    static def grammarCreator = [:]

    static {
        createLexerGrammar()
        createParserGrammar()
        grammarCreator.parserGrammar.tokenGrammar = grammarCreator.lexerGrammar
    }

    static void createLexerGrammar() {
        /**
         * st -> ruleDivider | ruleStart | or | terminal | nonTerminal | lambda
         * ruleDivider -> '->'
         * ruleStart -> ':' | number ':'
         * or -> '|'
         * terminal -> character
         * nonTerminal -> name
         * name -> alphaChar name | alphaChar
         * alphaChar -> a-Z
         * character -> ' any ' | " any "
         * any -> .
         * number -> 0-9 | number 0-9
         * lambda -> 'LAMBDA'
         */
        def terminals = [:]
        // add ASCII to terminals
        (0..255).each { 
            def charStr = (it as char) as String
            terminals[charStr] = new Terminal(charStr) 
        }

        def START = new NonTerminal('START')
        def st = new NonTerminal('st')
        def ruleDivider = new NonTerminal('ruleDivider')
        def ruleStart = new NonTerminal('ruleStart')
        def orDivider = new NonTerminal('orDivider')
        def terminal = new NonTerminal('terminal')
        def nonTerminal = new NonTerminal('nonTerminal')
        def lambda = new NonTerminal('lambda')
        def name = new NonTerminal('name')
        def any = new NonTerminal('any')
        def anyMul = new NonTerminal('anyMul')
        def alphaChar = new NonTerminal('alphaChar')
        def number = new NonTerminal('number')
        def dig = new NonTerminal('dig')
        //def dot = new NonTerminal('dot')
        //def range = new NonTerminal('range')
        //def not = new NonTerminal('not')

        START.rules.add( [st] )

        st.rules.add( [ruleDivider] )
        st.rules.add( [ruleStart] )
        st.rules.add( [orDivider] )
        st.rules.add( [terminal] )
        st.rules.add( [nonTerminal] )
        //st.rules.add( [dot] )
        //st.rules.add( [range] )
        //st.rules.add( [not] )

        ruleDivider.rules.add( [terminals['-'], terminals['>']] )

        ruleStart.rules.add( [terminals[':']] )
        ruleStart.rules.add( [number, terminals[':']] )

        orDivider.rules.add( [terminals['|']] )

        terminal.rules.add( [terminals["'"], anyMul, terminals["'"]] )
        terminal.rules.add( [terminals['"'], anyMul, terminals['"']] )

        anyMul.rules.add( [any, anyMul] )
        anyMul.rules.add( [any] )

        nonTerminal.rules.add( [name] )

        lambda.rules.add( [terminals['L'], terminals['A'], terminals['M'], terminals['B'], terminals['D'], terminals['A']] )

        name.rules.add( [alphaChar, name] )
        name.rules.add( [alphaChar] )

        number.rules.add( [dig, number] )
        number.rules.add( [dig] )

        //dot.rules.add( [terminals['.']] )

        ('0'..'9').each { dig.rules.add( [terminals[it]] ) }

        ('a'..'z').each { alphaChar.rules.add( [terminals[it]] ) }
        ('A'..'Z').each { alphaChar.rules.add( [terminals[it]] ) }

        terminals.values().each { any.rules.add( [it] ) }
        
        def nonTerminals = [
            'START': START, 'st': st, 'ruleDivider': ruleDivider, 'ruleStart': ruleStart, 
            'orDivider': orDivider, 'terminal': terminal, 'nonTerminal': nonTerminal, 
            'any': any, 'alphaChar': alphaChar, 'name': name, 'lambda': lambda, 'anyMul': anyMul,
            'number': number, 'dig': dig
        ]

        def lexerGrammar = new Grammar(terminals, nonTerminals, START)
        lexerGrammar.isLexerGrammar = true
        lexerGrammar.init()

        this.grammarCreator.lexerGrammar = lexerGrammar
    }

    static void createParserGrammar() {
        /**
         * S -> S S | rule
         * rule -> exprRhs exprLhs
         * exprRhs -> ruleStart nonTerminal ruleDivider
         * exprLhs -> terminal exprLhs | nonTerminal exprLhs | regex exprLhs | exprLhsList | lambda |
         * exprLhsList -> exprLhs Or exprLhs 
         * regex -> dot | range | not
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
        exprLhs.isLambda = true

        exprLhsList.rules.add( [exprLhs, terminals['orDivider'], exprLhs] )
        
        def nonTerminals = ['START': START, 'S': S, 'rule': rule, 'exprRhs': exprRhs, 'exprLhs': exprLhs, 'exprLhsList': exprLhsList]

        def parserGrammar = new Grammar(terminals, nonTerminals, START)
        parserGrammar.init()

        this.grammarCreator.parserGrammar = parserGrammar
    }

    Grammar() { }

    /**
     * Map<String, Terminal> terminals
     * Map<String, NonTerminal> nonTerminals
     */
    Grammar(terminals, nonTerminals, startExpr) {
        this.terminals = terminals
        this.nonTerminals = nonTerminals
        this.nonTerminals['START'] = startExpr

        this.terminals[EOF.tag] = EOF // Add eof token

        Collection.metaClass.eachWithPeek = { closure ->
            def current = null
            delegate?.each { next ->
                if (current) closure(current, next)
                current = next
            }
            if (current) closure(current, null)
        }

        Collection.metaClass.frontPop = {
            def frontItem = delegate.take(1)[0]
            delegate = delegate.drop(1)
            return frontItem
        }

        Collection.metaClass.eachWithLookahead = { k, closure ->
            def current = null
            def lookahead = []
            delegate?.each { next ->
                if (current && lookahead.size() == k) closure(current, lookahead)
                lookahead << next
                if (lookahead.size() > k) current = lookahead.frontPop()
            }
            current = lookahead.frontPop(1)
            if (current) closure(current, lookahead)
        }   
    }

    def initFromDefinition(filename) {
        // parserGrammar.parse() == START
        // children[0] == S
        ExpressionNode node = grammarCreator.parserGrammar.parse(filename).children[0]
        processNode(node)
        init()
    }

    def processNode(node) {
        if (node.tag == "S") node.children.each { processNode(it) }
        else if (node.tag == "rule") addRule(node)
        else throw new Exception("Parsing error, cannot process node: $node.tag")        
    }

    def addRule(node) {
        // node.children[0] == exprRhs
        // exprRhs.children[1] == rhs (nonTerminal)
        def rhs = node.children[0]
        def ruleLevel = getRuleLevel(rhs.children[0])
        def rhsTag = node.children[0].children[1].value
        // node.children[1] == exprLhs
        def rules = []
        getRules(node.children[1], rules)
        if (!nonTerminals[rhsTag]) nonTerminals[rhsTag] = new NonTerminal(rhsTag)
        def expr = nonTerminals[rhsTag]
        expr.level = ruleLevel
        rules.each { it.size() == 0 ? expr.isLambda = true : expr.rules << it }
    }

    def getRuleLevel(rhsStarter) {
        def level
        if (rhsStarter.value.size() > 1)
            level = rhsStarter.value[0..-2].toInteger()
        else 
            level = 0
        return level
    }

    def getRules(expr, rules) {
        switch(expr.tag) {
        case 'terminal':
            if (!rules) rules << []
            if (!terminals[expr.value]) terminals[expr.value[1..-2]] = new Terminal(expr.value[1..-2])
            def term = terminals[expr.value[1..-2]]
            rules[-1] << term
            break
        case 'nonTerminal':
            if (!rules) rules << []
            if (!nonTerminals[expr.value]) nonTerminals[expr.value] = new NonTerminal(expr.value)
            def term = nonTerminals[expr.value]
            rules[-1] << term
            break
        case 'lambda':
            if (!rules) rules << []
            break
        case 'exprLhs':
            expr.children.each { getRules(it, rules) }
            break
        case 'exprLhsList':
            evalExprLhsList(0, expr, rules)
            evalExprLhsList(2, expr, rules)
            break
        default:
            throw new Exception("Unknown expr: $expr.tag (expecting: terminal, nonTerminal, lambda, exprLhs, exprLhsList)")
            break
        }
    }

    def evalExprLhsList(num, expr, rules) {
        if (!isExprLhsList(expr.children[num])) rules << []
        getRules(expr.children[num], rules)
    }

    def isExprLhsList(exprLhs) { exprLhs.children[0].tag == 'exprLhsList' }

    def getLexer(filename) {
        return new Lexer(isc.toParseTable(), rules, discardables, filename)
    }

    def parse(filename) {
        def parser = new Parser(tokenGrammar.getLexer(filename), isc.toParseTable(), rules)
        parser.parse()
        return parser.outputStack[0]
    }

    def createItemSets() { 
        isc = new ItemSetCreator()
        isc.createStartSet(getStartExpr())
        isc.createItemSets()
        return isc.doneList
    }


    def init() {
        removeLambdas()
<<<<<<< HEAD
        setUpGrammar()
=======
        //processExprs()
>>>>>>> 3682dc6a3a604a8ad5250e98f698381dcb0915bb
        getFirsts()
        getFollows()
        setupRules()
        println "creating Item Sets"
        createItemSets()
        ready = true
        println "done init'ing"
    }

    def processExprs() {
        def nonTermVals = []
        nonTerminals.each { if (it.key != 'START' && it.key != 'S') nonTermVals << it.value }
        def usedVals = []
        if (nonTerminals['S']) {
            nonTerminals['S'].rules.each {
                it.each {
                    usedVals << it.tag
                }
            }
        }
        for(nonTerm in nonTermVals) {
            for(rule in nonTerm.rules) {
                for(expr in rule) {
                    if (!expr.isTerminal()) {
                        nonTerm.hasNonTerminal = true
                        break
                    }
                }
                if(nonTerm.hasNonTerminal) break
            }
        }

        for(nonTerm in nonTermVals) {
            def newRules = []
            for(rule in nonTerm.rules) {
                newRules += replaceTerms(rule, [], 0)
            }
            nonTerm.rules = newRules
        }

        //for(nonTerm in nonTermVals) {
        //    if (!nonTerm.hasNonTerminal && !usedVals.contains(nonTerm.tag)) {
        //        nonTerminals[nonTerm.tag] = null
        //    }
        //}
    }

    def replaceTerms(rule, newRules, pos) {
        if (pos >= rule.size()) return newRules
        def curExpr = rule[pos]
        
        if (curExpr.isTerminal() || curExpr.hasNonTerminal) {
            if (newRules.size() == 0) newRules << [] 
            newRules.each { it << curExpr }
        }
        else {
            def nextNewRules = []
            if (newRules.size() == 0) {
                curExpr.rules.each { curRule -> nextNewRules << ([] + curRule) }
            }
            else {
                curExpr.rules.each { curRule -> newRules.each { nextNewRules << (it.clone() + curRule) } }
            }
            newRules = nextNewRules
        }
        return replaceTerms(rule, newRules, pos + 1)
    }

    def createRule() {
        def newRule = new Rule()
        newRule.id = rules.size()
        rules << newRule
        return newRule
    }

    def setupRules() {
        for(nonTerm in nonTerminals.values()) {
            def newRules = []
            nonTerm.rules.each {
                def newRule = createRule()
                newRule.lhs = nonTerm
                newRule.rhs = it
                newRule.level = nonTerm.level
                newRules << newRule
            }
            nonTerm.rules = newRules
        }
    }

    def getStartExpr() { return nonTerminals['START'] }

    def removeLambdas() {
        nonTerminals.values().each {
            def newRules = []
            it.rules.each { removeLambdas(it).each { if(it) newRules << it } }
            it.rules = newRules
        }
    }

    def removeLambdas(rule) {
        def rules = [[]]
        rule.each { curExpr ->
            def newRules = []
            rules.each {
                if (curExpr.isLambda()) newRules = rules.collect { it.clone() }
                it << curExpr
            }
            newRules.each { rules << it }
        }
        return rules
    }

    /**
     * return void
     * Get startExpr from nonTerminals list and use it as base expr
     * for the recursive function.
     */
    def getFirsts() {
        def startExpr = nonTerminals["START"]
        if (!startExpr) throw new Exception("Start Expression not defined.")

        def exprsSeen = []
        nonTerminals.each { getFirsts(it.value, exprsSeen) }
    }

    /**
     * Expression expr
     * List<NonTerminal> exprsSeen
     * return ArrayList<Expression>
     */
    def getFirsts(expr, exprsSeen) {
        if (expr.isTerminal()) return [expr]
        if (exprsSeen.contains(expr)) return expr.firsts

        exprsSeen << expr
        return expr.firsts = expr.rules.collect { getFirsts(it[0], exprsSeen) }.flatten() as Set
    }

    /**
     * return void
     * Finds the follows set for each Non-Terminal in the grammar
     * and updates the Non-Terminal's follow set to match.
     */
    def getFollows() {
        def startExpr = nonTerminals["START"]
        startExpr.follows << EOF
        if (isLexerGrammar) discardables.each { startExpr.follows << new Terminal(it) }

        nonTerminals.each { getFollows(it.value) }
        nonTerminals.each { completeFollows(it.value) }
    }

    /**
     * NonTerminal expr
     * return Set<Terminal>
     * This function is called after the initial getFollows() function
     * is called. It removes all Non-Terminals in expr.follows, calls
     * this function for the Non-Terminals removed, and adds the returned
     * set of follows to the expr.follows
     */
    def completeFollows(expr) {
        expr.follows = expr.follows.flatten()
        def nonTermFollows = expr.follows.findAll { !it.isTerminal() }
        nonTermFollows.each { 
            expr.follows -= it
            expr.follows << completeFollows(it)
        }
        expr.follows = expr.follows.flatten()
        if (expr.isLambda()) expr.firsts << expr.follows
        expr.firsts = expr.firsts.flatten()
        return expr.follows
    }

    /** 
     * ArrayList<Expression> exprList
     * return void
     * Call getFirsts() before calling this, otherwise follows will not be set correctly
     */
    def getFollows(expr) { 
        expr.rules.each { it.eachWithPeek { cur, next -> if (!cur.isTerminal()) cur.follows << toFollows(expr, next) } }
    }

    /**
     * return Set<Expression>
     * NonTerminal rhsExpr = right hand side expression
     * Expression expr 
     */
    def toFollows(lhsExpr, expr) { 
        if (!expr) return lhsExpr 
        else if (expr.isTerminal()) return expr 
        else return expr.firsts 
    }

}

