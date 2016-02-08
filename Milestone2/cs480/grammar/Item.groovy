package cs480.grammar

import cs480.grammar.Expression
import cs480.grammar.Terminal
import cs480.grammar.NonTerminal
import cs480.grammar.Rule

class Item {
    Rule rule
    int markerPos = 0
    Set<Terminal> lookahead = [] as Set
    Action transitionAction
    Action reduceAction

    Item() { }

    Item(Rule rule) {
        this.rule = rule
    }

    Item(Rule rule, Set<Terminal> lookahead) {
        this.rule = rule
        this.lookahead = lookahead
    }

    Item(Rule rule, int markerPos, Set<Terminal> lookahead) {
        this.rule = rule
        this.markerPos = markerPos
        this.lookahead = lookahead
    }

    Item cloneForNext() { new Item(rule, markerPos + 1, lookahead) }

    Expression getCurExpr() { rule.rhs[markerPos] }

    Expression getNext() { rule.rhs[markerPos + 1] }

    Set<Terminal> getFollows() {
        Set<Terminal> follows = [] as Set
        Expression nextExpr = peek()
        if (!nextExpr)
            follows += lookahead
        else if (nextExpr.isTerminal()) 
            follows << nextExpr
        else
            follows = nextExpr.firsts
        return follows
    }

    Expression peek() { rule.rhs[markerPos + 1] }

    boolean isFinished() { markerPos == rule.rhs.size() - 1 }

    String toHash() { "${markerPos}${rule.lhs.tag}${rule.rhs.collect{ it.tag }}" }

    void addAction(Action action) {
        if (rule.level != null) action.level = rule.level
        if (action.type == ActionType.Reduce || action.type == ActionType.Accept) reduceAction = action
        else transitionAction = action
    }

    /**
     * Merge the given item into this item. Overwrites this item's action with 
     * the given item's action => conflict if they are different.
     */
    void merge(Item item) { 
        if (!item) return
        this.lookahead += item.lookahead
        if (!this.reduceAction) this.reduceAction = item.reduceAction
        if (!this.transitionAction) this.transitionAction = item.transitionAction
    }

    List<Item> getClosureItems() {
        Expression curExpr = getCurExpr()
        if (curExpr.isTerminal()) return []
        curExpr.rules.each { if(!(it instanceof Rule)) println it.tag }
        return curExpr.rules.collect { new Item(it, getFollows()) }
    }

    void createTransition(Item item, ItemSet set) {
        if (this.getCurExpr() != item.getCurExpr()) return
        ActionType actionType = this.getCurExpr().isTerminal() ? ActionType.Shift : ActionType.Goto
        addAction(new Action(actionType, set.id))
        set.add(this.cloneForNext())
    }

    void replace(ItemSet set, ItemSet baseSet) { if (transitionAction?.actionId == set.id) transitionAction.actionId = baseSet.id }

    boolean hasReduceAction() { reduceAction != null }

    boolean hasTransitionAction() { transitionAction != null }
}