package cs480.grammar

import cs480.grammar.Item

class ItemSet {
    int sequence = 0
    String id
    boolean complete = false
    Map<String, Item> set = [:]

    ItemSet() { }

    ItemSet(String id) {
        this.id = id
    }

    int size() { set.size() }

    void add(Item item) {
        Item existingItem = set[item.toHash()]
        item.merge(existingItem)
        set[item.toHash()] = item
    }

    Item get(int index) { set[ set.keySet()[index] ] }

    Set toHash() { set.keySet() }

    void merge(ItemSet itemSet) { itemSet.set.each { this.set[it.key].merge(it.value) } }

    void generateClosureItems() {
        int index = 0
        while (index < this.size()) {
            Item item = this.get(index)
            item.getClosureItems().each { this.add(it) }
            index++
        }
    }

    void setReduceActions() { set.values().each { if (it.isFinished() && !it.hasReduceAction()) it.addAction(new Action(ActionType.Reduce, it.rule.id as String)) } }

    List<ItemSet> createTransitions() { 
        List<ItemSet> sets = []
        set.values().each { if (!it.hasTransitionAction()) sets << createTransition(it) } 
        return sets
    }
    
    ItemSet createTransition(Item item) {
        ItemSet nextSet = new ItemSet("$id-${sequence++}")
        set.values().each { 
            if (!it.isFinished()) it.createTransition(item, nextSet)
        }
        return nextSet
    }

    void replace(ItemSet replaceSet, ItemSet baseSet) { set.values().each { it.replace(replaceSet, baseSet) } }
}