#!/usr/bin/perl

use strict;
use warnings;

use MIME::Lite;

my $msg = MIME::Lite->new(
    From => 'jimmy.teillard@edu.univ-eiffel.fr',
    To => 'teillard.jimmy@outlook.fr',
    Subject => 'test',
    Data => 'message'
);
$msg->attach(
    Type => 'application/pdf',
    Path => './sample.pdf'
);
$msg->send();