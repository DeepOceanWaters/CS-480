package classes

import classes.TokenTag

class Token {
    static Map<String, Token> tokenTable = [
        '*'  : new OpToken(TokenTag.Asterix),
        '+'  : new OpToken(TokenTag.Plus),
        '?'  : new OpToken(TokenTag.QuestionMark),
        '-'  : new OpToken(TokenTag.Minus),
        '['  : new OpToken(TokenTag.Lbracket),
        ']'  : new OpToken(TokenTag.Rbracket)
        '^'  : new OpToken(TokenTag.Not),
        '\\' : new OpToken(TokenTag.Backslash),
        '('  : new OpToken(TokenTag.Lparen),
        ')'  : new OpToken(TokenTag.Rparen)
    ]

    TokenType subType

    Token(TokenType subType) {
        this.subType = subType
    }

    boolean match(Token token) { this.class == token.class && this.subType == token.subType }
}