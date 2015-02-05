package classes

import classes.Token

class AtomToken extends Token {
    String value

    AtomToken(TokenType subType, String value) {
        super(subType)
        this.value = value
    }

    boolean match(AtomToken token) { super.match(token) && this.value == token.value }
}