#!/usr/bin/perl

use strict;
use warnings;

my %days = (
    janvier => 31,
    fevrier => 28,
    mars => 31,
    avril => 30,
    mai => 31,
    juin => 30,
    juillet => 31,
    aout => 31,
    septembre => 30,
    octobre => 31,
    novembre => 30,
    decembre => 31
);

foreach my $month (@ARGV) {
    printf("%s : %s\n", $month, $days
{$month} // "inconnu");
}