def find_outlier(integers: list):
    odds = list(filter(lambda x: not(x % 2), integers))
    if len(odds) > 1:
        return list(filter(lambda x: x not in odds, integers))[0]
    return odds[0]
