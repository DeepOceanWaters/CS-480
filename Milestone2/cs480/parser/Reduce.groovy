package cs480.parser

import cs480.parser.Action
import cs480.parser.ExpressionNode

class Reduce extends Action {
    def rhsExpr
    def rule

    Reduce(rhsExpr, rule) {
        this.rhsExpr = rhsExpr
        this.rule = rule
        this.eats = false
    }

    def perform(stateStack, outputStack) {
        def outList = []
        rule.size().times { outList << outputStack.pop() }
        (rule.size() - 1).times { stateStack.pop() }
        outputStack << new ExpressionNode(rhsExpr, outList.reverse())
    }
}