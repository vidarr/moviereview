#!/usr/bin/perl -w
# (C) 2010 Michael J. Beer
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
use Authorization;
use MovieConfig;

=pod

=encoding utf8

=head1 NAME

  adduser.pl

=head1 SYNOPSIS

    ./adduser.pl myself verykeenpassword

=head1 DESCRIPTION

  This script creates a new user - password entry in the user file specified within the Authorization module
  myself being the user name
  verykeenpassword being the password

=cut


do {
  print "Usage: adduser user password\n";
  print @ARGV;
  print "\n$#ARGV\n";
  exit;
} unless $#ARGV >0;

my ($user, $pw) = @ARGV;

my $file = Authorization::getUserFile;
$pw = Authorization::createCheckString($pw);

open(USERFILE, ">>", $file) or die "$file could not be opened!";
print USERFILE "$user $pw\n";
close(USERFILE);

my $userxhtml = MovieConfig::USERS_DIR;
$userxhtml .= "/$user.xhtml";
open(USERFILE, ">", $userxhtml) or die "Zugriff auf $userxhtml nicht moeglich!";
print USERFILE "This user has not yet added a description of himself!";
close USERFILE;

print "$user has been added.\n";


=head1 AUTHOR

  Michael J. Beer <michael.josef.beer@googlemail.com>

=cut



# Local Variables:
# runcmd: "perl"
# End:
