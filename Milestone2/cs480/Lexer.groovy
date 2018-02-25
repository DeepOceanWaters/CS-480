package cs480

class Lexer {
    Map<String, Token> symbolsTable = [:]
    Set<StateMachine> stateMachines = [] as Set
    Set<StateMachine> curStateMachines = [] as Set
    Set<StateMachine> prevStateMachines = [] as Set
    List<Token> tokens = []
    Set<String> discardables = [] as Set
    String curStr = ""

    void setup() {
        // setup keywords
        symbolsTable['int'] = new Token(TokenTag.Type, 'int')
        symbolsTable['bool'] = new Token(TokenTag.Type, 'bool')
        symbolsTable['real'] = new Token(TokenTag.Type, 'real')
        symbolsTable['string'] = new Token(TokenTag.Type, 'string')
        symbolsTable['and'] = new Token(TokenTag.Operator, 'and')
        symbolsTable['or'] = new Token(TokenTag.Operator, 'or')
        symbolsTable['not'] = new Token(TokenTag.Operator, 'not')
        symbolsTable['true'] = new Token(TokenTag.Boolean, 'true')
        symbolsTable['false'] = new Token(TokenTag.Boolean, 'false')
        symbolsTable['sin'] = new Token(TokenTag.Keyword, 'sin')
        symbolsTable['cos'] = new Token(TokenTag.Keyword, 'cos')
        symbolsTable['tan'] = new Token(TokenTag.Keyword, 'tan')
        symbolsTable['stdout'] = new Token(TokenTag.Keyword, 'stdout')
        symbolsTable['if'] = new Token(TokenTag.Keyword, 'if')
        symbolsTable['while'] = new Token(TokenTag.Keyword, 'while')
        symbolsTable['let'] = new Token(TokenTag.Keyword, 'let')
        symbolsTable['sin'] = new Token(TokenTag.Keyword, 'sin')
        symbolsTable['+'] = new Token(TokenTag.Operator, '+')
        symbolsTable['-'] = new Token(TokenTag.Operator, '-')
        symbolsTable['*'] = new Token(TokenTag.Operator, '*')
        symbolsTable['/'] = new Token(TokenTag.Operator, '/')
        symbolsTable['%'] = new Token(TokenTag.Operator, '%')
        symbolsTable['^'] = new Token(TokenTag.Operator, '^')
        symbolsTable['<'] = new Token(TokenTag.Operator, '<')
        symbolsTable['>'] = new Token(TokenTag.Operator, '>')
        symbolsTable['='] = new Token(TokenTag.Operator, '=')
        symbolsTable['<='] = new Token(TokenTag.Operator, '<=')
        symbolsTable['>='] = new Token(TokenTag.Operator, '>=')
        symbolsTable['!='] = new Token(TokenTag.Operator, '!=')
        symbolsTable[':='] = new Token(TokenTag.Operator, ':=')
        symbolsTable['('] = new Token(TokenTag.Lparen, '(')
        symbolsTable[')'] = new Token(TokenTag.Rparen, ')')    

        // setup integer state machine
        StateMachine intSM = new StateMachine(TokenTag.Integer)
        intSM.stateTable[0] = [:]
        ('0'..'9').each { intSM.stateTable[0][it] = 0 }
        intSM.finalStates = [0: true]

        // setup real state machine
        StateMachine realSM = new StateMachine(TokenTag.Real)
        realSM.stateTable[0] = [:]
        ('0'..'9').each { realSM.stateTable[0][it] = 0 }
        realSM.stateTable[0]['.'] = 1

        realSM.stateTable[1] = [:]
        ('0'..'9').each { realSM.stateTable[1][it] = 2 }

        realSM.stateTable[2] = [:]
        ('0'..'9').each { realSM.stateTable[2][it] = 2 }

        realSM.finalStates = [0: false, 1: false, 2: true]

        // setup string state machine
        StringStateMachine stringSM = new StringStateMachine(TokenTag.String)
        stringSM.defaultStates[1] = 1
        stringSM.stateTable[0] = [:]
        stringSM.stateTable[0]['"'] = 1
        stringSM.stateTable[1] = [:]
        stringSM.stateTable[1]['"'] = 2
        stringSM.stateTable[2] = [:]

        stringSM.finalStates = [0: false, 1: false, 2: true]

        // setup id state machine
        StateMachine idSM = new StateMachine(TokenTag.Id)
        idSM.stateTable[0] = [:]
        idSM.stateTable[0]['_'] = 1
        ('a'..'z').each { idSM.stateTable[0][it] = 1 }
        ('A'..'Z').each { idSM.stateTable[0][it] = 1 }

        idSM.stateTable[1] = [:]
        idSM.stateTable[1]['_'] = 1
        ('a'..'z').each { idSM.stateTable[1][it] = 1 }
        ('A'..'A').each { idSM.stateTable[1][it] = 1 }
        ('0'..'9').each { idSM.stateTable[1][it] = 1 }

        idSM.finalStates = [0: false, 1: true]

        // setup discardables
        discardables << ' '
        discardables << '\t'
        discardables << '\n'

        stateMachines << intSM
        stateMachines << realSM
        stateMachines << stringSM
        stateMachines << idSM
    }

    boolean discardable(item) { discardables.contains(item) }

    void reset(full) {
        if (full) tokens.each { tokens -= it }
        stateMachines.each { it.reset() }
        stateMachines.each { curStateMachines << it }
        prevStateMachines.each { prevStateMachines -= it }
        curStr = ""
    }

    void tokenizeFile(filename) {
        FileInputStream byteStream = new FileInputStream(filename)
        reset(true)
        tokenize(byteStream)
        byteStream.close()
    }

    void tokenize(stream) { stream.eachByte { tryParse((it as char) as String) } }

    void tryParse(curChar) { !discardable(curChar) && curStateMachines ? processChar(curChar) : addToken() }

    void processChar(curChar) {
        curStateMachines.each { !it.process(curChar) ? curStateMachines -= it : prevStateMachines << it }
        if (curStateMachines) curStr += curChar
        else {
            prevStateMachines.each { curStateMachines << it }
            addToken()
            curStr += curChar
            addToken()
        }
    }

    void addToken() {
        if (curStr) {
            Token symbolToken = symbolsTable[curStr]
            if (symbolToken) tokens << symbolToken
            else createNewToken()
            reset()

        }
    }

    void createNewToken() {
        Set<StateMachine> tmpStateMachines = curStateMachines ? curStateMachines : prevStateMachines
        Set<StateMachine> accStateMachines = tmpStateMachines.findAll { it.accepting() }
        if (accStateMachines.size() > 1) throw new Exception("Ambiguous string for $accStateMachines.tokenTag: $curStr")
        else if (accStateMachines.size() == 1) tokens << accStateMachines[0].createNewToken(curStr)
    }
}

class StateMachine {
    TokenTag tokenTag
    Map<Integer, Map<String, Integer>> stateTable = [:]
    Map<Integer, Boolean> finalStates = [:]
    Integer currentState = 0

    StateMachine() { }

    StateMachine(TokenTag tokenTag) {
        this.tokenTag = tokenTag
    }

    void reset() { currentState = 0 }

    boolean process(String item) { 
        Integer nextState = stateTable[currentState][item]
        if (nextState != null) currentState = nextState
        return nextState != null
    }

    boolean accepting() { finalStates[currentState] }

    Token createNewToken(String str) { new Token(tokenTag, str) }

}

class StringStateMachine extends StateMachine {
    Map<Integer, Integer> defaultStates = [:]

    StringStateMachine() { }

    StringStateMachine(TokenTag tokenTag) {
        this.tokenTag = tokenTag
    }

    boolean process(String item) {
        Integer nextState = stateTable[currentState][item]
        if (!nextState) nextState = defaultStates[currentState]
        return (currentState = nextState) != null
    }
}

enum TokenTag {
    Boolean,
    Integer,
    Real,
    String,
    Id,
    Operator,
    Type,
    Keyword,
    Lparen,
    Rparen
}

class Token {
    TokenTag tag
    String value

    Token() { }

    Token(TokenTag tag, String value) {
        this.tag = tag
        this.value = value
    }
}
