package cs480.parser

import cs480.parser.Action

class Shift extends Action {
    def state = new State()

    Shift() {
        this.eats = true
    }

    Shift(state) {
        this.eats = true
        this.state = state
    }

    def perform(stateStack, outputStack) {
        stateStack.push(state.id)
    }
}