def solution(string, markers):
    lines = string.split('\n')
    result = []
    markers = [' ' + marker for marker in markers] + markers
    print(markers)
    for line in lines:
        for marker in markers:
            index = line.find(marker)
            line = line[:] if index == -1 else line[:index]
        result.append(line)
    return '\n'.join(result)
