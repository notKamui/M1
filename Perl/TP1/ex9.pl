#!/usr/bin/perl

use strict;
use warnings;

sub Modif { my ($text, $old, $new) = @_;
    for (
        my $next = index($text, $old); 
        $next != -1; 
        $next = index($text, $old, $next + length($new))
    ) {
        substr($text, $next, length($old), $new);
    }
    return $text;
}

print(Modif("bonjour vous, bonjour", "bonjour", "allo")."\n");
print(Modif("bonjour vous, bonjour", "bonjour", "bonjour bonjour")."\n");