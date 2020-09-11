class Cons:
    def __init__(self, head, tail):
        self.head = head
        self.tail = tail

    def to_array(self):
        return [self.head] + (self.tail.to_array() if self.tail is not None else [])

    @classmethod
    def from_array(cls, arr):
        if len(arr) == 0:
            return None

        cons_head = Cons(arr[0], None)
        cons_tail = cons_head

        for a in arr[1:]:
            cons_tail.tail = Cons(a, None)
            cons_tail = cons_tail.tail
        return cons_head

    def filter(self, fn):
        return Cons.from_array(list(filter(fn, self.to_array())))

    def map(self, fn):
        self.head = fn(self.head)
        if self.tail is not None:
            self.tail.map(fn)
            return self
