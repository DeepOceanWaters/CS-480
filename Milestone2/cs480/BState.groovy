package classes

import classes.TokenMatch

BState {
    Matchable matchValue
    int exprId
    List<State> nextStates

    boolean isFinal() { exprId != null }

    List<State> getNextStates(Matchable matchVal) { nextStates.findAll { matchVal.match(it.matchValue) }.collect { it.getNextStates(matchVal) } }
}

MState {
    Map<TokenTag, StateAction> actions
    Map<TokenTag, StateAction> lookahead
    Map<ExprTag, Integer> goto
}

StateTransition {
    StateAction action
    int actionId
}

enum StateAction {
    Error,
    Shift,
    Reduce
}

abstract class Expression {
    String tag

    abstract boolean isTerminal()
}

class NonTerminal extends Expression {
    List<ArrayList<Expression>> rules
    Set<Terminal> firsts
    Set<Terminal> follows

    boolean isTerminal() { false }
}

class Terminal extends Expression {
    boolean isTerminal() { true }
}