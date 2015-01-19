package classes

class Token {
    static Map<String, Token> tokenTable = [
        '*'  : new Token('*', 1, 0),
        '+'  : new Token('+', 1, 0),
        '?'  : new Token('?', 1, 0),
        '-'  : new Token('-', 1, 1),
        '|'  : new Token('|'),
        '^'  : new Token('^', 0, 1),
        '\\' : new Token('\\', 0, 1),
        '('  : new GroupStartToken('('),
        ')'  : new GroupEndToken(')')
    ]

    String value
    int tokensBefore
    int tokensAfter

    Token() {}

    Token(value) {
        this.value = value
    }

    Token(tokensBefore, tokensAfter) {
        this.tokensBefore = tokensBefore
        this.tokensAfter = tokensAfter
    }

    Token(value, tokensBefore, tokensAfter) {
        this.value = value
        this.tokensBefore = tokensBefore
        this.tokensAfter = tokensAfter
    }

    Node parse(parentNode, tokens) {
        def newNode = new Node(this)
        tokensBefore.times { addPrevNode(newNode, parentNode) }
        tokensAfter.times { addNextNode(newNode, tokens) }
        return newNode
    }

    static void addPrevNode(newNode, parentNode) {
        def prevNode = getPrevNode(parentNode)
        newNode.addChild(prevNode)
    }

    static Node getPrevNode(parentNode) {
        def prevNode = parentNode.removeLastNode()
        return prevNode
    }

    static void addNextNode(newNode, tokens) {
        def nextNode = getNextNode(newNode, tokens)
        newNode.addChild(nextNode)
    }

    static Node getNextNode(newNode, tokens) {
        return tokens.remove(0).parse(newNode, tokens)
    }
}