#!/usr/bin/perl

use strict;
use warnings;
use lib '.';
use Disc;
use Ring;
use Data::Dumper;

my $disc = Disc->new(3, 5, 6);
print Dumper($disc);
print $disc->area()."\n";
print $disc."\n";

my $ring = Ring->new(3, 5, 6, 2);
print Dumper($ring);
print $ring->area()."\n";
print $ring."\n";