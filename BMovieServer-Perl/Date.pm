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
package Date;

my @days = ('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun');
my @months = ('Jan', 'Feb', 'Mar', 'Apr', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec');


=pod

=encoding utf8

=head1 NAME

  Date

=head1 SYNOPSIS

    my $time = DATE::getTimeStamp;

=head1 DESCRIPTION

  This Package provides for routines that handle dates and times

=head2 Functions

  The following functions are provided by default

=cut

=head3 getTimeStamp

  Gives stamp of current time & date

=cut

sub getTimeStamp {
  my ($sec,$min,$hour,$tag,$monat,$jahr,$wday,$yday,$isdst) = localtime(time);
  $jahr += 1900;
  $tag++;
  return "$tag.$monat.$jahr $hour:$min:$sec";
};


=head3 getRFC822

  Returns RFC 822 compliant date

=cut

sub getRFC822 {
  my ($sec,$min,$hour,$day,$month,$year,$wday,$yday,$isdst) = localtime(time);
  my $date = $days[--$wday] . ", $day " . $months[$month];
  return join '', ($date, " ", 1900 + $year, " $hour:$min:$sec +0100");
};


