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
package GUDB;

use XMLFile;
use MovieConfig;
use MovieXHTML;
use Data::Dumper;


=pod

=encoding utf8

=head1 Name

Grand Unified Data Base

=head1 Synopsis

=head1 Description

This is the 'Grand Unified Data Base'

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


=head2 Data

  These are the possible categories that can be searched

=cut

our @searchCategories = ('xml', 'jpg', 'author', 'title', 'land', 'genre', 'fsk', 'year', @MovieConfig::pointCategories);
our %searchCategories = ();

=head2 Functions

The following functions are provided

=head3 spawn

Spawns a new GUSE object

=cut


# Internals:
# The following entries are kept in a GUSE hash:
# db       Database kept internally that is dynamically created and actually searched

sub spawn {
  my $this = {'db' => []};
  if(@_) {
    $this{'db'} = [@_];
  };

  return bless $this;
};


=head3 search

  searches for movies satisfying one special criterium
  first argument has to be one of the searchCriteria out of the searchCriteria array
  There are up to two more params depending on the data type of the criterium:
  Number:
    second one is the value min
    third one is the max value
  String:
    second one is the search string 
    third one is an array describing the search mode. It contains one or more strings:
      'standard', i.e. case insensitive, looking for entries containing the search string
      'caseSensitive'
      'exactMatch'

=cut


sub search {

  my $this = shift or die "Internal Error";
  my $cat = shift or die "Give category";
  my $index = $searchCategories{$cat};
  die "Invalid search category: $cat" unless $index;
  my @result;

  if($index < 6) { # is a string category
    my ($str, $modes) = @_;
    # mode not yet supported
    for my $entry (@{$this{'db'}}) {
      $entry->[$index] =~ /$str/i and push @result, $entry;
    };
  } else { #integer category
    my ($min, $max) = @_;
#    return ('search: ', $cat, " $min $max");
    if($cat eq 'fsk') {
      $min = encodeFSK($min);
      $max = encodeFSK($max) if $max;
    };
    for my $entry (@{$this{'db'}}) {
      my $val = $entry->[$index];
      if($cat eq 'fsk') {
	$val = encodeFSK($val);
      };
      if($max) {
	if($val >= $min and $val <= $max){
	  push @result, $entry;
	};
      } else {
	  if($val == $min) {
	    push @result, $entry;
	  };
	};
    };
  };
  return @result;
};


=head3 connectToSource

Connects to a data source
two parameters are expected:

1. kind of source, currently 'file' or 'dir' is supported
2. location of the source, currently qualified name of the index file if kind = 'file' or qualified name of the xml dir

=cut

sub connectToSource {
  my ($this, $kind, $path) = @_;
  {
    $kind eq 'dir' && do {
      # create internal db
      opendir(MOVIEDIR, $path) or die "Bitte Verzeichnis angeben!";
      my @files = readdir MOVIEDIR;
      close MOVIEDIR;

      for my $file (@files) {
	next unless $file =~ /([^(.xml)]).xml/;
	$this->insertToDB($path, $file);
      };
      last;
    };
    $kind eq 'file' && do {
      die "Data Base $path not found" unless open DBFILE, $path;
      while(my $entry = <DBFILE>) {
	my @entry = $entry =~ /'([^']*)'*\s*/g;
	push @{$this{'db'}}, [@entry];
      };
      close DBFILE;
      last;
    };
    die "Unknown kind of data base: $kind";
  };
};



=head3 insertToDB

Inserts a new entry into the internal db
expects one argument being a qualified name of a movie xml file
OR an object of type XMLFile and its name

You can give an author as a optional third argument

This sub does not affect the db file stored on disk.
If you want to alter this one, overwrite it by writing the content of this{'db'} to the data base file

=cut

sub insertToDB {
  my $this = shift;
  my $dir = shift;
  my $movieXML = shift;
  my $author = shift || 'unknown';
  my $xml;

  # make sue the entry will be recognized correctly
  $dir = $dir =~ tr/\'//;
  $author = $author =~ tr/\'//;
  $movieXML = $movieXML =~ tr /\'//;

 if(ref($dir) eq 'XMLFile') {
    $xml = $dir;
  } else {
    $xml = XMLFile::spawn($dir . '/' . $movieXML);
  };
  my @entry;
  my $title;
 
  push @entry, $movieXML;
  push @entry, $xml->getTag('cover')->[0]->getContent() =~ tr /\'//;
  my $details;
  if($title = $xml->getTag('titel')) {
    $title = $title->[0]->getContent() =~ tr /\'//;
    $details = $xml->getTag('details')->[0];
  } else {
    $details = $xml->getTag('textfelder')->[0];
    $title = $details->getTag('titel')->[0]->getContent() =~ tr /\'//;
  };
  push @entry, $author;
  push @entry, $title;
  push @entry, $details->getTag('land')->[0]->getContent() =~ tr /\'//;
  push @entry, $details->getTag('genre')->[0]->getContent() =~ tr /\'//;
  push @entry, $details->getTag('fsk')->[0]->getContent() =~ tr /\'//;
  push @entry, $details->getTag('jahr')->[0]->getContent() =~ tr /\'//;
  my $scores = $xml->getTag('punktwertungen')->[0];
  for $score (@MovieConfig::pointCategories) {
    push @entry, $scores->getTag($score)->[0]->getAttribute('val') =~ tr /\'//;
  };
  push @{$this{'db'}}, \@entry;
};


=head3 createDBString

Creates a string representation of the internal db
This could be written to a file in order to create an db file

=cut

sub createDBString {
  my @content;
  my $this = shift(@_);

  for my $entry (@{$this{'db'}}) {
    push @content, ("'", join('\' \'',@$entry), "'\n");
  };
  return join '', @content;
};


=head3 store

  Stores db into a file
  Exects one argument: the fully qualified file name

=cut
sub store {
  my $this = shift or die "Internal error in GUDB";
  my $file = shift or die "give file to store db!";
  open(DBFILE, ">", $file) or die "Could not open db $file";
  print DBFILE $this->createDBString;
  close DBFILE;
  MovieXHTML::log('GUDB::store(): ' . "$file  stored \n");
};


=head3 getDB

  Returns the internal data base

=cut
sub getDB {
  $this = shift or die "Internal error!";
  return $this{'db'};
};


# fill lookup table 
for (my $i = 0; $i < @searchCategories; $i++) {
  $searchCategories{$searchCategories[$i]} = $i;
};


return 1;


=head1 AUTHOR

  Michael J. Beer <michael.josef.beer@googlemail.com>

=cut


sub encodeFSK($) {
 $_ = shift @_;
 die "Uninitialized value" unless $_;
 $_ eq '0' and return 0;
 $_ eq '6' and return 1;
 $_ eq '12' and return 2;
 $_ eq '16' and return 3;
 ($_ eq '18' || $_ eq 'Keine Freigabe') and return 4;
 $_ eq 'Indiziert' and return 5;
 $_ eq 'Beschlagnahmt' and return 6;
 return 10;
};


sub decodeFSK($) {
 $_ = shift @_;
 die "Uninitialized value" unless $_;
 $_ eq '0' and return '0';
 $_ eq '1' and return '6';
 $_ eq '2' and return '12';
 $_ eq '3' and return '16';
 $_ eq '4' and return 'Keine Freigabe';
 $_ eq '5' and return 'Indiziert';
 $_ eq '6' and return 'Beschlagnahmt';
 return 'unbekannt';
};



