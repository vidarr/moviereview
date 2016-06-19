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

=pod

=encoding utf8

=head1 NAME

  makedb.pl - creates the search db out of an xml db

=head1 SYNOPSIS

 makedb.pl movies

=head1 DESCRIPTION

  Usage:

    makedb.pl [movies-directory] {DBFile}


  Creates a search data base out of an xml data base
  movies-directory is the directory the xml files are located in, if omitted, the current dir is assumed
  DBFile: Giving the fully qualified name to the db file is optional

  The result is written to stdout if no db file is given

  Beware: There is no information about the author, so if you create a db using the xmls only, no information
  about who created them is available and thus none will be stored within db!
=cut


my $gudb = spawn GUDB;
my $dir;
unless ($dir = shift) {$dir = '.'};
  
$gudb->connectToSource('dir', $dir);

if(my $dbfile = shift) {
  $gudb->store($dbfile);
} else {
  print $gudb->createDBString();
};
