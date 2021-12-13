#!/usr/bin/python3

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
    """Retrieves m and n values from a data content

    :param content: the data content
    :return: m and n values
    """
    m, n, *_ = content[0].split()
    return m, n


def get_limits(content: List[str]) -> List[float]:
    """Retrieves the limits from a data content

    :param content: the data content
    :return: the limits
    """
    return content[1].split()


def get_products(content: List[str], amount: int) -> List[Product]:
    """Retrieves the products from a data content

    :param content: the data content
    :param amount: the amount of products
    :return: the products
    """
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
    """Builds components from a data file

    :param fname: the data file name
    :return: the components
    """
    try:
        with open(fname, "r") as f:
            content = f.readlines()

            m, n = get_m_n(content)

            limits = get_limits(content)
            if len(limits) != int(m):
                raise Exception("Invalid input: len(limits) != m")

            products = get_products(content, int(n))

            return Components(m, n, limits, products)
    except:
        print("Error reading file")
        sys.exit(1)


def product_to_factor(product: Product) -> str:
    """Implicit product as string

    :param product: the product
    :return: the product as string from gain and name as a factor
    """
    return product.gain + product.name


def lp_from_components(components: Components) -> str:
    """Builds a lp file from components

    :param components: the components
    :return: the lp file content
    """
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
    """Writes the given content to a file

    :param content: the content
    :param fname: the file name to write to
    """
    try:
        with open(fname, "w") as f:
            f.write(content)
    except:
        print("Error writing to file")
        sys.exit(1)


def parse_lp_output(fname: str):
    """Runs lp_solve on the given file and parses the output

    :param fname: the file name to read from
    """
    with os.popen("lp_solve " + fname) as output:
        lines = output.readlines()
        count = 0
        for i in range(4, 4 + len(components.products)):
            line = lines[i].split()
            if float(line[1]) != 0:
                print(" = ".join(line))
                count += 1
        print("|> optimum:", lines[1].split(":")[1].strip())
        print("|>", count, "different product(s)")


if __name__ == "__main__":
    int_flag = False
    args = sys.argv[1:]
    if "-int" in args:
        args.remove("-int")
        int_flag = True
    if len(args) != 2:
        print("Usage: ./generic.py [-int] <input_file> <output_file>")
        sys.exit(1)
    components = get_components_from_file(args[0])
    lp = lp_from_components(components)
    if int_flag:
        lp += "int "
        lp += ", ".join(map(lambda p: p.name, components.products))
        lp += ";\n"
    write_to_file(lp, args[1])
    parse_lp_output(args[1])
