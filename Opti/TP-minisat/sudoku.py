#!/usr/bin/python
"""Sudoku"""

import itertools
import math
import sys
import doctest


def var(i, j, k):
    """Return the literal Xijk.
    """
    return (1, i, j, k)


def neg(l):
    """Return the negation of the literal l.
    """
    (s, i, j, k) = l
    return (-s, i, j, k)


def initial_configuration():
    """Return the initial configuration of the example in td6.pdf

    >>> cnf = initial_configuration()
    >>> [(1, 1, 4, 4)] in cnf
    True
    >>> [(1, 2, 1, 2)] in cnf
    True
    >>> [(1, 2, 3, 1)] in cnf
    False
    """
    return [[var(1, 4, 4)], [var(2, 1, 2)], [var(3, 2, 1)], [var(4, 3, 1)]]


def at_least_one(L):
    """Return a cnf that represents the constraint: at least one of the
    literals in the list L is true.

    >>> lst = [var(1, 1, 1), var(2, 2, 2), var(3, 3, 3)]
    >>> cnf = at_least_one(lst)
    >>> len(cnf)
    1
    >>> clause = cnf[0]
    >>> len(clause)
    3
    >>> clause.sort()
    >>> clause == [var(1, 1, 1), var(2, 2, 2), var(3, 3, 3)]
    True
    """
    return [L]


def at_most_one(L):
    """Return a cnf that represents the constraint: at most one of the
    literals in the list L is true

    >>> lst = [var(1, 1, 1), var(2, 2, 2), var(3, 3, 3)]
    >>> cnf = at_most_one(lst)
    >>> len(cnf)
    3
    >>> cnf[0].sort()
    >>> cnf[1].sort()
    >>> cnf[2].sort()
    >>> cnf.sort()
    >>> cnf == [[neg(var(1,1,1)), neg(var(2,2,2))], \
    [neg(var(1,1,1)), neg(var(3,3,3))], \
    [neg(var(2,2,2)), neg(var(3,3,3))]]
    True
    """
    ret = []
    for combo in itertools.combinations(L, 2):
        clause = []
        for lit in combo:
            clause.append(neg(lit))
        ret.append(clause)
    return ret


def assignment_rules(N):
    """Return a list of clauses describing the rules for the assignment (i,j) -> k.
    """
    cnf = []
    for i in range(1, N+1):
        for j in range(1, N+1):
            node = [var(i, j, k) for k in range(1, N+1)]
            cnf += at_least_one(node)
            cnf += at_most_one(node)
    return cnf


def row_rules(N):
    """Return a list of clauses describing the rules for the rows.
    """
    cnf = []
    for i in range(1, N+1):
        for k in range(1, N+1):
            line = [var(i, j, k) for j in range(1, N+1)]
            cnf += at_least_one(line)
            cnf += at_most_one(line)
    return cnf


def column_rules(N):
    """Return a list of clauses describing the rules for the columns.
    """
    cnf = []
    for j in range(1, N+1):
        for k in range(1, N+1):
            col = [var(i, j, k) for i in range(1, N+1)]
            cnf += at_least_one(col)
            cnf += at_most_one(col)
    return cnf


def subgrid_rules(N):
    """Return a list of clauses describing the rules for the subgrids.
    """
    cnf = []
    root = int(math.sqrt(N))
    for k in range(1, N+1):
        for subI in range(root):
            for subJ in range(root):
                sub = []
                for i in range(root*subI+1, root*subI+root+1):
                    for j in range(root*subJ+1, root*subJ+root+1):
                        sub.append(var(i, j, k))
                cnf += at_least_one(sub)
                cnf += at_most_one(sub)
    return cnf


def generate_rules(N):
    """Return a list of clauses describing the rules of the game.
    """
    cnf = []
    cnf.extend(assignment_rules(N))
    cnf.extend(row_rules(N))
    cnf.extend(column_rules(N))
    cnf.extend(subgrid_rules(N))
    return cnf


def literal_to_integer(l, N):
    """Return the external representation of the literal l.

    >>> literal_to_integer(var(1,2,3), 4)
    7
    >>> literal_to_integer(neg(var(3,2,1)), 4)
    -37
    """
    (s, i, j, k) = l
    return s*(N*N*(i - 1) + N*(j - 1) + k)


def writeCNFToFile(fname, cnf, N):
    """Writes the CNF of a board of size N to a given file
    """
    with open(fname, "w+") as f:
        f.write("p cnf %d %d\n" % (N**3, len(cnf)))
        for clause in cnf:
            line = ' '.join(map(lambda term: str(
                literal_to_integer(term, N)), clause))
            f.write("%s 0\n" % line)


def parseCNFFromFile(fname):
    """Reads a CNF from a file and returns a list of clauses and the dimension of the grid.
    """
    with open(fname, "r") as f:
        lines = f.readlines()
        N = int(lines[0])
        grid = list(map(lambda line: list(
            map(lambda term: int(term), line.split())
        ), lines[1:]))
        cnf = [[var(i+1, j+1, grid[i][j])]
               for i in range(N)
               for j in range(N)
               if grid[i][j] != 0]
    return cnf, N


def main():
    doctest.testmod()

    cnf, N = parseCNFFromFile(sys.argv[1])
    writeCNFToFile(sys.argv[1] + ".cnf", cnf + generate_rules(N), N)


if __name__ == "__main__":
    main()
