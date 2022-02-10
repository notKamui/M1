package MonModule;

use strict;
use warnings;
use parent qw(Exporter);
our @EXPORT = qw(TableMult1 TableMult2 TableMult3);

sub TableMult1 { 
    my ($n) = @_;
    for (my $i = 1; $i <= $n; $i++) {
        for (my $j = 1; $j <= $n; $j++) {
            printf("%5d", $i * $j);
        }
        print("\n");
    }
}

sub TableMult2 { 
    my ($n) = @_;
    foreach my $i (1..$n) { 
        foreach my $j (1..$n) {
            printf("%5d", $i * $j);
        }
        print("\n");
    }
}

sub TableMult3 { 
    my ($n) = @_;
    my $ret = "";
    foreach my $i (1..$n) { 
        foreach my $j (1..$n) {
            $ret = sprintf("$ret%5d", $i * $j);
        }
        $ret = sprintf("$ret\n");
    }
    return $ret;
}

1;