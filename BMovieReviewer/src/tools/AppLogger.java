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
package tools;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.Globals;

/**
 * Stellt einen globalen Logger zur Verfuegung
 * @author mibeer
 *
 */
public class AppLogger {
	protected static java.util.logging.Logger logger = Logger.getLogger(Globals.APP_NAME);
	
	
	public static Logger getLogger() {
		return logger;
	}

	public static void log(Level level, String msg) {
		logger.log(level, msg);
	}
	
	public static void 	info(String msg) {
		logger.info(msg);
	}
	
	public static void config(String msg) {
		logger.config(msg);
	}
	
	public static void severe(String msg) {
		logger.severe(msg);
	}
	
	public static void warning(String msg) {
		logger.warning(msg);
	}
	
	public static void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
		logger.throwing(sourceClass, sourceMethod, thrown); 
	}
	
	public static void  entering(String sourceClass, String sourceMethod) {
		logger.entering(sourceClass, sourceMethod);
	}
	
	public static void exiting(String sourceClass, String sourceMethod) {
		logger.exiting(sourceClass, sourceMethod); 
	}
	
	public static void fine(String msg) {
		logger.fine(msg);
	}
	
	public static void finer(String msg) {
		logger.finer(msg);
	}
	
	public static void finest(String msg) {
		logger.finest(msg);
	}
	

}


