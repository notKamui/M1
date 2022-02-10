package Disc;

use strict;
use warnings;
use overload '""' => \&dump;

use Math::Trig;

sub new {
    my ($class, $x, $y, $r) = @_;
    my $self = {
        X => $x // 0,
        Y => $y // 0,
        R => $r // 1
    };
    return bless($self, $class);
}

sub area {
    my ($self) = @_;
    return (pi * $self->{R}) ** 2;
}

sub dump {
    my ($self) = @_;
    return "Disc:".$self->{X}.",".$self->{Y}.",".$self->{R};
}

1;