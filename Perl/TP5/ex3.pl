#!/usr/bin/perl

use strict;
use warnings;

use IO::Socket;
use threads;
use threads::shared;

my $count :shared = 1;

my $listen_s = IO::Socket::INET->new(
    Proto => 'tcp',
    LocalPort => 2000,
    Listen => 5,
    Reuse => 1
) or die("$@");

while (my $accept_s = $listen_s->accept()) {
    print "New client\n";
    threads->new(sub {
        $accept_s->send($count++."\n");
        sleep(5);
        $accept_s->send($count++."\n");
        close($accept_s);
    })->detach();
}

close($listen_s);