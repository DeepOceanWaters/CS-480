package cs480

@Grab(group='org.spockframework', module='spock-core', version='0.7-groovy-2.0')

import spock.lang.*
import cs480.Lexer

class LexerTest extends Specification {
    void testTokenize() {
        when:
        Lexer testLexer = new Lexer()
        testLexer.setup()

        then:
        testLexer.tokenizeFile("cs480/test1.txt")
        println testLexer.tokens.value
        testLexer.tokenizeFile("cs480/test2.txt")
        println testLexer.tokens.value
    }
}