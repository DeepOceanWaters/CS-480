package cs480




    void testTokenize() {
        when:
        Lexer testLexer = new Lexer()
        testLexer.setup()

        
        def curToken
        testLexer.setFile("cs480/test1.txt")
        println ""
        while((curToken = testLexer.getNextToken()).value != '$end') { 
            print curToken.value
            print " " 
        }
        curToken = null
        println ""
        testLexer.setFile("cs480/test2.txt")
        while((curToken = testLexer.getNextToken()).value != '$end') { print "$curToken.value " }
        then:
        assert 1 == 1
    }

    testTokenize()