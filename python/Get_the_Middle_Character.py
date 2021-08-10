def get_middle(s: str):
    l = int(len(s) / 2)
    return s[l] if len(s) % 2 else s[l - 1] + s[l]
