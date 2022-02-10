package Party;

use strict;
use warnings;
use lib '.';

use Moose;
use Person;
use PartyMember

has capacity => (is => 'ro', isa => 'Int', required => 1);
has friends  => (
    is => 'ro', 
    isa => 'ArrayRef[PartyMember]', 
    default => sub{[]}, 
    auto_deref => 1, 
    traits => ['Array'], 
    handles => {
        attend => 'push', 
        expel => 'pop', 
        nbPeople => 'count'
    }
);

sub party {
    my ($self) = @_;
    foreach ($self->friends) {
        print $_->slurp().$_->scream()."\n";
    }
    print "\n";
}

before attend => sub {
    my ($self, $p) = @_;
    print $p->name;
};

after attend => sub {
    my ($self, $p) = @_;
    if ($self->nbPeople() > $self->capacity) {
        print "...YEET ".$self->expel()->name."\n";
    } else {
        print ", Hey !\n";
    }
};

1;