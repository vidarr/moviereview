/*
 * BMovieReviewer Copyright (C) 2009, 2010 Michael J. Beer
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

import tools.AppLogger;

/** 
 * This Thread invokes another Thread, waits a given amount of time and kills the invoked thread 
 * if still running
 * !!! This one uses Thread.stop() to terminate thread !!!
 * @author Michael J. Beer
 */
public class TimeoutThread implements Runnable{

    protected Runnable  thread;
    protected int millies;
    
    public TimeoutThread(Runnable r) {
        this(r, 6000);
    }
    
    public TimeoutThread(Runnable r, int millies) {
        if(r == null) {
            throw new IllegalArgumentException();
        }
        this.thread = r;
        this.millies = millies;
    }
    
    
    @Override
    @SuppressWarnings("deprecation")  // stop is the only possibility to stop a thread for sure
    public void run() {
        int count = 0;
        ThreadRegistry.getInstance().registerThread(this, "TimeOut");
        Thread thread = new Thread(this.thread);
        thread.start();
        try{
            // This threads checks every 1/100 * millies whether the job has finished in order not to
            // wait the entire millies milliseconds even if the job has been finished already
            while(thread.isAlive() && count < 100) {
                Thread.sleep(millies/100);
                count++;
            }
        }catch (InterruptedException e) {
            AppLogger.throwing("TimeoutThread", "run()", e);
        }
        if(thread.isAlive()) {
            thread.stop();
            AppLogger.warning("Timeout: Thread " + thread.toString() + "wurde getoetet.");
        } 
        ThreadRegistry.getInstance().unregisterThread(this);
    }
    
    

}
