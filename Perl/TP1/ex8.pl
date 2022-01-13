#!/usr/bin/perl

use strict;
use warnings;

sub primes { my ($max) = @_;
    my @candidates = (2..$max);
    my @primes = ();
    while (@candidates) {
        my $prime = shift(@candidates);
        push(@primes, $prime);
        @candidates = grep { $_ % $prime != 0 } @candidates;
    }
    return @primes;
}

my @primes = primes(@ARGV);
print("@primes\n");