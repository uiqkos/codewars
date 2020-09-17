import re
class Variable:
    def __init__(self, name, coef):
        self.name = name
        self.coef = coef

    @staticmethod
    def parse(string: str):
        if len(string) == 1:
            return Variable(string, 1)
        return Variable(string[-1], int(string[:-1]) if string[:-1] != '-' else -1)


def parse_expr(expr):
    # Open brackets
    while '(' in expr:
        # Get bracket indexes
        left_index, right_index = expr.index('('), -1
        bracket_counter = 1
        for i in range(left_index + 1, len(expr)):
            if expr[i] == '(':
                bracket_counter += 1
            elif expr[i] == ')':
                bracket_counter -= 1
            if bracket_counter == 0:
                right_index = i
                break

        # Calculate expression in brackets
        in_brackets_expr = parse_expr(expr[left_index + 1: right_index])

        # Get brackets coef
        # TODO Remove brackets coef
        brackets_coef = 1
        try:
            if left_index > 1:
                if expr[left_index - 1] != ' ':
                    brackets_coef = int(expr[left_index - 1])
                else:
                    brackets_coef = int(expr[left_index - 2])
                del expr[left_index - 2 : left_index]
        except Exception:
            pass

        expr = expr[:left_index].split()
        # Apply brackets coef to expression in brackets
        for index in range(len(in_brackets_expr)):
            in_brackets_expr[index].coef = in_brackets_expr[index].coef * brackets_coef

        expr += in_brackets_expr

    if isinstance(expr, str):
        expr = expr.split()

    # Dodge '-'
    for i in range(len(expr)):
        if expr[i] == '-':
            expr[i] = '+'
            expr[i + 1] = '-' + expr[i + 1]

    # Remove '+'
    expr = list(filter('+'.__ne__, expr))

    # To list<Variable>
    var_list = []
    for var in expr:
        if isinstance(var, str):
            var_list.append(Variable.parse(var))
        else:
            var_list.append(var)
    return var_list
    #

def calc(expr):
    res = {}

    for var in expr:
        if var.name in res.keys():
            res[var.name] += var.coef
        else:
            res[var.name] = var.coef
    return [Variable(name, coef) for name, coef in res.items()]

def simplify(examples: list, formula):
    pass

# simplify(["a + a = b", "b - d = c ", "a + b = d"], "c + a + b")

vars = parse_expr('a + b - c + 2a - 3b')
print([var.name for var in vars])
