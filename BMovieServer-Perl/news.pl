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
use XMLFile;

use MovieXHTML;
use Data::Dumper;
use constant {
  NO_ENTRIES => 10
};


my $news = XMLFile::spawn(MovieXHTML::RSS_FILE);

eval{
  die "Datei " . MovieXHMTL::RSS_FILE . "konnte nicht geladen werden!" unless $news;
#  $news = Dumper($news);
  my @entries = @{$news->getTag('channel')};
  die 'RSS-Datei nicht wohlgeformt' unless @entries;
  my @entries = reverse @{$entries[0]->getTag('item')};
  die 'RSS-Datei nicht wohlgeformt' unless @entries;

  my @output = ();

  for(my $c = 0; $c < NO_ENTRIES && $c < @entries; $c++) {
    my @entryL = @{$entries[$c]->getTag('title')};
    die 'RSS-Datei nicht wohlgeformt' unless @entryL;
#    $news .= "\n" . Dumper($entryL[0]);
    my $title = $entryL[0]->getContent();
    @entryL = @{$entries[$c]->getTag('link')};
    die 'RSS-Datei nicht wohlgeformt' unless @entryL;
    my $link = $entryL[0]->getContent();
    @entryL = @{$entries[$c]->getTag('description')};
    die 'RSS-Datei nicht wohlgeformt' unless @entryL;
    my $desc = $entryL[0]->getContent();
    @entryL = @{$entries[$c]->getTag('pubDate')};
    die 'RSS-Datei nicht wohlgeformt' unless @entryL;
    my $date = $entryL[0]->getContent();
    push @output, ('<p>', $date, '<br/><b><a href="', $link, '">', $title, '</a></b><br/>', $desc, '</p>');
  };

  $news = join '', @output;
};

if($@) {
  $news = $@;
};

print MovieXHTML::getFramedPage($news);




