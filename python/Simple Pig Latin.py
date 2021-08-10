def pig_it(text):
    return ' '.join([word[1:] + word[0] + 'ay' for word in text.split(' ')])
