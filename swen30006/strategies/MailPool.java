package strategies;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.HashMap;

import automail.Building;
import automail.Clock;
import automail.MailItem;
import automail.IMailPool;


/**
 * Sample of what a MailPool could look like.
 * This one tosses the incoming mail on a pile and takes the outgoing mail from the top.
 */
public class MailPool implements IMailPool {

    private HashMap<Integer, PriorityQueue<MailItem>> mailItems;
    private Comparator<MailItem> comparator;
    private Integer timeOfLastUpdate[];
    private static final int CHANGE_STATE_TIME = 1;
    private static final int DELIVERY_TIME = 1;

    public MailPool(){

        comparator = new MailItemComparator();
        mailItems = new HashMap<>();
        timeOfLastUpdate = new Integer[Building.FLOORS];
        Arrays.fill(timeOfLastUpdate, -1);
    }
   @Override
    public void addToPool(MailItem mailItem) {

        int destinationFloor = mailItem.getDestFloor();
        int arrivalTime = mailItem.getArrivalTime();
        if(this.mailItems.containsKey(destinationFloor)) {

            /** Adding an item to an existing priority queue for a floor, but cannot add
             *  in the existing queue since the priority value of the new item will be
             *  calculated using the currentTime, so all the other elements need to have
             *  their priority calculated against the same time. (Note: this is not an
             *  issue when new elements are not added to the list since the change in
             *  value of priority, which is basically the score isn't likely to change
             *  much).
             *  Also, I make sure of using the timeOfLastUpdate[] to re-do the queue
             *  only once every second.
             */
            System.out.println("Key for " + destinationFloor + " exists");
            System.out.println("Adding " + mailItem + " to the queue");
            if(this.timeOfLastUpdate[destinationFloor  - 1] != arrivalTime) {

                PriorityQueue<MailItem> oldQueue = this.mailItems.get(destinationFloor);

                MailItem items[] = new MailItem[oldQueue.size()];
                oldQueue.toArray(items);
                PriorityQueue<MailItem> newQueue = new PriorityQueue<>(comparator);
                for (MailItem item : items) {
                    newQueue.add(item);

                }
                System.out.println("Changing timeOfLastUpdate to " + Clock.Time());
                newQueue.add(mailItem);
                this.mailItems.put(mailItem.getDestFloor(), newQueue);
                this.timeOfLastUpdate[mailItem.getDestFloor() - 1] = Clock.Time();
            }
            else {

                System.out.println("No need to change  timeofLastUpdate we are good");
                this.mailItems.get(mailItem.getDestFloor()).add(mailItem);
            }

        }
        else{
            /** If the floor doesn't exist then set a new floor along with the of MailItems to add during
             *  that time step.
             */
            PriorityQueue<MailItem> newMailFloor = new PriorityQueue<MailItem>();
            newMailFloor.add(mailItem);
            System.out.println("Adding " + mailItem + " as the first item to the floor " + destinationFloor);
            this.mailItems.put(mailItem.getDestFloor(),newMailFloor);
            this.timeOfLastUpdate[mailItem.getDestFloor() - 1] = Clock.Time();
        }

    }

    public PriorityQueue<MailItem> getFloorMail(int floor)
    {
        return this.mailItems.get(floor);
    }


    public boolean isEmptyPool() {
        boolean empty = true;
        System.out.println("Printing the pool");
        for(Integer key : this.mailItems.keySet())
        {
            System.out.println("Printing for floor "  + key);
            PriorityQueue<MailItem> pq = this.mailItems.get(key);
            for(MailItem m : pq)
            {
                System.out.println("Here begins for floor " + key);
                System.out.println(m);
                empty = false;
            }

        }
        System.out.println("Tube is empty:" + empty);
        return empty;
    }

    public MailItem getItem(int floor){
        return this.mailItems.get(floor).peek();
    }

    public void removeItem(int floor, MailItem item){
        System.out.println("Just removed: " + item);
        this.mailItems.get(floor).remove(item);
        if(this.mailItems.get(floor).size() == 0) {
            System.out.println("Removing key=" + floor + " since no items inside");
            this.mailItems.remove(floor);
        }
    }

    /*public MailItem get(){
        return mailItems.peek();
    }*/

    /*public void remove(int floor){
        mailItems.get(floor);
    }*/


    /** Class comparator allows us to implement the comparison function */
    public static class MailItemComparator implements Comparator<MailItem> {

        @Override
        public int compare(MailItem mailOne, MailItem mailTwo)
        {
            double mailOneScore = calculateDeliveryScore(mailOne);
            double mailTwoScore = calculateDeliveryScore(mailTwo);
            if (mailOneScore < mailTwoScore)
            {
                return -1;
            }
            else if (mailOneScore > mailTwoScore)
            {
                return 1;
            }
            return 0;
        }

        private static double calculateDeliveryScore(MailItem deliveryItem) {
            // Penalty for longer delivery times
            final double penalty = 1.1;
            // Take (delivery time - arrivalTime)**penalty * priority_weight
            double priority_weight = 0;



            // Determine the priority_weight
            switch(deliveryItem.getPriorityLevel()) {
                case "LOW":
                    priority_weight = 5;
                    break;
                case "MEDIUM":
                    priority_weight = 10;
                    break;
                case "HIGH":
                    priority_weight = 20;
                    break;
            }

            return Math.pow((CHANGE_STATE_TIME + (deliveryItem.getDestFloor() - Building.MAILROOM_LOCATION) +
                    DELIVERY_TIME) + Clock.Time() - deliveryItem.getArrivalTime(), penalty)*priority_weight;
        }
    }



}