#!/usr/bin/perl

use strict;
use warnings;

my $nrequests = 0;
my $nerrors = 0;
my $totalvolume = 0;
my %urlAccess = ();
my %ipRequests = ();
my %ipToVolume = ();

open(my $fd, '<', "./access_log") or die("open: $!");
while (defined(my $line = <$fd>)) {
    chomp $line;
    if (my ($ip, $url, $httpcode, $volume) = $line =~ m/^((?:\d+.){3}\d+) - - \[.*?\] "[A-Z]+ (.*?) HTTP\/\d+\.\d+" (\d+) (\d+) ".*?" ".*?"$/) {
        $nrequests++;
        if (int($httpcode) != 200) { $nerrors++; }
        $totalvolume += int($volume);
        $urlAccess{$url}++;
        $ipRequests{$ip}++;
        $ipToVolume{$ip} += int($volume);
    }
}
close($fd);

print("N° of requests : $nrequests\nN° of errors (not 200) : $nerrors\nTotal transfer volume : $totalvolume\n");

print("\nURL : #access\n");
foreach (reverse sort { $urlAccess{$a} <=> $urlAccess{$b} } keys %urlAccess) {
    print("$_ : $urlAccess{$_}\n");
}

print("\nFrequent IP (first 10) : #access #totalVolume\n");
foreach ((reverse sort { $ipRequests{$a} <=> $ipRequests{$b} } keys %ipRequests)[0..9]) {
    print("$_ : $ipRequests{$_} $ipToVolume{$_}\n");
}