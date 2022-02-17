#!/usr/bin/perl

use strict;
use warnings;

use POSIX qw/strftime/;

print strftime('%A', 0, 0, 0, 15, 7, 1999 - 1900)."\n";