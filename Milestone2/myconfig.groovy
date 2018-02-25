gram {
    tokens = [
        'bool',
        'int',
        'real',
        'string',
        'operators',
        'comparisons',
        'type',
        'id',
        'print',
        'if',
        'while',
        'let'
    ]
    rules = [
        integer {   name = 'int'
            isTerminal = true
            rules = [['number']]
        },
        number = {   name = 'number'
            isTerminal = false
            rules = [['number', 'digit'], ['digit']]
        },
        digit = {   name = 'digit'
            isTerminal = true
            rules = ['0'..'9']
        }
    ]
    discardables = [' ', '\t', '\n']
}