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
package XMLFile;

use XMLTag;
use Data::Dumper;

our @VERSION = 1.00;

# Eine XML-Datei ist auch ein XMLTag
our @ISA = ("XMLTag");


=pod

=encoding utf8

=head1 NAME

  XMLFile - Representation of a XML file

=head1 SYNOPSIS

    use XMLFile;

=head1 DESCRIPTION

  A XML File is basically atag where the name of the file namespace is the name of the tag
  This Class provides functionality to build up a tag out of a XML file and writing a tag 
  as a XML file

=head2 Functions

  The following functions are exported by default

=head3 spawn

  Creates new XML file object
  Needs a file name to be read from

=cut

sub spawn {
  my ($file) = @_;
  
  my $newXMLFile = bless {'fileName' => $file};
  my $data = $newXMLFile->read();
  return bless {%$newXMLFile, %$data};
}

=head3 read

  Reads in a XML file and fills up the current object with the data read

=cut

sub read {
  my ($this) = @_;
  my $file;
  ($file = $$this{'fileName'}) or die "XMLFile::read(): Falscher Parameter $this !";

  open(INFILE, $file) or die "Datei $file nicht gefunden/ kann nicht geöffnet werden!";

  my @data = ();

  while (<INFILE>) {
    push @data, $_;
  }

  close INFILE;
  
  return readTag(\@data);
}


=head3 readTag

  Creates a XMLTag out of XML data
  This function expects 
  1. the name of the tag, if undef, 
     the first opening tag is not taken to be a nested tag but the tag to 
     be scanned
  2. the data as array containing single lines of xml data

  Returns undef in case of error

=cut

sub readTag {
  my ($data, $name, $attribs) = @_;
  die "XMLFile::readTag(): $data ist keine ARRAY-Referenz!" unless (ref($data) eq 'ARRAY');
  my %attribs;
  %attribs = %$attribs if defined ($attribs);
  my %tags;
  my @content;
  my $wellFormed = undef;

  while ($_ = shift @$data) {

    /^ *<!--/ && do {
      # ignore annotations
      while (! ($_ =~ /-->/)) {
	$_ = shift @$data;
      };
      next;
    };

    /^ *<\?/ && next;		# Ignore <? - tags 

    / *<([^\/]\S+) *(.*)\/>/ && do {
      # found a nested tag with attributes only
      print "Tag $1 in Kurzform gefunden - wird ignoriert!";
      next;
    };

    / *<([^\/][^\s>]+) *([^>]*)>(.*)/ && do {
      # Found a tag
      my $tagName = $1;
      unshift @$data, $3 if (defined($3) and $3 ne "");

      # Scan attributes
      my %localAttrs = $2 =~ / *(\S*) *= *"([^"]*)"/g;

      if (defined($name)) {
	# Scan nested Tag

	$tag = readTag($data, $tagName, \%localAttrs);
	if (!defined($tags{$tagName})) { # If there has not been a tag with the same name yet, create new hash entry
	  $tags{$tagName} = [];
	}
	push @{$tags{$tagName}}, $tag;  # add tag to the array of tags with name $tagName
      } else {
	# its not a nested tag but its the first tag to be scanned
	$name = $tagName;
	%attribs = %localAttrs;
      }
      next;
    };

    # Closing tag found
    /^ *<\/ *($name)[^>]*>(.*)/ && do {
      unshift @$data, $2 if (defined($2) and $2 ne "");
      $wellFormed = 1;
      last;
    };

    # elsewise, its ordinary content
    my ($rawData, $otherTags) = /([^<]*)(.*)/;
    unshift @$data, $otherTags if (defined($otherTags) and $otherTags ne "");
    $rawData =~ s/\&lt;/\</g;
    $rawData =~ s/\&gt;/\>/g;
    push @content, $rawData;
  }

  defined($wellFormed) &&
    return XMLTag::spawn($name, \%attribs, \%tags, join(' ', @content));

  print "Tag $name nicht wohlgeformt!";
  print @$data;
  return undef;
}


=head3 writeFile

  Returns a string being the XML representation of the XML file

=cut
sub writeFile {
  ($this) = @_;
  die "XMLFile::writeFile(): $this ist keine XMLFile-Referenz!" unless ref($this) eq "XMLFile";
  return join "\n", ('<?xml version="1.0" encoding="UTF-8" >', writeTag $this);
}


=head1 AUTHOR

  Michael J. Beer <michael.josef.beer@googlemail.com>

=cut



# Local Variables:
# mainmodul: "showmovie.pl"
# runcmd: "perl"
# End:
