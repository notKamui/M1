#!/usr/bin/perl

use strict;
use warnings;

sub TableMult1 { my ($n) = @_;
    for (my $i = 1; $i <= $n; $i++) {
        for (my $j = 1; $j <= $n; $j++) {
            printf("%5d", $i * $j);
        }
        print("\n");
    }
}

sub TableMult2 { my ($n) = @_;
    foreach my $i (1..$n) { 
        foreach my $j (1..$n) {
            printf("%5d", $i * $j);
        }
        print("\n");
    }
}

sub TableMult3 { my ($n) = @_;
    my $ret = "";
    foreach my $i (1..$n) { 
        foreach my $j (1..$n) {
            $ret = sprintf("$ret%5d", $i * $j);
        }
        $ret = sprintf("$ret\n");
    }
    return $ret;
}

my $n = $ARGV[0] // 10;

TableMult1($n);
print("\n");
TableMult2($n);
print("\n");
print(TableMult3($n));