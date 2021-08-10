def move_zeros(array: list):
    return list(filter(lambda x: x != 0 or isinstance(x, bool), array)) \
         + list(filter(lambda x: x == 0 and not isinstance(x, bool), array))
