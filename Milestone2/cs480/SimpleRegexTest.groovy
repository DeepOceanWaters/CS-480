package classes

@Grab(group='org.spockframework', module='spock-core', version='0.7-groovy-2.0')

import spock.lang.*
import classes.SimpleRegex

class SimpleRegexTest extends Specification {
    void testTokenize(expr, tokens) {
        expect:
        new SimpleRegex(expr).tokenize().collect { it.class } == tokens

        where:
                      expr | tokens
            "0-9*(.0-9+)?" | [AtomToken, Token, AtomToken, Token, GroupStartToken, AtomToken, AtomToken, Token, AtomToken, Token, GroupEndToken, Token]
              "true|false" | [AtomToken, AtomToken, AtomToken, AtomToken, Token, AtomToken, AtomToken, AtomToken, AtomToken, AtomToken]
        "\\((a-z|A-Z)+\\)" | [Token, GroupStartToken, GroupStartToken, AtomToken, Token, AtomToken, Token, AtomToken, Token, AtomToken, GroupEndToken, Token, Token, GroupEndToken]
    }

    void testParse(expr, parsedTokens) {
        setup:
        def sreg = new SimpleRegex(expr)
        def tokens = sreg.tokenize()

        expect:
        sreg.parse(tokens).toTokenList().value == parsedTokens

        where:
                      expr | parsedTokens
            "0-9*(.0-9+)?" | ['(', '*', '-', '0', '9', '?', '(', '.', '+', '-', '0', '9']
              "true|false" | ['(', '|', '(', 't', 'r', 'u', 'e', '(', 'f', 'a', 'l', 's', 'e']
        "\\((a-z|A-Z)+\\)" | ['(', '\\', '(', '+', '(', '|', '(', '-', 'a', 'z', '(', '-', 'A', 'Z', '\\', ')']
    }
}