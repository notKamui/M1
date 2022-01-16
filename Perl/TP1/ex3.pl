#!/usr/bin/perl

use strict;
use warnings;

sub SommeTest {
    my ($x, $y, $n) = @_;
    return $x + ($x.$y) == $n;
}

if (SommeTest(@ARGV)) {
    print("OK\n");
} else {
    print("KO\n");
}