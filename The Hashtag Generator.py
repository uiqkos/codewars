def generate_hashtag(s):
    res = '#' + s.title().replace(' ', '')
    return False if len(res) == 1 or len(res) > 140 else res
