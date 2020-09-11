def make_readable(seconds:int):
    hours = int(seconds / 3600)
    mins = int(seconds / 60 - hours*60)
    secs = seconds % 60
    return '{:02}:{:02}:{:02}'.format(hours, mins, secs)
