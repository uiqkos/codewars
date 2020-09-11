def validate_pin(pin):
    return (len(list(filter(lambda x: x in '1234567890', pin))) == len(pin)) and (len(pin) in (4, 6))
