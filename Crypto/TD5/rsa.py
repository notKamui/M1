from math import gcd


def gcd(a, b):
    if b == 0:
        return a
    else:
        return gcd(b, a % b)


def xgcd(a, b):
    if b == 0:
        return (a, 1, 0)
    else:
        d, u, v = xgcd(b, a % b)
        return (d, v, u - (a//b)*v)


def invmod(a, n):
    d, u, _ = xgcd(a, n)
    if d == 1:
        return u % n
    else:
        return None


def int_to_str(n: int) -> str:
    return n.to_bytes((n.bit_length() + 7) // 8, 'big').decode('ascii', 'replace')


def str_to_int(s: str) -> int:
    return int.from_bytes(s.encode('ascii', 'replace'), 'big')


class RSA:
    def __init__(self, p: int, q: int, e: int = None):
        Pn = (p - 1) * (q - 1)
        d = xgcd(e, Pn)[1]
        if e is None:
            e = 2
            while gcd(e, Pn) != 1:
                e += 1
        if d < 0:
            d += Pn
        self.n = p * q
        self.e = e
        self.d = d

    def encrypt(self, x: int) -> int:
        return pow(x, self.e, self.n)

    def decrypt(self, y: int) -> int:
        return pow(y, self.d, self.n)


if __name__ == '__main__':
    rsa = RSA(
        1113954325148827987925490175477024844070922844843,
        1917481702524504439375786268230862180696934189293,
        3
    )

    word = 1808808319415691415062465989446183136395423154715795462152356725976667671981921260211627443446049
    v = rsa.encrypt(word)
    print(hex(v))
    print(invmod(2135987035920910082395022704999628797051095341826417406442524165008583957746445088405009430865999, 3))

    h = 0x004972030440233370FFF101000625002062503497544849424F4E2F4D4F4E49515545202020202020202020202020F200
    print(int_to_str(h))
