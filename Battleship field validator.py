
def validate(row, column, field):
    return \
        0 <= row < 10 and \
        0 <= column < 10 and \
        field[row][column] == 1

def get_ship_size(x, y, field):
    field[x][y] = 0
    for i in [-1, 0, 1]:
        for j in [-1, 0, 1]:
            if j + i == 1 or j + i == -1:
                row, column = x + i, y + j
                if validate(row, column, field):
                    field[row][column] = 0
                    return 1 + get_ship_size(row, column, field)
    return 1

def validate_battlefield(field):
    # battleship = 1  # size: 4 cells
    # cruisers = 2    # size: 3 cells
    # destroyers = 3  # size: 2 cells
    # submarine = 4   # size: 1 cells

    validate_ships = {
        4: 1,
        3: 2,
        2: 3,
        1: 4
    }

    # Check diagonals
    for bias in range(10):
        # Main diag
        prev_with_row_bias = 0
        prev_with_column_bias = 0

        for i in range(10 - bias):
            # 1 0
            # 0 1
            if field[i + bias][i] + prev_with_row_bias == 2 \
            or field[i][i + bias] + prev_with_column_bias == 2:
                return False

            prev_with_row_bias = field[i + bias][i]
            prev_with_column_bias = field[i][i + bias]

        # Backward diag
        prev_with_row_bias_backward = 0
        prev_with_column_bias_backward = 0

        for i in range(1, 10 - bias):
            # 0 1
            # 1 0
            if field[i - 1 + bias][-i] + prev_with_row_bias_backward == 2 \
            or field[i - 1][-i - bias] + prev_with_column_bias_backward == 2:
                return False

            prev_with_row_bias_backward = field[i - 1 + bias][-i]
            prev_with_column_bias_backward = field[i - 1][-i - bias]

    for row in range(10):
        for column in range(10):
            if field[row][column] == 1:
                try:
                    validate_ships[get_ship_size(row, column, field.copy())] -= 1
                except KeyError:  # KeyError => ship size > 4
                    return False

    return list(validate_ships.values()) == [0] * 4