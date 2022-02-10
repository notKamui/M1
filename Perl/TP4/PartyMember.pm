package PartyMember;

use strict;
use warnings;

use Moose::Role;

has drink => (is => "ro", isa => "Str", required => 1);

sub slurp {
    my ($self) = @_;
    print "mmm".$self->drink."\n";
}

requires 'scream';

1;