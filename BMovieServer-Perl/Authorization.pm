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
package Authorization;

use constant {
  TEST_STRING => 'anreS ed araveuG otsenrE \'ehC\'',
    USER_FILE => 'users.sst'
};

=pod

=encoding utf8

=head1 NAME

  Authorization

=head1 SYNOPSIS

    die "Authorization failed" unless checkLogin(\%params)

=head1 DESCRIPTION

  This Package provides for an authorization mechanism relying on user names/passwords

=head2 Functions

  The following functions are provided by default

=cut

my ($userName, $password) = ("login", "authinfo");
my %pw = ();

=head3 loadPWList

  loads user name / password tuples from a file
  if no file name is specified as parameter, it uses the content of PW_FILE as file name

=cut

sub loadPWList {
  open (USERFILE, "<", getUserFile()) or die "Konnte Nutzerdaten nicht laden!";
  while($_ = <USERFILE>) {
    my @line = split ' ', $_;
    $pw{$line[0]} = $line[1];
  };
  close(USERFILE);
  return 1;
};


=head3 checkLogin

  Checks whether given user name and password are valid
  If 1 parameter is given, they are interpreted as a reference to an hash containing $userName and $password as keys
  If 2 parameters are given, the first one should be the user name, the second one the password
  gives undef if not valid, true elsewise

=cut

sub checkLogin {
  my ($hash, $pw) = @_;
  return undef unless $hash;
  unless(defined($pw)) {
    my $name = $hash->{$userName};
    return undef unless $name;
    return checkPassword($name, $hash->{$password});
  };
  return checkPassword($hash, $pw);
}


=head3 getCmdUser

  Gives the name of the post field that contains the user name

=cut

sub getCmdUser{
  return $userName;
};


=head3 getCmdPW

  Gives the name of the post field that contains the password

=cut

sub getCmdPW{
  return $password;
};


=head3 encrypt

  Creates check string to be stored in a file
  This string can be used to verify a password
  It is impossible to reconstruct the actual password from the check string
  The actual password is *NOT* represented in the check string
  1 parameter ist expected: The string a checkstring is to be created from

=cut

sub createCheckString {
  my ($passphrase) = @_;
  die "Wrong number of args" unless $passphrase;
  return crypt($passphrase, TEST_STRING);
};


=head3 checkPassword

  Checks whether a password is correct
  This function expects 2 parameters:
  user the user name whose password should be checked
  pw the password that should be verified

=cut

sub checkPassword {
  my ($user, $pw) = @_;
  die "Wrong number of args: $user $pw" unless $user && $pw;
  my $check = $pw{$user};
  die "User not found" unless $check;
  return createCheckString($pw) eq $check;
};



=head3 getUserFile

  Gives the name of the file the users and check strings are stored

=cut

sub getUserFile {
  return USER_FILE;
};


loadPWList;


=head1 AUTHOR

  Michael J. Beer <michael.josef.beer@googlemail.com>

=cut



# Local Variables:
# mainmodul: "showmovie.pl"
# runcmd: "perl"
# End:
