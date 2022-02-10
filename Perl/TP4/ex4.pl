#!/usr/bin/perl

use strict;
use warnings;
use lib '.';

use Party;
use Person;

my $party = Party->new(capacity => 2);
my $p1 = Person->new(name => "jim", drink => "tea");
my $p2 = Person->new(name => "lorris", drink => "mocha");
my $p3 = Person->new(name => "irwin", drink => "grayons");

$party->attend($p1);
$party->attend($p3);
$party->attend($p2);

$party->party();