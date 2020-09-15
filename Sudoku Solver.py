def get_possible(y, x, puzzle):

    possible_horizontal = list(filter(lambda i: i not in puzzle[y], list(range(1, 10))))
    possible_vertical = list(filter(lambda i: i not in [puzzle[j][x] for j in range(9)], list(range(1, 10))))
    possible_in_square = list(set(possible_horizontal) & set(possible_vertical))
    i_beg = (y // 3) * 3
    j_beg = (x // 3) * 3

    for i in range(i_beg, i_beg + 3):
        for j in range(j_beg, j_beg + 3):
            if puzzle[i][j] != 0:
                if puzzle[i][j] in possible_in_square:
                    possible_in_square.remove(puzzle[i][j])
    res = list(set(possible_in_square) & set(possible_vertical) & set(possible_horizontal))
    return res


def sudoku(puzzle):
    """return the solved puzzle as a 2d array of 9 x 9"""
    for i in range(9):
        for j in range(9):
            if puzzle[i][j] == 0:
                possible = get_possible(i, j, puzzle)
                print(i, j, puzzle[i][j], possible)
                for line in puzzle:
                    print(line)
                if len(possible) == 0:
                    return None

                for possible_ in possible:
                    next_puzzle = [p for p in puzzle]
                    next_puzzle[i][j] = possible_
                    next_solved_puzzle = sudoku(next_puzzle)

                    if next_solved_puzzle is not None:
                        return next_solved_puzzle

                return None
    return None

puzzle = [[5, 3, 0, 0, 7, 0, 0, 0, 0],
          [6, 0, 0, 1, 9, 5, 0, 0, 0],
          [0, 9, 8, 0, 0, 0, 0, 6, 0],
          [8, 0, 0, 0, 6, 0, 0, 0, 3],
          [4, 0, 0, 8, 0, 3, 0, 0, 1],
          [7, 0, 0, 0, 2, 0, 0, 0, 6],
          [0, 6, 0, 0, 0, 0, 2, 8, 0],
          [0, 0, 0, 4, 1, 9, 0, 0, 5],
          [0, 0, 0, 0, 8, 0, 0, 7, 9]]
print(sudoku(puzzle))
