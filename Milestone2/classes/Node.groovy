package classes

class Node {
    def parent
    def children = []
    Token token

    Node(token) {
        this.token = token
    }

    def addChild(node) {
        if(node.parent) {
            node.parent.children.remove(node)
            node.parent = this
        }
        children.add(node)
    }

    def setParent(node) {
        if(parent) {
            parent.children.remove(this)
        }
        parent = node
    }

    def removeNode(index) {
        def node = children.remove(index)
        node.parent = null
        return node
    }

    def removeLastNode() {
        return removeNode(children.size() - 1)
    }

    def printThis() {
        println "$token.value: ${children.token.value.join(', ')}"
        children.each { if (it.token.class != AtomToken) it.printThis() }
    }
}