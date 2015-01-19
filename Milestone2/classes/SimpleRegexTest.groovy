package classes

@Grab(group='org.spockframework', module='spock-core', version='0.7-groovy-2.0')

import spock.lang.*
import classes.SimpleRegex

class SimpleRegexTest extends Specification {
    void testTokenize(expr, tokens) {
        expect:
        new SimpleRegex(expr).tokenize().collect { it.class } == tokens

        where:
        expr       | tokens
        "x*(.x+)?" | [AtomToken.class, Token.class, GroupStartToken.class, AtomToken.class, AtomToken.class, Token.class, GroupEndToken.class, Token.class]
    }

    void testParse() {

    }
}