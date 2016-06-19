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
use GUDB;
use MovieXHTML;
use MovieConfig;
use Data::Dumper;


=pod

=head1 DESCRIPTION

  This is a frontend for the GUDB 

=head1 SYNOPSIS

  searchgudb.pl?title=leprechaun&fsk=12&fsk2=18

=head1 USAGE

  Use this within CGI

  you can search the database provided within file ConfigMovie::MOVIE_LIST_FILE by the criteria defined in GUDB::searchCategories
  For searching criterium 'crit' for the value 'val', give crit=val as argument to the script

  For each criterium 'crit', you can supply a parameter 'crit2'. If you do so, for example:
  searchgudb.p?fsk=6&fsk2=18
  the range between value of 'crit' and value of 'crit2' will be searched for thus returning in this example all movies having a fsk between 6 and 18
  If 'crit' is a string, you can set the search mode via 'crit2':
  if 'crit2' eq "standard" the criterium will be searched cas insensitively for substring 'crit'
                "caseSensitive" the criterium will be searched case sensitive
  You can combine several different criteria:
  searchgudb.pl?title=lep&fsk=18
  will search for all movies containing "leprechaum" in their title and having a fsk of exactly 18.

  All criteria you do not set explicitely wont be searched for.

=cut

eval {
  $ENV{'REQUEST_METHOD'} =~ tr/a-z/A-Z/;
  if ($ENV{'REQUEST_METHOD'} eq "GET") {
    $buffer = $ENV{'QUERY_STRING'};
  }
  
  %params = $buffer =~ /([^=&2]+)=([^&]+)/g;
  %paramsSec = $buffer =~  /([^=&]+)2=([^&]+)/g;
  MovieXHTML::log("searchgudb.pl :: $buffer");

  my $gudb = GUDB::spawn;
  $gudb->connectToSource('file', MovieConfig::MOVIE_LIST_FILE);

  # remove all entries containing '*'
  for my $key (keys %params) {
    delete $params{$key} if $params{$key} eq '*';
  };
  for my $key (keys %paramsSec) {
    delete $paramsSec{$key} if $paramsSec{$key} eq '*';
  };

  my @keys = (keys(%params)) or die "No parameters given";


  my @result = @{$gudb->getDB()};

  for my $cat (@keys) {
    last unless (@result);
    my $gudb1 = GUDB::spawn(@result);
    @result = $gudb1->search($cat, $params{$cat}, $paramsSec{$cat});
  };

  $result = MovieXHTML::getMovieListPage(@result);
};

if($@) {
  $result = $@;
};

print MovieXHTML::getFramedPage($result);
