package cs480.grammar

class Token extends Expression {
    String value

    Token(String tag) {
        this.tag = tag
    }

    Token(String tag, String value) {
        this.tag = tag
        this.value = value
    }

    boolean isTerminal() { true }
    
}
