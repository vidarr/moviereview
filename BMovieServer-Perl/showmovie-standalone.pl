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

eval {
  # $ENV{'REQUEST_METHOD'} =~ tr/a-z/A-Z/;
  # if ($ENV{'REQUEST_METHOD'} eq "GET") {
  #   $buffer = $ENV{'QUERY_STRING'};
  # }
  
  # %params = $buffer =~ /([^=]*)=([^?]*)/g;
  # $fileName = $params{'movie'};

  $fileName = "captain_berlin.xml";
  die "Bitte geben Sie einen Film an!\n" if (!defined($fileName));
  $movie = MovieXHTML::getMoviePage($fileName);
};
  
if($@) {
  $movie = $@;
};


print <<ENDOFHEADER;

Content-Type: text/html;charset=utf-8


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<xhtml xmlns="http://www.w3.org/1999/xhtml">
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<head>
<title>B Movie Review</title>
</head>
<body>
ENDOFHEADER

print $movie;

print <<ENDOFTAIL;
</body>
</xhtml>
ENDOFTAIL

# print <<ENDOFTAIL;

# </body>
# </html>
# ENDOFTAIL



# local Variables:
# runcmd: "perl"
# End:
