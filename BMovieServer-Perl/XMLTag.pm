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
package XMLTag;

use Data::Dumper;

# our @EXPORT  = ();
our @VERSION = 1.00;


=pod

=encoding utf8

=head1 NAME
 
  XMLTag - Representation of a XML tag

=head1 SYNOPSIS

    C<use XMLTag;>

=head1 DESCRIPTION

  A tag consists of 
    a name being a simple string
    an attribute list being a hash set of attribute names as keys and attribute values as values
    a nested tag list being a hash set of tag names, each name is associated with an array of XMLTags
    content being a simple string    
  Parameters are handled by references

=head2 Functions

  The following functions are exported by default

=head3 spawn

    C<$tag = spawn XMLTag (name, attributes, tags, content);>

  Returns a new instance of type XMLTag

=cut

sub spawn {
  my ($name, $attr, $tags, $content) = @_;
  bless {'name' => $name, 'attributes' => $attr, 'tags' => $tags, 'content' => $content};
}

=head3 getName

  Liefert den Namen eines Tags zurueck 
  Name wird direkt zurueckgeliefert

=cut

sub getName {
  my ($this) = @_;
  return $$this{'name'};
}

=head3 getTags

  Liefert Referenz auf Attributehash

=cut

sub getTags {
  my ($this, $name) = @_;
  return $$this{'tags'};
}


=head3 getTag

  Liefert ein Array mit allen zu einem Namen gehoerenden Untertags zurueck
  Rueckgabe erfolgt per Referenz

=cut

sub getTag {
  my ($this, $name) = @_;
  my %tags = %{getTags $this};
  return $tags{$name};
}

=head3 modTag

  Fuegt den uebergebenen Tag als Untertag an
  Exisitert dieser bereits, wird er ueberschrieben

=cut

sub modTag {
  my ($this, @tag) = @_;
  my $name = $$tag{'name'};
  push @{$$this{'tags'}{$name}}, (\@tag);
}


=head3 getContent

  Liefert den Inhalt des Tags zurueck

=cut

sub getContent {
  my ($this) = @_;
  return $$this{'content'};
}


=head3 setContent

 Setzt den Inhalt des Tags

=cut

sub setContent {
  my ($this, $content) = @_;
  $$this{'content'} = $content;
}


=head3 getAttributes

  Liefert die Attribute 

=cut

sub getAttributes {
  my ($this) = @_;
  return $$this{'attributes'};
}


=head3 getAttribute

  Liefert Wert eines Attributs zurueck

=cut

sub getAttribute {
  my ($this, $name) = @_;
  return $$this{'attributes'}{$name};
}


=head3 writeTag

  Returns a string being the XML representation of the tag

=cut

sub writeTag {
  my ($this) = @_;

  # print "This ist $this \n";
  my $name = $this->getName();
  my @strings = ("<$name"); # in here, all sub strings will be collected to be joined to the eventual result string finally

  my %attrs = %{$this->getAttributes()};
  for my $attr (keys %attrs) {
    push @strings, (" ", $attr, '="', $attrs{$attr}, '"');
  }
  push @strings, ">\n";

  my $tags = getTags $this;
  for my $tag (sort keys(%$tags)) {
    for my $singleTag (@{$$tags{$tag}}) {
      push @strings, $singleTag->writeTag();
    }
  };

  push @strings,  ($this->getContent(), "</$name>\n");
  return join '', (@strings, "\n");
}


=head3 getSubTag

  Liefert eine Referenz auf ein Array von Subtags zurueck, das nach der Methode SubTag1.SubTag2.SubTag3 angesprochen wird
  Parameter:
  
  $current : Wurzeltag, das nach Untertags durchsucht werden soll
  $path    : Pfad zum gesuchten Tag, angegeben als Abfolge von durch '.' getrennten Untertags

=cut

sub getSubTag {
  my ($current, $path) = @_;
  #die "XMLTag::getSubTag(): Kein Wurzeltag angegeben!" unless ref($current) eq "XMLTag";
  return () unless (defined($path) && $path ne '');

  my ($root, $remainder) = $path =~ /^([^.]*)\.*(.*)/;
  $current = $current->getTag($root);
  return $current unless $remainder;
  my @result = ();
  for $tag (@$current) {
    if($remainder) {
      my $subTags = $tag->getSubTag($remainder);
      push @result, @$subTags;
    };
  }
  return \@result;
};

1;

=head1 AUTHOR

  Michael J. Beer <michael.josef.beer@googlemail.com>

=cut


# Local Variables:
# mainmodul: "showmovie.pl"
# runcmd: "perl"
# End:
