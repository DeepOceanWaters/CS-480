package classes

import classes.Token

class AtomToken extends Token {
    AtomToken(value) {
        super(value)
    }

    Node parse(parentNode, tokens) {
        def newNode = new Node(this)
        return newNode
    }
}