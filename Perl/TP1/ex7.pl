#!/usr/bin/perl

use strict;
use warnings;

sub Intervalle { my ($n, $x) = @_;
    return grep { $_ != $x } (1..$n);
}

sub NonMult { my ($n, $x) = @_;
    return grep { $_ % $x != 0 } (1..$n);
}

my @interval = Intervalle(10, 3);
print("@interval\n");
my @nmult = NonMult(10, 3);
print("@nmult\n");