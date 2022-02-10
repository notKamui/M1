package Person;

use strict;
use warnings;
use lib '.';

use Moose;
with 'PartyMember';

has name => (is => 'ro', isa => 'Str');

sub scream {
    my ($self) = @_;
    print $self->name." AAAAAAA\n";
}

1;