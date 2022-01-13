#!/usr/bin/perl

use strict;
use warnings;

my @t = (4, -5, 7);
push(@t, -2, 3);
printf("%s\n", join(', ',@t));
unshift(@t, 0, -1);
$t[3] = 9;
@t= sort { $a <=> $b }
    grep { $_ > 0 }
    map { $_ * 2 } 
    @t;
print("@t\n");