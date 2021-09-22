#!/usr/bin/python

import sys

def are_in_dico(filename: str, *words: str):
    dico = open(filename).read().split('\n')
    for word in words:
        print("'%s' is %sin the dictionnary." % (word, "" if word in dico else "not "))

filename = sys.argv[1]
words = sys.argv[2:]
are_in_dico(filename, *words)
