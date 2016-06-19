/*
 * BMovieReviewer Copyright (C) 2012 Michael J. Beer
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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class ResourceManager {

	public static ResourceManager getInstance() {
		if(singleResourceManager == null) {
			singleResourceManager = new ResourceManager();
		}
		return singleResourceManager;
	}
	
	public URL getResource(String id) throws MalformedURLException {
		System.out.println("Resource requested: " + id);
		String urlString = Globals.getInstance().getProperty(id);
		System.out.println("Got " + urlString);
		URL url = null; 
		if(urlString == null || urlString.equals(Globals.PROPERTY_INTERNAL_RESOURCE)) {
			System.out.println("Trying to load internally");
			url = getUrlForInternalResource(id);
		} else {
			System.out.println("Trying to load externally");
			url = getUrlForExternalResource(urlString);
		}
		System.out.println("Finally got " + url.toString());
		return url;
	}
	
	
	public static final String[] RESOURCE_ID = {
		"xml.bmoviesty",
		"spellcheck.dictionaries",
		"logourl",       
		"splash.upload_1",
		"splash.upload_2",
		"splash.upload_3",
		"splash.upload_4",
		"splash.upload_5",
		"splash.upload_6"
	};
	
	public static final String[] RESOURCE_INTERNAL_URL = {
		"/dat/bmovie.sty",
		"/dat",
		"/dat/logo.png",
		"/dat/stage_1.png",
		"/dat/stage_2.png",
		"/dat/stage_3.png",
		"/dat/stage_4.png",
		"/dat/stage_5.png",
		"/dat/stage_6.png"
	};
	
	public static final String URL_FILE_PREFIX = "file:";
	
	//////////////////////////////////////////////////////////////////////////
	// INTERNALS
	//////////////////////////////////////////////////////////////////////////
	
	private static ResourceManager singleResourceManager;
	
	private HashMap<String, String> internalResourcePaths;
	
	protected ResourceManager() {
		internalResourcePaths = new HashMap<String, String>();
		for(int i = 0; i < RESOURCE_ID.length; i++) {
			internalResourcePaths.put(RESOURCE_ID[i], RESOURCE_INTERNAL_URL[i]);
		}
	}
	
	private URL getUrlForInternalResource(String id) {
		String path = internalResourcePaths.get(id);
		URL url = null;
		System.out.println("trying to get resource for " + id);
		if(path != null) {
			url = this.getClass().getResource(path);
			System.out.println("Loaded successful");
		}
		return url;
	}
	
	private URL getUrlForExternalResource(String path)  throws MalformedURLException {
		String absPath = System.getProperty("user.dir");
		absPath += File.separator + path;
		absPath = URL_FILE_PREFIX + absPath; 
		return new URL(absPath);
	}
}
