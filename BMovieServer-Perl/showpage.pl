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

=pod

=encoding utf8

=head1 NAME

  showpage

=head1 SYNOPSIS

    showpage.pl?file=FILE

=head1 DESCRIPTION

  This Script creates an XHTML page containing a navigation bar on the left, a bar on the top and 
  shows the content of FILE 
  The filename to be shown is got from the post parameter 'file'
=cut

my $content;

eval {
  #my $fileName = "captain_berlin.xml";
  $ENV{'REQUEST_METHOD'} =~ tr/a-z/A-Z/;
  if ($ENV{'REQUEST_METHOD'} eq "GET") {
    $buffer = $ENV{'QUERY_STRING'};
  }
  my %params = $buffer =~ /([^=]*)=([^?]*)/g;
  my $fileName = $params{'file'};
  die "Geben sie einen Dateinamen an!" if !defined($fileName);
  open(PAGE, $fileName) or die "Konnte Datei $fileName nicht finden!";
  
  my @content = ();
  
  while ($_ = <PAGE>){
    push @content, $_;
  };
  
  $content = join '', @content;
};

if($@) {
  $content = $@;
};

print MovieXHTML::getFramedPage($content);




