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
package MovieConfig;

use constant {
  LOG_FILE => 'MovieXHTML.log',
  MOVIE_DIR => 'movies',
  MOVIE_LIST_FILE => 'movies.sst',
  QUOTES_FILE => 'quotes.sst',
  RSS_FILE => 'mrs.rss',
  IMPRESSUM => 'data/impressum.xhtml',
  START_PAGE => 'data/start.xhtml',
  USERS_DIR => 'users'
};

our @pointCategories =  qw(Realismus Niveau Pornofaktor Gewaltverherrlichung Gewaltdarstellung Professionalität Sexismus Unterhaltungswert);


sub getUsersDir {return USERS_DIR};
sub getLogFile {return LOG_FILE};
sub getMovieDir {return MOVIE_DIR};
sub getMovieListFile {return MOVIE_LIST_FILE};
sub getQuotesFile {return QUOTES_FILE};
sub getRSSFile {return RSS_FILE};
sub getImpressum {return IMPRESSUM};
sub getStartPage {return START_PAGE};

1;
