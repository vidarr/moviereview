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
use Authorization;

use constant {
  CMD_XML => 'eval_file'
};

my $cmdXML= CMD_XML;
my $result = "";
my $tempDir = MovieXHTML::getTempDir;

eval {
  my $buffer = '';
  my $requestMethod = $ENV{'REQUEST_METHOD'};
  $requestMethod =~ tr/a-z/A-Z/;
  if ($requestMethod eq "GET") {
    $buffer = $ENV{'QUERY_STRING'};
  }
  elsif ($requestMethod eq "POST") {
    read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});
  };

  %params = $buffer =~ /([^=&]*)=([^&]*)/g;
  my $login = Authorization::getCmdUser;
  $login = $params{$login};
  $login = '' unless $login;

  die "$login :: registermovie.pl :: Falsches Login :: $buffer" unless Authorization::checkLogin(\%params);

  MovieXHTML::log "$login :: registermovie.pl :: $buffer";

  my $movie = $params{$cmdXML};
  die "Notwendig ist die Angabe von " . CMD_XML  unless $movie;

  $movie = MovieXHTML::getTempDir() . "/$movie";
  $reg = MovieXHTML::getMovieRegistry();
  die "Registry invalid!" unless $reg;
  MovieXHTML::registerMovie(MovieXHTML::getMovieRegistry(), $login, "$movie.xml");
  $result = "$movie wurde erfolgreich registriert";
};

if($@) {
  MovieXHTML::log($@);
  $result = $@;
};

print MovieXHTML::getHTMLPage($result);




# local Variables:
# runcmd: "perl"
# End:
