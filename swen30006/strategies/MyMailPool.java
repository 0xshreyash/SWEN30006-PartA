package strategies;
import java.util.*;
import automail.*;

/**
 * A class representing the mail pool.
 * Written by Sebastian Baker 757931
 * 
 * This strategy places new mail items in the pool and sorts the pool by priority/deliveryDistance.
 * This should leave the most urgent deliveries on top of the pool.
 */
public class MyMailPool implements IMailPool {

    /** LinkedLIst of mailItems pending delivery*/
    private LinkedList<MailItem> mailItems;
    
    /** Comparator used to sort items in the pool*/
    private static MailItemCompare<MailItem> mailItemCompare = new MailItemCompare<MailItem>();
    
    /**
     * MyMailPool constructor. Initializes the LinkedList which stores mail items.
     */
    public MyMailPool(){
        mailItems = new LinkedList<MailItem>();
    }
    
    /**
     * Adds a new mailItem to the pool, then sorts the pool.
     */
    @Override
    public void addToPool(MailItem mailItem){
        mailItems.addFirst(mailItem);
        Collections.sort(mailItems, mailItemCompare);
    }
    
    /**
     * Returns the mailItem at index i in the pool.
     * 
     * @param i	index of the item in the pool.
     * @return the MailItem at index i.
     */
    public MailItem peek(int i){
    	return mailItems.get(i);
    }
    
    /**
     * Removes the mailItem at index i from the pool, then sorts the pool.
     * 
     * @param i	index of the item in the pool.
     */
    public void remove(int i) {
    	mailItems.remove(i);
        Collections.sort(mailItems, mailItemCompare);
    }
    
    /**
     * Returns the number of mail items in the mailPool.
     * 
     * @return the number of mail items in the mailPool.
     */
    public int getPoolSize() {
    	return mailItems.size();
    }
    
    /**
     * Comparator which compares mail items by priority/deliveryDistance. This ensures
     * that the most important mail items are chosen first by the robot.
     */
    private static class MailItemCompare<E extends MailItem> implements Comparator<MailItem> {

		@Override
		public int compare(MailItem o1, MailItem o2) {
			double scorediff = myMailCompare(o2)-myMailCompare(o1);
			double eps = 0.001;
			
			if (Math.abs(scorediff) <= eps) {
				return 0;
			} else if (scorediff < 0) {
				return -1;
			}
			
			return 1;
		}
		
		private double myMailCompare(MailItem m) {
			
	        // Steps required to deliver the item and return
	        int floordist = 2*Math.abs(m.getDestFloor() - Building.MAILROOM_LOCATION)+1;
	        
	        // Priority multiplier of the item
	        double priority_weight = 0;
	        switch(m.getPriorityLevel()){
	            case "LOW":
	                priority_weight = 1;
	                break;
	            case "MEDIUM":
	                priority_weight = 1.5;
	                break;
	            case "HIGH":
	                priority_weight = 2;
	                break;
	        }
	        
	        return priority_weight/floordist;
		}
    }
}
