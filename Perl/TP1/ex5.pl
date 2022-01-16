#!/usr/bin/perl

use strict;
use warnings;

sub Fact { my ($n) = @_;
    if ($n == 0) {
        return 1;
    } else {
        return $n * Fact($n - 1);
    }
}

foreach (1..10) {
    printf("%d\n", Fact($_));
}

sub Fibo { my ($n) = @_;
    if ($n == 0) { return (0); }
    my @ret = (0, 1);
    foreach (2..$n) {
        push(@ret, $ret[-1] + $ret[-2]);
    }
    return @ret;
}

my @fibo10 = Fibo(10);
print("@fibo10\n");
printf("%d\n", $fibo10[-1]);