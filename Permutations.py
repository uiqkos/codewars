def permutations(string):
    result = []
    if len(string) == 1:
        return string
    
    for i in range(len(string)):
        perms = permutations([string[j] for j in range(len(string)) if j != i])
        for permutation in perms:
            result.append(string[i] + permutation)
    
    return list(set(result))
