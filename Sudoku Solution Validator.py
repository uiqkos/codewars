def get_square(i__, j__, board):
    result = []
    i_begin = (i__ // 3) * 3
    j_begin = (j__ // 3) * 3
    for i in range(i_begin, i_begin + 3):
        for j in range(j_begin, j_begin + 3):
            if i != i__ and j != j__:
                result.append(board[i][j])
    return result

def valid_solution(board):
    for i in range(9):
        for j in range(9):
            if board[i][j] in filter(lambda elem: elem != board[i][j], board[i]) \
            or board[i][j] in (board[k][j] for k in range(len(board)) if k != i) \
            or board[i][j] in get_square(i, j, board):
                return False

    return True
