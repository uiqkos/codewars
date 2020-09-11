def determinant(matrix: list):
    if len(matrix) == 1: return matrix[0][0]
    return sum([matrix[0][i] * pow(-1, i) * determinant([line[:i] + line[i + 1:] for line in matrix[1:]]) for i in range(len(matrix[0]))])