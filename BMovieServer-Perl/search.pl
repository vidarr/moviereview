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
use XMLFile;
use MovieConfig;

use constant {
  CMD_TITLE => 'title'
};

my $result = '';
my $cmdTitle = CMD_TITLE;

eval {
  $requestMethod = $ENV{'REQUEST_METHOD'};
  if ($requestMethod eq "GET") {
    $buffer = $ENV{'QUERY_STRING'};
  } elsif ($requestMethod eq "POST") {
    read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});
  };


  my %params = $buffer =~ /([^=&]*)=([^&]*)/g;
  my $title = $params{$cmdTitle};

  die "Not yet supported: $buffer \n" . keys(%params) unless defined $title;

  MovieXHTML::loadMovieList();
  my $dir = MovieConfig::MOVIE_DIR;

  my @movies = MovieXHTML::getMovies();
  my @result;
  for $movie (@movies) {
    my ($xml, $cover, $author) = @{MovieXHTML::getMovie($movie)};
    my $xmlFile = XMLFile::spawn($dir . '/' . $xml) or die "Fehler in Datenbasis!";
    my $movieTitle =  $xmlFile->getTag('titel');
    $movieTitle =  $xmlFile->getTag('textfelder')->[0]->getTag('titel') unless defined $movieTitle;
    $movieTitle = $movieTitle->[0]->getContent();
    $movieTitle =~ /$title/i && push(@result, $movie);
  };
  
  $result .= MovieXHTML::getMovieListPage(@result);
};

if($@) {
  $result = $@;
};

print MovieXHTML::getFramedPage($result);

