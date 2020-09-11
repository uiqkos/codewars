def next_bigger(n):
    list_digit = [d for d in str(n)]
    if len(list_digit) < 2: return - 1

    for i in range(len(list_digit) - 2, -1, -1):
        for j in range(len(list_digit) - 1, i, -1):
            if list_digit[i] < list_digit[j]:
                list_digit[i], list_digit[j] = list_digit[j], list_digit[i]
                return int(''.join(list_digit[:i + 1] + sorted(list_digit[i + 1:])))

    return -1
