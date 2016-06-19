/*
 * BMovieReviewer Copyright (C) 2009 Michael J. Beer
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package data;



/**
 * Verwaltet einen HTTP-Link
 * @author mibeer
 *
 */
public class Link extends QualifiedString {

	
	public final static String[] TYPES = { "UNBEKANNT", "IMDB(De)",
		"IMDB(En)", "Wiki(De)", "Wiki(En)", "VideoRaiders", "OFDB", "badmovies.de"
		/* , "Youtube" */ };

	public Link(int typ, String link) {
		super(typ, link, TYPES);
	}
	
	public Link clone() {
		return new Link(getTyp(), new String(getText()));
	}
}
