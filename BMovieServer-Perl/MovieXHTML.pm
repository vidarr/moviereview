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
package MovieXHTML;

use MovieConfig;
use XMLTag;
use XMLFile;
use Date;
use GUDB;

use Data::Dumper;

use constant {
  LOG_FILE => 'MovieXHTML.log',
  MOVIE_DIR => 'movies',
  MOVIE_LIST_FILE => 'movies.sst',
  CIRCLE => '&#x25CB;',
  FILLED_CIRCLE => '&#x25CF;',
  QUOTES_FILE => 'quotes.sst',
  RSS_FILE => 'mrs.rss',
  IMPRESSUM => 'data/impressum.xhtml',
  START_PAGE => 'data/start.xhtml'
};


=pod

=encoding utf8

=head1 NAME

  MovieXHTML

=head1 SYNOPSIS

    my $htmlMpviePage = getMoviePage('movie.xml');

=head1 DESCRIPTION

  This Package provides for routines that render XHTML pages

=head2 Functions

  The following functions are provided by default

=cut


my $title = 'B Movie Review System';
my ($header, $cover, $links, $textfields, $quotes);

my $movieListFile = MOVIE_LIST_FILE;
my $movieDir = MOVIE_DIR;

my %movies;
my %quotes;

my @pointCategories = @MovieConfig::pointCategories;

my @textCategories = ('Titel', 'Land', 'Originaltitel', 'Jahr', 'FSK', 'Genre', 'Ankündigung(RSS)', 'Handlung', 
		       'Fehler(Technisch)', 'Fehler(Inhaltlich-Logisch)', 'Fehler(Wissenschaftlich)', 'Bemerkungen', 'Bild');

my @textTags = ('titel','land', 'originaltitel', 'jahr', 'fsk', 'genre', 'rss', 'handlung', 
		 'technisch', 'inhalt', 'wissenschaft', 'bemerkungen', 'bild');


my @pointLineTemplate = ('<td align="center">' . CIRCLE . '</td>', '<td align="center">' . CIRCLE . '</td>',
			  '<td align="center">' . CIRCLE . '</td>', '<td align="center">' . CIRCLE . '</td>',
			  '<td align="center">' . CIRCLE . '</td>');


my @pointWeights = (5, 2, 2, -3, 4, -3, 3, 2);

my $mark = 0;

my $pointTable;
my $dump = '';


sub getPoint {
  my ($name, $points) = @_;
  my @tags = @{$points->getTag($name)};
  my $tag = $tags[0];
  die "Tag $name nicht gefunden!\n" if !defined($tag);
  my %attributes = %{$tag->getAttributes()};
  return $attributes{'val'};
}


sub createPointLine {
  my ($name, $points) = @_;
  my @line = ("<tr><td align=\"right\"><b> $name </b></td>", @pointLineTemplate);
  my $p = getPoint($name, $points);
  $line[6 - $p] = '<td align="center">' . FILLED_CIRCLE . '</td>' if $p > 0;
  $line[3] = "<td align=\"center\"><small>Nicht gewertet</small></td>" if $p < 1;
  return join '', (@line, '</tr>');
}

sub getTitle {
  my ($tag) = @_;
  $tag = $tag->getTag('titel');
  $tag = $$tag[0];
  die "Konnte Titel nicht finden"  unless defined($tag);
  return getContent $tag;
}

sub createLinkLink {
  my ($link) = @_;
  my $attr = $link->getAttributes();
  $attr = $$attr{'typ'};
  $link = $link->getContent();
  return "<a href=\"$link\">$attr</a>";
}

sub getTextField {
  my ($textfields, $kind) = @_;
  my @field = @{$textfields->getTag($kind)};
  my $tag = $field[0];
  die "Tag $kind nicht gefunden!" if !defined($tag);
  return $tag->getContent();
}


=head3 getMoviePage

  Creates a movie description page
  Expects the file name as argument
  The XHTML code is returned for further processing

=cut

sub getMoviePage {

  my ($fileName, $movieDir, $author) = @_;
    
  die "Bitte geben Sie einen Film an!\n" if (!defined($fileName));
  $fileName = $movieDir . '/' . $fileName if $fileName;
  {
    my $file = XMLFile::spawn($fileName);

    my $texts = $file->getTag("textfelder");
    die "Konnte Textfelder nicht finden!" unless $texts;
    $texts = $$texts[0];
    die "Konnte Textfelder nicht finden!" unless $texts;

    my $details = $file->getTag("details");
    if (defined($details)) {
      $title = getTitle($file);
      $details = $$details[0];
      die "Konnte Details nicht finden!" unless $details;
    } else {
      $details = $texts;
      $title = getTitle($details);
    }
    
    die "Konnte Titel nicht holen!" if (!$title);

    $cover = $file->getTag('cover');
    $cover = $$cover[0]->getContent();
    $cover = $movieDir . '/' . $cover if defined($movieDir);

    # Ueberblick ausgeben
    my @text = ('<table width="80%"><colgroup><col width="*"/><col widt="*" align="center"/><col width="*" align="right"/></colgroup><tr><td><b>',
		$textCategories[0], '</b> : ', $title, '</td><td><b>',
		$textCategories[5], '</b> : ', getTextField($details, $textTags[5]), '</td><td align="right"><b>',
		$textCategories[4], '</b> : ', getTextField($details, $textTags[4]), '</td></tr><tr><td><b>',
		$textCategories[2], '</b> : ', getTextField($details, $textTags[2]), '</td><td><b>',
		$textCategories[3], '</b> : ', getTextField($details, $textTags[3]), '</td><td align="right"><b>',
		$textCategories[1], '</b> : ', getTextField($details, $textTags[1]), '</td><td/></tr></table>');
    $header = join '', @text;

    # get ausgeben
    my @points = @{$file->getTag('punktwertungen')};
    die "Keine Punktwertungen gefunden!\n" if (!@points or @points < 1);
  
    # only one tag 'punktwertungen' allowed, so lets stick to the first one 
    my $points = $points[0];
    my $sum = 0;
    my @pointTable = ('<table width="80%">' .
		      '<colgroup><col width="30%"/><col width="10%"/><col width="10%"/><col width="10%"/><col width="10%"/><col width="10%"/>' .
		      '</colgroup>' .
		      '<tr><th></th><th>hoch</th><th></th><th></th><th></th><th>niedrig</th></tr>');
    for (my $index = 0; $index <  @pointCategories; $index++) {
      my $cat = $pointCategories[$index];
      push @pointTable, createPointLine($cat, $points);
      $mark += getPoint($cat, $points) * $pointWeights[$index];
      $sum += $pointWeights[$index];
    }
    $mark /= $sum;
    $mark = sprintf("%.1f" , $mark);

    push @pointTable, '</table>';
    $pointTable = join '',  @pointTable;


    # Create Textfields like plot, errors, annotations

    @text = ();
    $links = $file->getTag('links');
    $links = $$links[0]->getTags();
    $links = $$links{'link'};
    for $link (@$links) {
      push @text, (' ',createLinkLink($link), ' ');
    }
    $links = join '', @text;

    @text = ('<p><center><b>', getTextField($texts, $textTags[6]), '</b></center></p>');
    for (my $index = 7; $index < @textTags; $index++) {
      push @text, ('<p><b>',$textCategories[$index], '</b> : ', getTextField($texts, $textTags[$index]), '</p>');
    }

    $textfields = join '', @text;

    # finally, generate Quotes
    @text = ('<b>Zitate : </b><ul>');
    my @quotations = @{$file->getTag('zitate')};
    if(@quotations) {
      @quotations = @{$quotations[0]->getTag('zitat')};
      for my $quote (@quotations) {
	push @text, ('<li>', $quote->getContent(), '</li>');
      }
      $quotes = join '', (@text, '</ul>');
    } else {
      $quotes ="";
    };
    if(defined $author) {
      $author = '<a href="showpage.pl?file=' . MovieConfig::USERS_DIR . '/' . $author . '.xhtml">' . $author . '</a>';
    } else {
      $author = 'unbekannt';
    };
  }



  my $page = "";


  if ($@) {

    $page .= <<ENDOFERROR;
<p>Fehler: $@</p>
ENDOFERROR

  }

  $page .= <<ENDOFMOVIE;
<center><h1>$title</h1></center><p>
<center><img src="$cover" alt="Cover"></img></center></p>
<p><center>$links</center></p>
<p><center>
$header
</center>
<br/><br/>
</p>
<center><p>
$pointTable
</p><p>
<b>Gesamtnote: </b> <font color="red"><big><big>$mark</big></big></font>
</p>
</center>
<br/><br/>
<p>
$textfields
</p><p>
$quotes
</p>
<p align="right">
Autor: $author
</p>
ENDOFMOVIE

  return $page;
};


=head3 getMovieListPage

  gives a framed page containing a list of all movies that have been given to that routine

=cut
sub getMovieListPage {

  my @content = ("<table><tr>");
  my $cnt = -1;
  my $dir = MovieXHTML::getMovieDir();

  eval{
    for $movie (@_) {
      if(++$cnt >= 3) {
	push @content, "</tr>";
	$cnt = 0;
      };
      my ($xml, $cover, $author);
      if(ref($movie) eq 'ARRAY') {
	($xml, $cover, $author, $movie) = @$movie;
      } else {
	($xml, $cover, $author) = @{MovieXHTML::getMovie($movie)};
      };
      die "Film $movie nicht gefunden $reference" unless $xml;
      
      if($cover) {
	$cover = "<img src=\"$dir/$cover\" alt=\"$cover\">";
      } else {
	$cover = "Nicht verfügbar";
      };
      my $link = "<a href=\"showmovie.pl?movie=$xml&author=$author\">";
      push @content, "<td><center>$link$cover</a><br/>$link$movie</a></center></td>";
    };
    unless(@content > 1) {
      push @content, '<p align="center"><br><b>Kein Eintrag</b></br></p>';
    };
  };

  if($@) {
    print MovieXHTML::getFramedPage($@);
    exit -1;
  };

  my @endTags = ("</table>");
  unshift @endTags, "</tr>" unless $cnt == 0;
  return join '', (@content, @endTags);
};


=head3 getNavigation

  Creates the navigation bar
  The XHTML code is returned for further processing

=cut

sub getNavigation {
  my $impressum = IMPRESSUM;
  my $startpage = START_PAGE;
  return <<ENDOFDATA;
<p>
<img src="art/logo.png" alt=" B Movie Review System Logo"/>
</p><p>
<a href="showpage.pl?file=$startpage">Start</a>
<br/>
<a href="news.pl">Neues</a>
</p><p>
<a href="listmovies.pl">Alle Filme</a><br/>
<a href="showusers.pl">Mitglieder</a><br/>
</p><p>
<form action="search.pl">
  <p>Suche:<br><input name="title" type="text" size="20" maxlength="100"></p>
</form>
</p><p>
  <a href="showpage.pl?file=extsearch.xhtml">Erweiterte Suche</a>
</p><p>
<small><a href="showpage.pl?file=$impressum">Impressum</a></small>
</p><p>
<small><a href="mrs.rss" class="feed"><img src="data/feed.png"/></a><a href="mrs.rss">RSS - Feed</a></small>
</p>

ENDOFDATA

}


=head3 getQuote

  Returns a random quote as a string formatted as an html link

=cut

sub getQuote {
  my @quotes = keys %quotes;
  my $index = int(rand(@quotes));
  my $quote = $quotes[$index];
  return '<a href="showmovie.pl?movie='. $quotes{$quote}->[1] . '">' . $quote . ' (' . $quotes{$quote}->[0] . ')' . '</a>';
}


=head3 getFramedPage

  Returnes the actual page
  The navigation bar and the header bar are created from scratch
  The content for the main pane is given as a String to the function

=cut

sub getFramedPage {
  my ($cont) = @_;

  die if !defined($cont);

  my $navigation = getNavigation;
  my $quote = getQuote;

  return <<ENDOFDATA;
Content-Type: text/html;charset=utf-8


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
       "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<xhtml xmlns="http://www.w3.org/1999/xhtml">
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
  <head>
    <title>B Movie Review System</title>
    <link href="./styles/mainstyle.css" rel="stylesheet" type="text/css" />
    <style type="text/css">
    </style>
  </head>
  <body>
    <div id="navigation">$navigation</div>
    <div id="main">
      <div id="quotes">$quote</div>
      <div id="content">
        $cont
      </div>
    </div>
  </body>
</xhtml>
ENDOFDATA

};



=head3 getXHTMLPage

  Returnes an xhtml page 
  The actual content is given as a String to the function

=cut

sub getXHTMLPage {
  my ($cont) = @_;

  die if !defined($cont);

  return <<ENDOFDATA;
Content-Type: text/html;charset=utf-8


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
       "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<xhtml xmlns="http://www.w3.org/1999/xhtml">
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
  <head>
    <title>B Movie Review System</title>
  </head>
  <body>
    $cont
  </body>
</xhtml>
ENDOFDATA

};


=head3 getXHTMLPage

  Returnes an html page 
  The actual content is given as a String to the function

=cut

sub getHTMLPage {
  my ($cont) = @_;

  die if !defined($cont);

  return <<ENDOFDATA;
Content-Type: text/html;charset=utf-8


<html>
  <head>
    <title>B Movie Review System</title>
  </head>
  <body>
    $cont
  </body>
</html>
ENDOFDATA

};


=head3 loadQuotes

  Loads quotes from a file 
  The file name can be given as argument, elsewise they are loaded from QUOTE_FILE

=cut

sub loadQuotes {
  my ($quoteFile) = @_;
  $quoteFile = QUOTES_FILE unless defined($quoteFile);
  open QUOTES, $quoteFile or die "$quoteFile nicht gefunden";
  while($_ = <QUOTES>) {
    my @line = /^\s*'([^']*)' '([^']*)' (.*)/;
    die "$quoteFile hat nicht das richtige Format" unless @line && @line > 1;
    $quotes{$line[0]} = [$line[1], $line[2]];
  }
  close QUOTES;
}


=head3 log

  Logs one or more messages

=cut

sub log {
  open LOGGER, ">>", LOG_FILE or die "Konnte Log-Datei nicht oeffnen";
  while($_ = shift) {
    $time = Date::getTimeStamp;
    print LOGGER "$time :: $_\n";
  };
  close(LOGGER);
};



=head3 loadMovieList

  Loads list of available movies from a file
  If none is specified as argument, the file name is taken to be $movieListFile

=cut

sub loadMovieList {
  my $file = shift;
  $file = $movieListFile unless defined $file;
  open(LIST, "<", $file) or die "Datei $file konnte nicht geoeffnet werden!";
  while($_ = <LIST>) {
#    print;
    my ($xml, $cover, $author, $name) = /'([^']*)'/g; #/(\S*)\s+(\S*)\s+(\S*)\s+'([^']*)'/;
    die "Format der Datei $file nicht regulaer" unless $name;
    $movies{$name} = [$xml, $cover, $author];
  };
  close LIST;
#  print keys %movies;
}


=head3 getMovie

  Expects $movie - title of a movie - as argument 
  Gives the data of $movie if $movie has been registered

=cut

sub getMovie {
#  print keys %movies;
  my ($movie) = @_;
  return undef unless $movie;
  return $movies{$movie};
}


=head3 getMovies

  Gives a list of all registered movies 
  The list is not sorted

=cut

sub getMovies {
#  print "getMovies:";
#  print keys %movies;
  return keys %movies;
};



=head3 getMovieDir

  Gives current movie directory

=cut

sub getMovieDir {
  return $movieDir;
};



=head3 getTempDir

  Gives current temporary directory

=cut

sub getTempDir {
  return $movieDir;
};


=head3 registerRSS

  Writes a rss entry into the rss-feed file
  Expects 3 parameters:
    1. Movie title 
    2. XML file name
    3. RSS string

  Optional:
    4. the author's name could be given as a 4th parameter and 
    5. the name of the rss feed file as 5th parameter. If not given, the value of the constant RSS_FILE is used instead

=cut

sub registerRSS {
  my ($title, $xml, $rss, $author, $file) = @_;
  die "To few arguments for registerRSS" unless $title && $xml && $rss;
  $author = 'unknown' unless $author;
  $file = RSS_FILE unless $file;
  my @content = ();
  my $currDate = Date::getRFC822;

  open(RSS, $file) or die "Could not open RSS file";
  while($line = <RSS>) {
    $line =~ /\s*\<\/channel\>/ && do {
      push @content, ("     <item>\n         <title>", $title, " online</title>\n         <description>", $rss, 
		      "</description>\n         <link>" . "showmovie.pl?movie=", $xml, "</link>\n         <author>", 
		      $author, "</author>\n         <guid>$title:$currDate</guid>\n         <pubDate>", 
		      $currDate, "</pubDate>\n      </item>\n");
    };
    push @content, $line;
  };
  close RSS; 
  open (RSS, ">", $file) or die "Could not open RSS file";
  print RSS join( '', @content);
  close RSS;
};




=head3 registerMovie

  Registers one or more movies to the system
  Expects as first argument the file name of the registry
  The second parameter is the name of the author
  Followed by paths of the xml files of the movies

=cut

sub registerMovie {
  my $file = shift;
  die "Give a valid file!" unless $file; # && open(REGISTRY, ">>", $file);
  my $author = shift;
  die "Give user name!" unless $author;

  my $gudb = GUDB::spawn();
  $gudb->connectToSource('file', $file) if -f $file;
  while(my $movieFile = shift) {
    my ($dir, $name);
    if($movieFile =~ /(.*)\/([^\/]*)$/) {
      ($dir, $name) = ($1, $2);
    } else {
      ($dir, $name) = ('', $movieFile);
    };
    die "Give valid path!" unless $name;
    my $movie = XMLFile::spawn($movieFile);
    my $title;
    my $details = $movie->getTag("details");
    if (defined($details)) {
      $title = getTitle($movie);
      $details = $$details[0];
      die "Konnte Details nicht finden!" unless $details;
    } else {
      $details = $movie->getTag("textfelder")->[0];
      $title = getTitle($details);
    };
    die "Konnte Titel nicht holen!" unless $title;
    my @cover = @{$movie->getTag('cover')};
    my $cover = $cover[0]->getContent() if @cover;
    die "Titelbild nicht gefunden" unless $cover && -e "$dir" . "/" . "$cover";
    @cover = @{$movie->getSubTag('textfelder.rss')};
    my $rss = $cover[0]->getContent() if @cover;
    die "No rss string found" unless $rss;
    # my @rss = @{$movie->getTag('textfelder.rss')};
    # my $rss = $rss[0] if @rss;
    # die "RSS-Eintrag nicht gefunden" unless $cover && -e "$tempDir";
    #print REGISTRY "'$title' '$_' $cover";
    #print REGISTRY "$name $cover $author '$title'\n";
    $gudb->insertToDB($movie, $name, $author);
    registerRSS $title, $name, $rss, $author;
  };
  #close REGISTRY;
  $gudb->store($file);
};


=head3 getMovieRegistry

  Gives the movie registry

=cut

sub getMovieRegistry {
  return MOVIE_LIST_FILE;
};

eval{
  loadQuotes;
};

if(!defined($@)) {
  print getFramedPage($@);
  return 0;
};
1;

=head1 AUTHOR

  Michael J. Beer <michael.josef.beer@googlemail.com>

=cut


# local Variables:
# runcmd: "perl"
# End:
