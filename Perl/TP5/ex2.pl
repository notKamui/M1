#!/usr/bin/perl

use strict;
use warnings;

use POSIX qw/strftime/;

my ($uid, $date) = (stat($ENV{HOME}))[4,9];
print strftime('%Y/%m/%d %H:%M:%S', localtime($date))."\n";

my $login = (getpwuid($uid))[0];
print $login."\n";