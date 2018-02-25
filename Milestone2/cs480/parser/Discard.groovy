package cs480.parser

import cs480.parser.Action

/* Maybe rename class? */
class Discard extends Action {
    Discard() {
        this.eats = true
    }

    def perform(stateStack, outputStack) {
        outputStack.pop()
    }
}