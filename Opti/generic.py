import sys
import os
from dataclasses import dataclass
from typing import List, Tuple

@dataclass
class Product:
    name: str
    costs: List[str]
    gain: str

@dataclass
class Components:
    m: str
    n: str
    limits: List[str]
    products: List[Product]

def get_m_n(content: List[str]) -> Tuple[str, str]:
    # getting m and n values and casting to int
    m, n, *_ = content[0].split()
    return m, n

def get_limits(content: List[str]) -> List[float]:
    # getting list of size m of the resource limits
    return content[1].split()

def get_products(content: List[str], amount: int) -> List[Product]:
    # reading n lines, starting at index 2, for each product
    products = []
    for i in range(2, amount+2):
        comps = content[i].split()
        name = comps[0]
        costs = comps[1:-1]
        gain = comps[-1]
        products.append(Product(name, costs, gain))
    return products

def get_components_from_file(fname: str) -> Components:
    with open(fname, "r") as f:
        content = f.readlines()

        m, n = get_m_n(content)
        
        limits = get_limits(content)
        if len(limits) != int(m):
            raise Exception("Invalid input: len(limits) != m")

        products = get_products(content, int(n))

        return Components(m, n, limits, products)

def product_to_factor(product: Product) -> str:
    return product.gain + product.name

def lp_from_components(components: Components) -> str:
    # building the objective function
    objective = "max: "
    objective += " + ".join(map(product_to_factor, components.products))
    objective += ";\n"

    # building each restriction
    restrictions = []
    m, n = int(components.m), int(components.n)
    for i in range(m):
        restriction = ""
        for j in range(n):
            p = components.products[j]
            restriction += p.costs[i] + p.name
            if j < n-1:
                restriction += " + "
        restriction += " <= " + components.limits[i] + ";"
        restrictions.append(restriction)

    return objective + '\n'.join(restrictions) + "\n"

def write_to_file(content: str, fname: str):
    with open(fname, "w") as f:
        f.write(content)

if __name__ == "__main__":
    int_flag = False
    args = sys.argv[1:]
    if "-int" in args:
        args.remove("-int")
        int_flag = True
    components = get_components_from_file(args[0])
    lp = lp_from_components(components)
    print(int_flag)
    if int_flag:
        print("AAA")
        lp += "int " + ", ".join(map(lambda p: p.name, components.products)) + ";\n"
    write_to_file(lp, args[1])

    os.popen("lp_solve " + args[1])