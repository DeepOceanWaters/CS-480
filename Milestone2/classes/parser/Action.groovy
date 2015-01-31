package classes.parser

abstract class Action {
    def eats
    
    abstract def perform(stateStack, outputStack)
}