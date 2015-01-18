enum TokenType {
    GroupStart,
    GroupEnd,
    Operation,
    Atom
}

String.metaClass.toToken = {
    Token outToken
    switch(delegate) {
    case Token.tokenTable:
        outToken = new Token(delegate, Token.tokenTable[delegate])
        break
    default:
        outToken = new Token(delegate, TokenType.Atom)
        break
    }
    return outToken
}

class Token {
    char value
    TokenType type

    static def tokenTable = [
        '*'  : TokenType.Operation,
        '+'  : TokenType.Operation,
        '?'  : TokenType.Operation,
        '-'  : TokenType.Operation,
        '|'  : TokenType.Operation,
        '^'  : TokenType.Operation,
        '\\' : TokenType.Operation,
        '('  : TokenType.GroupStart,
        ')'  : TokenType.GroupEnd
    ]

    Token() {}

    Token(value, type) {
        this.value = value as char
        this.type = type
    }
}

class SimpleRegex {
    String inputStr
    

    SimpleRegex(inputStr) {
        this.inputStr = inputStr
    }
    
    def tokenize() {
        return inputStr.collect { it.toToken() }
    }
}
