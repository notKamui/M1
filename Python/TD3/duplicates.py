import os, sys, hashlib, argparse

parser = argparse.ArgumentParser(description='Finds duplicate files.')
parser.add_argument('-e', '--extension', metavar='E', dest='extension', default=None,
                    help='an exention to filter')
parser.add_argument('-d', '--directory', metavar='D', dest='directory', default='.',
					help='a directory to search in (defaults to current dir)')
parser.add_argument('-o', '--output', metavar='O', dest='output', default=None,
					help='a file to store the result of the query')

args = parser.parse_args()
