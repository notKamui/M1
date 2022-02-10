package Ring;

use strict;
use warnings;
use overload '""' => \&dump;

use Math::Trig;

use parent qw(Disc);

sub new {
    my ($class, $x, $y, $z, $ri) = @_;
    my $self = $class->SUPER::new($x, $y, $z);
    $self->{RI} = $ri // 0;
    return bless($self, $class);
}

sub area {
    my ($self) = @_;
    return $self->SUPER::area() - ((pi * $self->{RI}) ** 2);
}

sub dump {
    my ($self) = @_;
    return $self->SUPER::dump().",".$self->{RI};
}

1;