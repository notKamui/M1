#!/usr/bin/perl

use strict;
use warnings;

open(my $fd, '<', "./passwd") or die("open: $!");
while (defined(my $line = <$fd>)) {
    chomp $line;

    # print("$line\n") if ($line =~ m/^jc:/);

    # print("$line\n") if ($line !~ m/bash$/);

    # $line =~ s#/home/#/mnt/home/#g;
    # printf("%s\n", $line);

    # $line =~ s/^(.*?):.*?:(.*)$/$1:$2/;
    # printf("%s\n", $line);

    # $line =~ s/^(.*?:)(.*?:)(.*)$/$2$1$3/;
    # printf("%s\n", $line);

    # $line =~ s/^((?:.*?:){2})(.*?:)(.*?:)(.*)$/$1$3$2$4/;
    # printf("%s\n", $line);

    # if (my ($gid) = $line =~ m/^(?:.*?:){3}(?:(.*?):).*$/) { print("$gid\n"); }

    $line =~ s/^((?:.*?:){3})(?:(.*?):)(.*)$/$1.($2*2).":".$3/e;
    printf("%s\n", $line);
}
close($fd);
