
# (n * n**3 > m) => (n**4 > m)
#
# let N_i = n_i^3 + (n_i - 1)^3 + ... + 1
#   => N_i+1 = N_i + (n_i + 1)^3
#   and N_i < N_i+1 <= m
#
def find_nb(m: int):
    N = 1
    for i in range(2, m):
        N = N + i**3
        if N > m:
            return -1
        if N == m:
            return i
