def balanced_parens(n, left_count=None, right_count=None, stack=[]):
    if left_count is None and right_count is None:
        left_count, right_count = n, n

    if left_count == 0 and right_count == 0 \
    or left_count == -1 or right_count == -1:
        return ['']

    res = []
    if len(stack) > n * 2:
        return ['']

    if left_count != 0:
        for pair in balanced_parens(n, left_count=left_count - 1, right_count=right_count, stack=stack+['(']):
            res.append(f'({pair}')

    if len(stack) == 0:
        return res

    if stack.pop() != '(':
        return res

    if right_count != 0:
        for pair in balanced_parens(n, left_count=left_count, right_count=right_count - 1, stack=stack):
            res.append(f'){pair}')

    return res

print(balanced_parens(4))
