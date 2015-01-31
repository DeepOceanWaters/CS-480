package classes.grammar

import classes.grammar.Expression
import classes.grammar.Terminal
import classes.grammar.NonTerminal

class Grammar {
    def terminals = ['$': new Terminal('$')]    // Map<String, Terminal>
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

        if (!this.terminals['$']) this.terminals['$'] = new Terminal('$') // Add eof token if not present

        Collection.metaClass.eachWithPeek = { closure ->
            def current = null
            delegate?.each { next ->
                if (current) closure(current, next)
                current = next
            }
            if (current) closure(current, null)
        }
    }

    def init() {
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
                        def newNonTerm = new NonTerminal(it.tag + count++)
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
        startExpr.follows << terminals['$']

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
        def nonTermFollows = expr.follows.findAll { !it.isTerminal() }
        nonTermFollows.each { 
            expr.follows -= it
            expr.follows << completeFollows(it)
        }
        expr.follows = expr.follows.flatten()
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
    def toFollows(rhsExpr, expr) { 
        if (!expr) return rhsExpr 
        else if (expr.isTerminal()) return expr 
        else return expr.firsts 
    }
}