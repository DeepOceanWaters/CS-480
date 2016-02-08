package cs480.grammar

import cs480.grammar.Grammar 
import cs480.grammar.ItemSet
import cs480.grammar.Item

class ItemSetCreator {
    List<Rule> rules = []
    List<ItemSet> toDoList = []
    List<ItemSet> incList = []
    Map<Set, ItemSet> doneList = [:]
    ItemSet curSet = null
    ItemSet fromSet = null
    
    ItemSetCreator() { }

    void createStartSet(NonTerminal startExpr) {
        def set = new ItemSet("0")
        def startItem = new Item(startExpr.rules[0])
        startItem.lookahead += startExpr.follows
        startItem.addAction(new Action(ActionType.Accept, startItem.rule.id as String))
        set.add(startItem)
        toDoList << set
    }

    def createItemSets() {
        while (toDoList || incList) {
            while (toDoList) {
                curSet = toDoList.pop()
                curSet.generateClosureItems()
                curSet.setReduceActions()
                // continue if successfully merged with existing set
                if (tryMerge(curSet)) continue
                // curSet could not be merged; it is now done
                println "created set: $curSet.id"
                doneList[curSet.toHash()] = curSet
                incList << curSet 
            }

            if (incList) {
                curSet = incList.pop()
                println "creating transitions for: $curSet.id"
                toDoList += curSet.createTransitions()
                curSet.complete = true
                fromSet = curSet
            }
        }
    }

    def tryMerge(set) {
        def merged = false
        def baseSet = doneList[set.toHash()]

        if (baseSet) { 
            baseSet.merge(set)
            fromSet.replace(set, baseSet)
            merged = true
        }

        return merged
    }

    def toParseTable() {
        Map<String, Map<String, Map<String, Action>>> stateTable = [:]
        doneList.values().each {
            Map<String, Map<String, Action>> setTable = [:]
            it.set.values().each { item ->
                Map<String, Action> actionsTable = [:]
                if (item.hasReduceAction()) {
                    item.lookahead.each { 
                        def reduceAction = actionsTable[it.tag]
                        if (reduceAction) {
                            if (reduceAction.level < item.reduceAction.level){
                                actionsTable[it.tag] = item.reduceAction
                            }
                            else if (reduceAction.level == item.reduceAction.level) {
                                //throw new Exception("Reduce reduce error in grammar!")
                            }
                        }
                        else {
                            //println "Adding reduce action $item.rule.lhs.tag"
                            actionsTable[it.tag] = item.reduceAction 
                        }
                    }
                }
                if (item.hasTransitionAction())
                    actionsTable.action = item.transitionAction
                setActions(item, setTable, actionsTable)
                //setTable[item.getCurExpr().tag] = actionsTable
            }
            stateTable[it.id] = setTable
        }
        return stateTable
    }

    void setActions(item, setTable, actionsTable) {
        def existingTable = setTable[item.getCurExpr().tag]
        if (existingTable) {
            println "exists!"
            actionsTable.each {
                if (!existingTable[it.key]) existingTable[it.key] = it.value
            }
        }
        else {
            setTable[item.getCurExpr().tag] = actionsTable
        }
    }
}