#!/usr/bin/python3

import sys
import os
from typing import List


def find_optimized_cuts(max: int, sizes: List[int]) -> List[List[int]]:
    """Finds the optimal cuts for a given max size and list of sizes.

    :param max: The maximum size of the steel bar.
    :param sizes: The list of sizes to cut.
    :return: The optimal cuts.
    """
    section = sizes[0]
    maxcut = max // section
    if len(sizes) == 1:
        return [[maxcut]]
    result = []
    for i in range(maxcut + 1):
        delta = section * i
        values = find_optimized_cuts(max - delta, sizes[1:])
        for e in values:
            e.insert(0, i)
            result.append(e)
    return result


def write_output(fname: str, amounts: List[int], cuts: List[List[int]]):
    """Writes the output to a file.

    :param fname: The name of the file to write to.
    :param amount: The list of amount of cuts to make for each size.
    :param cuts: The list of possible optimal cuts.
    """
    with open(fname, 'w') as f:
        f.write("min: ")
        f.write(" + ".join(["x" + str(i) for i in range(len(cuts))]))
        f.write(";\n")

        for j in range(len(amounts)):
            f.write(" + ".join([str(cuts[i][j]) + " x" + str(i)
                    for i in range(len(cuts))]))
            f.write(" >= " + str(amounts[j]) + ";\n")

        f.write("int ")
        f.write(" ".join(["x" + str(i) for i in range(len(cuts))]))
        f.write(";\n")


def parse_lp_output(fname: str, max: int):
    """Runs lp_solve on the given file and parses the output

    :param fname: the file name to read from
    :param max: the maximum size of the steel bar
    """
    with os.popen("lp_solve " + fname) as output:
        lines = output.readlines()
        print("Amount of bars of size", max, ":",
              int(float(lines[1].split(": ")[1][:-2])))


if __name__ == "__main__":
    args = sys.argv[1:]
    if len(args) != 4:
        print("Usage: ./steelcut.py <max> <sizes,...> <amounts,...> <output_file>")
        exit(1)

    max = int(args[0])
    sizes = [int(x) for x in args[1].split(",")]
    amounts = [int(x) for x in args[2].split(",")]
    fname = args[3]

    cuts = find_optimized_cuts(max, sizes)
    write_output(fname, amounts, cuts)
    parse_lp_output(fname, max)
