import classes.*

String.metaClass.toToken = {
    Token outToken
    switch(delegate) {
    case Token.tokenTable:
        outToken = Token.tokenTable[delegate]
        break
    default:
        outToken = new AtomToken(delegate)
        break
    }
    return outToken
}

def reg = new SimpleRegex("x*(.x+)?")
def tokens = reg.tokenize()
reg.parse(tokens).printThis()