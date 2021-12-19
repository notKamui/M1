#!/usr/bin/python
"""Sudoku"""

import itertools
import math
import sys
import doctest
import os
import subprocess


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


def write_CNF_to_file(fname, cnf, N):
    """Writes the CNF of a board of size N to a given file
    """
    vars = dict()
    with open(fname, "w+") as f:
        f.write("p cnf %d %d\n" % (N*N*N, len(cnf)))
        for clause in cnf:
            line = ""
            for lit in clause:
                n = literal_to_integer(lit, N)
                if lit[0] == 1:
                    vars[n] = lit
                line += "%d " % n
            f.write("%s0\n" % line)
    return vars


def parse_CNF_from_file(fname):
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


def parse_minisat_output(fname, vars, N):
    """Runs minisat on a given file and parses the output to display it back into a sudoku grid.
    """
    outfile = fname + ".out"
    subprocess.call(
        ["minisat", fname, outfile],
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL
    )

    with open(outfile, "r") as f:
        lines = f.readlines()
        if lines[0] != "SAT\n":
            print("This grid is unsolvable")
            return

        solution = list(filter(lambda x: x > 0, map(
            lambda x: int(x), lines[1].split())))

        grid = []
        for var in solution:
            grid.append(vars[var][3])

        for i in range(N*N):
            print(str(grid[i]).rjust(2, " "), end=" ")
            if (i+1) % N == 0:
                print()


def main():
    doctest.testmod()

    args = sys.argv[1:]
    clean = False
    if "-c" in args:
        clean = True
        args.remove("-c")
    if len(args) != 1:
        print("Usage: python sudoku.py <filename> [-c]")
        return

    cnf, N = parse_CNF_from_file(sys.argv[1])
    cnffile = sys.argv[1] + ".cnf"
    vars = write_CNF_to_file(cnffile, cnf + generate_rules(N), N)
    parse_minisat_output(cnffile, vars, N)

    if clean:
        os.remove(cnffile)
        os.remove(cnffile + ".out")


if __name__ == "__main__":
    main()
