package strategies;

import exceptions.TubeFullException;

import java.util.Collections;
import java.util.Comparator;

import automail.*;

/**
 * A class for sorting mail and filling the storage tube.
 * Written by Sebastian Baker 757931
 * 
 * This strategy iterates over the items in the mail pool. The first item
 * defines the direction the robot will travel on the trip.
 * Add subsequent items if they will fit and and if they don't cause the robot to go past the mail room
 * on its trip.
 * Lastly, sort items in the tube to prevent robot from changing directions on the trip,
 * then tell the robot to start delivering (return true).
 */
public class MyMailSorter implements IMailSorter{
	
	/** MailPool from which mail items are removed. */
	private MyMailPool mailPool;
	
	/** Comparator used to sort items in the Robot StorageTube*/
	private static DestFloorCompare<MailItem> destFloorCompare = new DestFloorCompare<MailItem>();
	
	/**
     * MyMailSorter constructor.
     * @param mailPool MailPool from which mail items will be removed.
     */
	public MyMailSorter(MyMailPool mailPool) {
		this.mailPool = mailPool;
	}
	
	/**
     * Fills the storage tube.
     * 
     * The first item defines the direction (pathDir) the robot will travel on the trip.
     * Add subsequent items if they will fit and and if they don't cause the robot to go
     * past the mail room on its trip (iDir == pathDir).
     * Lastly, sort items in the tube to prevent robot from changing directions on the trip,
     * then tell the robot to start delivering (return true).
     * 
     * @param tube	The storage tube of the robot
     * @return		True to begin delivery, false to wait at the mail room
     */
    @Override
    public boolean fillStorageTube(StorageTube tube) {
    	
        try{
            if (mailPool.getPoolSize() > 0) {
            	
            	int pathDir = 0;	// Direction of the trip. +ve is up, -ve is down.
            	int iDir = 0;		// Direction of the destination of a given item. +ve is up, -ve is down.
            	
            	for (int i=0; i<mailPool.getPoolSize(); i++) {
            		
            		// Skip item if it doesn't fit in the tube
            		if (tube.MAXIMUM_CAPACITY-tube.getTotalOfSizes()-mailPool.peek(i).getSize() < 0) {
            			continue;
            		}
            		
            		// Direction of the item from the mail room
            		int iDist = mailPool.peek(i).getDestFloor() - Building.MAILROOM_LOCATION;
            		if (iDist != 0) {
            			iDir = iDist/Math.abs(iDist);
            		} else {
            			iDir = 0;
            		}
            		
            		// Add the item if it's the first item or if it will not cause a detour
            		if (iDir == 0 || pathDir == 0 || pathDir == iDir) {
            			
            			tube.addItem(mailPool.peek(i));
            			mailPool.remove(i);
            			i--;
            			
            			// Define trip information using the first item added to the tube
            			if (pathDir == 0 && iDir != 0) {
	            			pathDir = iDir;
            			}
            		}
            	}
            } else if(tube.isEmpty()) {
            	return false;
            }
        }
        
        /** Refer to TubeFullException.java --
         *  Usage below illustrates need to handle this exception. However you should
         *  structure your code to avoid the need to catch this exception for normal operation
         */
        catch(TubeFullException e){
        	return true;
        }
        
        // Sort the items in the tube before delivery
        Collections.sort(tube.tube, destFloorCompare);
        return true;
    }
    
    /**
     * Comparator which compares mail items by distance from the mail room. Used to sort the mail tube
     * to ensure fast trips.
     */
    private static class DestFloorCompare<E extends MailItem> implements Comparator<MailItem> {
    	
		@Override
		public int compare(MailItem o1, MailItem o2) {
			return Math.abs(o2.getDestFloor()-Building.MAILROOM_LOCATION)-
					Math.abs(o1.getDestFloor()-Building.MAILROOM_LOCATION);
		}
    }
    
}
