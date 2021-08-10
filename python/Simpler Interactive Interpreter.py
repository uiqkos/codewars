import re

def tokenize(expression):
    if expression == "":
        return []

    regex = re.compile("\s*(=>|[-+*\/\%=\(\)]|[A-Za-z_][A-Za-z0-9_]*|[0-9]*\.?[0-9]+)\s*")
    tokens = regex.findall(expression)
    return [s for s in tokens if not s.isspace()]

class Interpreter:
    @staticmethod
    def get_braces_indexes(tokens):
        if '(' not in tokens:
            return -1, -1

        braces = {'(': 1, ')': -1}
        left_brace_index = tokens.index('(')
        stack_counter = 1
        for index, token in enumerate(tokens[left_brace_index + 1:]):
            if token in braces.keys():
                stack_counter += braces[token]
            if stack_counter == 0:
                return left_brace_index, index + left_brace_index + 1

        return -1, -1

    def __init__(self):
        self.functions = {
            '%': lambda n1, n2: n1 % n2,
            '/': lambda n1, n2: n1 / n2,
            '*': lambda n1, n2: n1 * n2,
            '-': lambda n1, n2: n1 - n2,
            '+': lambda n1, n2: n1 + n2,
            '=': lambda var, value: self._set_variable(var, value)
        }
        self.vars = {}

    def _set_variable(self, var, value):
        self.vars[var] = self.parse_token(value)
        return self.vars[var]

    def parse_token(self, token):
        return self.vars[token] if token in self.vars.keys() else int(token)

    def calculate(self, operator, arg1, arg2):
        if operator != '=':
            arg1 = self.parse_token(arg1)
        arg2 = self.parse_token(arg2)

        return self.functions[operator](
            arg1,
            arg2
        )

    def input(self, expression, is_tokenize=False):
        if not is_tokenize:
            tokens = tokenize(expression)
        else:
            tokens = expression

        if len(tokens) == 0:
            return ''

        if len(tokens) == 1:
            return self.parse_token(tokens[0])

        if len(tokens) == 2:
            raise Exception('Invalid syntax')

        left_brace, right_brace = self.get_braces_indexes(tokens)
        while left_brace != -1:
            tokens[left_brace] = self.input(tokens[left_brace+1:right_brace], is_tokenize=True)
            del tokens[left_brace+1:right_brace+1]
            left_brace, right_brace = self.get_braces_indexes(tokens)

        while len(tokens) > 1:
            for op in filter(lambda operator: operator in tokens, self.functions.keys()):
                op_index = tokens.index(op)
                tokens[op_index - 1] = self.calculate(op, tokens[op_index - 1], tokens[op_index + 1])
                del tokens[op_index:op_index + 2]

        return tokens[0]
