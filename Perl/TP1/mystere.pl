#!/usr/bin/perl

use strict;
use warnings;

my $nntpmntp = 10;
while ($nntpmntp>5) {
   print "$nntpmntp\n";
   $nntpmntp --;
}

my $x = 'oui';
my $y = 'non';
if ( $x eq $y ) {
   print "c'est dingue!\n";
} else {
   print "tout va bien\n";
}

print '2'.'9'+'5'."\n";
