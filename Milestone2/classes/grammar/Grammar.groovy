package classes.grammar

import classes.grammar.Expression
import classes.grammar.Terminal
import classes.grammar.NonTerminal

class Grammar {
    def terminals = ['$': new Terminal('$')]    // Map<String, Terminal>
    def nonTerminals = [:] // Map<String, NonTerminal>

    /**
     * Map<String, Terminal> terminals
     * Map<String, NonTerminal> nonTerminals
     */
    Grammar(terminals, nonTerminals) {
        this.terminals = terminals
        this.nonTerminals = nonTerminals

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

    /**
     * return void
     * Get startExpr from nonTerminals list and use it as base expr
     * for the recursive function.
     */
    def getFirsts() {
        def startExpr = nonTerminals["START"]
        if (!startExpr) throw new Exception("Start Expression not defined.")

        nonTerminals.each { if (it.key != "START") startExpr.rules.add([ it.value ]) }
        println startExpr.rules.tag
        getFirsts(startExpr, [])
    }

    /**
     * Expression expr
     * List<NonTerminal> exprsSeen
     * return ArrayList<Expression>
     */
    def getFirsts(expr, exprsSeen) {
        if (expr.isTerminal()) return [ expr ]
        if (exprsSeen.contains(expr)) return expr.firsts

        println expr.tag
        exprsSeen.add(expr)
        return expr.firsts = expr.rules.collect { getFirsts(it[0], exprsSeen) }.flatten() as Set
    }

    /** 
     * ArrayList<Expression> exprList
     * return void
     * Call getFirsts() before calling this, otherwise follows will not be set correctly
     */
    def getFollows(exprList) { exprList.eachWithPeek { cur, next -> if (cur.isTerminal()) cur.follows += toFollows(next) } }

    /**
     * return Set<Expression>
     * Expression expr 
     */
    def toFollows(expr) { if (!expr) [ terminals['$'] ] else if (expr.isTerminal()) [ expr ] else expr.firsts }
}