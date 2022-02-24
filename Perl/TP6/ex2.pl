#!/usr/bin/perl

use strict;
use warnings;

use DBI;

my $source = 'dbi:Pg:host=sqletud.u-pem.fr;dbname=jimmy.teillard_db';
my $dbh = DBI->connect($source, 'jimmy.teillard', 'dbkamui') or die($DBI::errstr);

$dbh->do('CREATE SCHEMA IF NOT EXISTS perlCourse') or die($dbh->errstr());
$dbh->do('CREATE TABLE IF NOT EXISTS perlCourse.annuaire (prenom_nom VARCHAR(40), numero_tel VARCHAR(20))') or die($dbh->errstr());

my $req = $dbh->prepare('INSERT INTO perlCourse.annuaire VALUES (?, ?)') or die($dbh->errstr());

my %pages = (
    'Jimmy Teillard' => '0123456789',
    'Irwin Madet' => '0101010101',
    'Lorris Creantor' => '0606060606',
    'Frederic Xu' => '0909090909'
);

foreach my $k (keys %pages) {
    $req->execute($k, $pages{$k});
}