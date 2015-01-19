package classes

import classes.Token

class GroupEndToken extends Token {
    GroupEndToken(value) {
        super(value)
    }

    Node parse(parendNode, tokens) {
        def newNode = new Node(this)
        return newNode
    }
}
