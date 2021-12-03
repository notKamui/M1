#!/usr/bin/python3

from typing import List


def find_optimized_cuts(max: int, sizes: List[int]) -> List[List[int]]:
    """
    Finds the optimal cuts for a given max size and list of sizes.
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


a = find_optimized_cuts(500, [200, 120, 100, 50])
print(len(a))
