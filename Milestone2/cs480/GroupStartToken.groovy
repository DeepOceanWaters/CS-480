package classes

import classes.Token

class GroupStartToken extends Token {
    GroupStartToken(value) {
        super(value)
    }

    Node parse(parentNode, tokens) {
        def newNode = new Node(this)
        parseHelper(newNode, tokens)
        return newNode
    }

    void parseHelper(newNode, tokens) {
        def nextNode = getNextNode(newNode, tokens)
        if (nextNode.token.value != ')') {
            newNode.addChild(nextNode)
            if (tokens.size() > 0) {
                parseHelper(newNode, tokens)
            }
        }
    }
}