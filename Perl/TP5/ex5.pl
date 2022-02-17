#!/usr/bin/perl

use strict;
use warnings;

use MIME::Parser;
use Data::Dumper;
use MIME::Base64;

my $parser = MIME::Parser->new();

my $msg = $parser->parse_open('./courriel');


my $subject = $msg->get('Subject');
$subject =~ s/=\?utf-8\?b\?(.*?)\?=/decode_base64($1)/eig;

my $date = $msg->get('Date');


print $msg->get('From') . "\n" . $subject . "\n" . $date . "\n";