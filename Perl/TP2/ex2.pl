#!/usr/bin/perl

use strict;
use warnings;

my %uids = ();

open(my $fd, '<', "./passwd") or die("open: $!");
while (defined(my $line = <$fd>)) {
    chomp $line;
    my ($login, $uids) = (split(/:/, $line))[0, 2];
    $uids{$login} = $uids; 
}
close($fd);

# foreach my $login (keys %uids) {
#     printf("%s : %s\n", $login, $uids{$login});
# }

# foreach my $login (sort keys %uids) {
#     printf("%s : %s\n", $login, $uids{$login});
# }

foreach my $login (sort { $uids{$a} <=> $uids{$b} or $a cmp $b } keys %uids) {
    printf("%s : %s\n", $login, $uids{$login});
}