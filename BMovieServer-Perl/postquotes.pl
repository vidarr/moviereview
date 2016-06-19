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
use CGI;

use constant {
  QUOTES_FILE => 'quotes.sst',
  CMD_MOVIE => 'movie_name_2',
  CMD_MOVIEXML => 'eval_file_2',
  CMD_QUOTES => 'quotes'
};

my ($cmdMovie, $cmdMovieXML, $cmdQuotes) = (CMD_MOVIE, CMD_MOVIEXML, CMD_QUOTES);
my $result = "";

eval {
  my $buffer = "";
  my $requestMethod = $ENV{'REQUEST_METHOD'};
  #$buffer = 'movie_name_2=leprechaun&eval_file_2=leprechaun_4.xml&quotes=dib&';
  $requestMethod =~ tr/a-z/A-Z/;
  if ($requestMethod eq "GET") {
    $buffer = $ENV{'QUERY_STRING'};  }
  elsif ($requestMethod eq "POST") {
    read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});
  };

  %params = $buffer =~ /([^=&]*)=([^&]*)/g;
  my $login = Authorization::getCmdUser;
  $login = $params{$login};
  $login = '' unless $login;

  die "$login :: postquotes.pl :: Falsches Login" unless Authorization::checkLogin(\%params);

  my ($movie, $movieXML, $quotes) = ($params{$cmdMovie}, $params{$cmdMovieXML}, $params{$cmdQuotes});
  die "$login :: postquotes.pl :: Sowohl '" . CMD_MOVIE . "' als auch '" . CMD_MOVIEXML . "' als auch '" . CMD_QUOTES . "' sind als Parameter erforderlich!"
    unless $movie and $movieXML and $quotes;

  my $query = new CGI;
  $movie = $query->unescape($movie);
  $movieXML = $query->unescape($movieXML);
  $quotes = $query->unescape($quotes);
  $movieXML .= '.xml';

  my @quotes = split "\n", $quotes;
  
  open(QUOTES_OUT, ">>", QUOTES_FILE) or die "Konnte Zitatedatenbank nicht kontaktieren!";
  my @results = ();
  for my $q (@quotes) {
    print QUOTES_OUT "'$q' '$movie' $movieXML\n";
    push @results, "$q wurde hinzugefuegt";
  };

  close QUOTES_OUT;
  $result = join "<br/>", @results;
};

if($@) {
  MovieXHTML::log($@);
  $result .= $@;
};

print MovieXHTML::getHTMLPage($result);




# local Variables:
# runcmd: "perl"
# End:
