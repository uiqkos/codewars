def tower_builder(n_floors):
    return [''.join(['{:^', str(n_floors*2-1), '}']).format('*'*i) for i in range(1, n_floors*2, 2)]
