package cs480.parser

import cs480.parser.Action

class ActionList extends Action {
    def actions = []

    ActionList() {
        this.eats = false
    }

    def perform(stateStack, outputStack) {
        actions.each { it.perform(stateStack, outputStack) }
    }
}