package cs480.grammar

import cs480.grammar.ActionType

class Action {
    ActionType type
    String actionId
    int level = 0

    Action(ActionType type, String actionId) {
        this.type = type
        this.actionId = actionId
    }

    Action(ActionType type, String actionId, int level) {
        this.type = type
        this.actionId = actionId
        this.level = level
    }
}
