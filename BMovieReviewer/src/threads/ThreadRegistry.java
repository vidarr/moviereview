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
package threads;

import java.awt.Event;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import tools.UpdateListener;

public class ThreadRegistry {
	
	public static ThreadRegistry getInstance() {
		if(registry == null)
			registry = new ThreadRegistry();
		return registry;
	}
	
	protected ThreadRegistry() {
		this.threads = new HashMap<Runnable, String>();
		this.listeners = new LinkedList<UpdateListener>();
	}
	
	public void registerThread(Runnable thread, String name){
		if(thread == null) {
			throw new IllegalArgumentException();
		}
		this.threads.put(thread, name);
		notifyListenersAdded(thread);
	}
	
	public void unregisterThread(Runnable thread){
		if(thread == null) {
			throw new IllegalArgumentException();
		}		
		this.threads.remove(thread);
		notifyListenersRemoved(thread);
	}
	
	
	public Set<Runnable> getThreads(){		
		return threads.keySet();
	}
	
	public List<String> getThreadNames() {
		List<String> names = new LinkedList<String>();
		
		for(Runnable thread : this.threads.keySet()) {
			names.add(thread.toString());
		}
		return names;
	}
	
	public String toString() {
		String threads = "";
		for(Runnable thread : this.threads.keySet()) {
			threads += thread.toString() + "\n";
		}
		return threads;
	}
	
	
	public void addListener(UpdateListener l) {
	    if(l == null) {
	        throw new IllegalArgumentException();
	    }
	    listeners.add(l);
	}
	
	public boolean removeListener(UpdateListener l) {
        if(l == null) {
            throw new IllegalArgumentException();
        }
        return listeners.remove(l);
    }
	
	protected void notifyListenersAdded(Runnable thread) {
	    Event e = new Event(this, 0, thread);
	    for (UpdateListener l: listeners) {
	        l.added(e);
	    }
	}
	
	
	protected void notifyListenersRemoved(Runnable thread) {
        Event e = new Event(this, 0, thread);
        for (UpdateListener l: listeners) {
            l.removed(e);
        }
    }
	
	
	public String getName(Runnable thread) {
	    String name = threads.get(thread);    
	    return (name == null) ? "" : name;
	}
	
	protected List<UpdateListener> listeners = null;
	protected HashMap<Runnable, String> threads = null;
	protected static ThreadRegistry registry = null;

}
