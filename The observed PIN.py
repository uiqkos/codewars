def get_pins(observed):
    possible = []
    for n in [int(n) for n in observed]:
        possible_ = []
        if n == 0:
            possible.append(['8', '0'])
            continue

        for bias in [-1, 0, 1]:
            if 0 <= (n - 1) % 3 + bias < 3:
                possible_.append(str(n + bias))

        for bias in [-3, 3]:
            if 0 < n + bias < 10:
                possible_.append(str(n + bias))

        if n == 8:
            possible_.append('0')

        possible.append(possible_)

    res = possible[0]
    for i in range(1, len(possible)):
        res = [r + n for n in possible[i] for r in res]

    return res
