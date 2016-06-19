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
use MovieXHTML;

my $movie;
my $fileName;
my $movieDir = MovieXHTML::getMovieDir;
my $buffer;

eval {
  #$fileName = 'captain_berlin.xml';
  $ENV{'REQUEST_METHOD'} =~ tr/a-z/A-Z/;
  if ($ENV{'REQUEST_METHOD'} eq "GET") {
    $buffer = $ENV{'QUERY_STRING'};
  }
  
  %params = $buffer =~ /([^=&]*)=([^&]*)/g;
  $fileName = $params{'movie'};
  $author = $params{'author'};

  die "Bitte geben Sie einen Film an!\n" if (!defined($fileName));
  $movie = MovieXHTML::getMoviePage($fileName, $movieDir, $author);
};

if($@) {
  $movie = $@;
};

print MovieXHTML::getFramedPage($movie);




# local Variables:
# runcmd: "perl"
# End:
