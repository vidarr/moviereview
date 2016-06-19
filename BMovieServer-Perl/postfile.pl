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
use strict;
use File::Basename;

use constant {
  CMD_FILENAME => 'uploadedfile'
};

$CGI::POST_MAX = 150000;


my $safeFilenameCharacters = "a-zA-Z0-9_.-";
my $destDir = MovieXHTML::getTempDir;

my $result = "";

eval {
  my $buffer = "";

  MovieXHTML::log "postfile.pl: ";

  my $query = new CGI;
  my $filename = $query->param(CMD_FILENAME);
  my ($user, $pw) = ($query->param(Authorization::getCmdUser()), $query->param(Authorization::getCmdPW()));
  $user = '' unless $user;
  die "$user :: postfile.pl :: Problem beim Dateipost" unless $filename && $user && $pw;
  die "$user :: postfile.pl :: Autorisierung fehlgeschlagen" unless Authorization::checkLogin($user, $pw);

  my ( $name, $path, $extension ) = fileparse ( $filename, '\..*' );
  $filename = $name . $extension;
  $filename =~ tr/ /_/;
  $filename =~ s/[^$safeFilenameCharacters]//g;
  my $filehandle = $query->upload(CMD_FILENAME);
  open ( UPLOADFILE, ">$destDir/$filename" ) or die "$!";
  binmode UPLOADFILE;

  while ( <$filehandle> ) {
    print UPLOADFILE;
  };

  close UPLOADFILE;

  MovieXHTML::log "$user :: postfile.pl:: $filename gepostet";
  $result .= "<br/> $filename erfolgreich hochgeladen";
};

if($@) {
  MovieXHTML::log($@);
  $result .= $@;
};

print MovieXHTML::getHTMLPage($result);




# local Variables:
# runcmd: "perl"
# End:
