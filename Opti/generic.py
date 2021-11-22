import sys
from dataclasses import dataclass
from typing import List

@dataclass
class Product:
    name: str
    costs: List[float]
    gain: float

@dataclass
class Components:
    m: int
    n: int
    limits: List[float]
    products: List[Product]

def to_float_lst(lst: List[str]) -> List[float]:
    return list(map(lambda l: float(l), lst))

def get_m_n(content: List[str]) -> (int, int):
    # getting m and n values and casting to int
    m, n, *_ = content[0].split()
    return int(m), int(n)

def get_limits(content: List[str]) -> List[float]:
    # getting list of size m of the resource limits
    limits = content[1].split()
    return to_float_lst(limits)

def get_products(content: List[str], amount: int) -> List[Product]:
    # reading n lines, starting at index 2, for each product
    products = []
    for i in range(2, amount+2):
        comps = content[i].split()
        name = comps[0]
        costs = comps[1:-1]
        costs = to_float_lst(costs)
        gain = float(comps[-1])
        products.append(Product(name, costs, gain))
    return products

def get_components_from_file(fname: str) -> Components:
    with open(fname) as f:
        content = f.readlines()

        m, n = get_m_n(content)
        
        limits = get_limits(content)
        if len(limits) != m:
            raise Exception("Invalid input: len(limits) != m")

        products = get_products(content, n)

        return Components(m, n, limits, products)


components = get_components_from_file(sys.argv[1])
print(components)