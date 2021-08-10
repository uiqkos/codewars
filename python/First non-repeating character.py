def first_non_repeating_letter(string):
    for char in string:
        if string.count(char) == 1 or string.lower().count(char) == 1:
            return char
    return ''