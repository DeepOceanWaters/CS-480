package cs480.grammar

import cs480.grammar.Expression
import cs480.grammar.Terminal
import cs480.grammar.NonTerminal

class Grammar {
    static final def EOF = new Terminal('$')
    def terminals = [:]    // Map<String, Terminal>
    def nonTerminals = [:] // Map<String, NonTerminal>
    def ready = false // boolean

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

    def init() {
        removeLambdas()
        setUpGrammar()
        getFirsts()
        getFollows()
        ready = true
    }

    def getStartExpr() { return nonTerminals['START'] }

    def setUpGrammar() {
        def newNonTerminals = [:]
        nonTerminals.each {
            def count = 0
            def expr = it.value
            def newRules = []
            it.value.rules.each { 
                def newRule = []
                it.each { 
                    if (it.tag == expr.tag) { 
                        def newNonTerm = new NonTerminal(it.tag + count++, it.isLambda)
                        newNonTerminals[newNonTerm.tag] = newNonTerm
                        newRule << newNonTerm
                    }
                    else {
                        newRule << it 
                    } 
                }
                newRules << newRule
            }
            expr.rules = newRules
            count.times { newNonTerminals[expr.tag + it].rules = newRules }
        }
        newNonTerminals.each { nonTerminals[it.key] = it.value }
    }

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