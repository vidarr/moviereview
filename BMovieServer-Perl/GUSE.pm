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
package GUSE;

=pod

=encoding utf8

=head1 Name

Grand Unified Search Engine

=head1 Synopsis

=head1 Description

This is the 'Grand Unified Search Engine'

It is part of the Perl implementation of the 
(B) Movie Server being part of the
(B) Movie Review System

It provides for searching the movie data base for various criteria

It allows searching the very movie data base being constituted of xml files
As well as the creation and use of index files for faster results

For these aims, construct a GUSE object, connect it to whatever data source you want and, well, search it

To create a new index file for a directory, 
1. create a GUSE object by 
   $mySearchObj = spawn GUSE;
2. connect it to the dir using 'connectToSource' - this creates the internal index for that dir
   $mySearchObj->connectToSource('dir', 'path to xml dir');
3. write the content of this{'index'} to the desired file
   open MYFILE, 'myIndexFile' and print MYFILE $mySearchOb->getIndexString() and close MYFILE;

=head2 Functions

The following functions are provided

=head3 spawn

Spawns a new GUSE object

=cut


# Internals:
# The following entries are kept in a GUSE hash:
#
# dataBase The location of the data base, i.e. the dir containing the xml files
# dataIndex The location of the index file
# index    internal kept index that is dynamically created and actually searched
# searchMode if 0, search case insensitive

sub spawn {
  return bless @_;
};


=head3 search

searches for movies satisfying special criteria
to do so, hand over a hash that contains the following fields:

'title'  A String being part of the desired movie title

Several pairs
'min[cat]' Minimum for the value of cat
'max[cat]' maximum for the value of cat
where cat is one of the evaluation categories

'land' being the land
'genre' being the genre

If one or more of these fields are not currently set, every value is taken to be possible

=cut


# Internal:
# search is able to search an index only
# Thus, if connected to a dir, an internal index has to be created
# For this goal, the connectToSource sub should make use of the sub createIndex if it connects to a dir

sub search {

  

};


=head3 connectToSource

Connects to a data source
two parameters are expected:

1. kind of source, currently 'file' or 'dir' is supported
2. location of the source, currently qualified name of the index file if kind = 'file' or qualified name of the xml dir

=cut

sub connectToSource {
  my ($kind, $path) = @_;
  do {
    $kind eq 'dir' && do {
      # create internal db
      opendir(MOVIEDIR, $path) or die "Bitte Verzeichnis angeben!";
      my @files = readdir MOVIEDIR;
      close MOVIEDIR;
      my @result;

      for my $file (@files) {
	next unless $file =~ /([^(.xml)]).xml/;
	insertToDB $file;
      };
      next;
    };
    $kind eq 'file' && do {
      die "Not yet supported";
    };
    die "Unknown kind of data base: $kind";
  };
};


=head3 createIndex

Expects the GUSE to be connected to a source of kind dir

This sub creates an index for the current xml dir
The index is NOT written to a file but returned as string

=cut

sub createIndex {
  return '';
};


=head3 insertToIndex

Inserts a new entry into the index
expects one argument being a qualified name of a movie xml file

This sub does not affect the index file stored on disk.
If you want to alter this one, overwrite it by writing the content of this{'index'} to this{'dataIndex'}

=cut

sub insertToDB {
  

};


=head3 createIndexString

Creates a string representation of the internal index 
This could be written to a file in order to create an index file

=cut

sub createIndexString {
  return '';
};



return 1;


=head1 AUTHOR

  Michael J. Beer <michael.josef.beer@googlemail.com>

=cut

