package classes.parser

import classes.parser.Action

class Goto extends Action {
    def state = new State()

    Goto() {
        this.eats = true
    }

    def perform(stateStack, outputStack) {
        stateStack.push(state.id)
    }
}