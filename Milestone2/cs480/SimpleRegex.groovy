package classes

class SimpleRegex {
    String inputStr
    Map<Integer, MState> fsa = [
        0: [actions: [TokenTag.Not:,TokenTag.], ]
    ]

    SimpleRegex(inputStr) {
        this.inputStr = inputStr
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
    }
    
    def tokenize() {
        return inputStr.collect { it.toToken() }
    }

    def parse(tokens) {
        def startToken = new GroupStartToken('(')
        return startToken.parse(null, tokens)
    }
}
