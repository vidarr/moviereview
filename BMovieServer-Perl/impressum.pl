#!/usr/bin/perl
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

my $text;

eval{
  open(IMPRESSUM, MovieXHTML::IMPRESSUM) or die 'Impressum nicht gefunden!';
  my @text;
  while($_ = <IMPRESSUM>) {
    push @text, $_;
  };
  $text = join '', @text;
};

$text = $@ if($@);

print MovieXHTML::getFramedPage($text);
