package strategies;


/** Remove the imports that are not used */
<<<<<<< HEAD
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.HashMap;
=======
>>>>>>> Knapsack
import java.util.ArrayList;
import automail.MailItem;
import automail.IMailPool;

import java.util.Collections;
import java.util.Comparator;


/**
 * Sample of what a MailPool could look like.
 * This one tosses the incoming mail on a pile and takes the outgoing mail from the top.
 */
public class MailPool implements IMailPool {

<<<<<<< HEAD
    private HashMap<Integer, ArrayList<MailItem>> mailItems;

    public MailPool(){

        this.mailItems = new HashMap<>();
=======
    private ArrayList<MailItem> mailItems;

    public MailPool(){

        this.mailItems = new ArrayList<>();
>>>>>>> Knapsack

    }

   @Override
    public void addToPool(MailItem mailItem) {

       System.out.println("Adding to the pool " + mailItem);
       int floor = mailItem.getDestFloor();
       ArrayList<MailItem> floorItems;
       if(this.mailItems.get(floor) == null) {
           floorItems= new ArrayList<>();
           floorItems.add(mailItem);
           this.mailItems.put(floor, floorItems);
       }
       else {
           floorItems = this.mailItems.get(floor);
           floorItems.add(mailItem);

       }
       return;
    }

<<<<<<< HEAD
    public int getNumItemsForFloor(Integer floor) {

        return this.mailItems.get(floor).size();
    }

    public MailItem getMailItem(int floor, int index) {
        return this.mailItems.get(floor).get(index);
=======
    public int getLength() {

        return mailItems.size();
    }

    public MailItem getMailItem(int index) {

        return this.mailItems.get(index);
>>>>>>> Knapsack
    }

    public boolean isEmptyPool() {

        return mailItems.isEmpty();
    }

    public void removeMailItem(MailItem mailItem) {
<<<<<<< HEAD
        int floor = mailItem.getDestFloor();
        this.mailItems.remove(floor mailItem);
=======

        this.mailItems.remove(mailItem);
        return;
    }

    public void sortByFloor() {

        FloorComparator comparator = new FloorComparator();

        Collections.sort(this.mailItems, comparator);

        printPool();

>>>>>>> Knapsack
        return;
    }

    public void printPool() {
        System.out.println("==============================");
        System.out.println("Result of sorting");
        for(MailItem mi : this.mailItems) {
            System.out.println(mi);

        }

        System.out.println("==============================");
    }


    public class FloorComparator implements Comparator<MailItem>
    {
        public FloorComparator() {

        }

        @Override
        public int compare(MailItem itemOne, MailItem itemTwo)
        {
            int floorOne = itemOne.getDestFloor();
            int floorTwo = itemTwo.getDestFloor();
            if (floorOne < floorTwo)
            {
                return -1;
            }
            if (floorTwo > floorOne)
            {
                return 1;
            }
            return 0;
        }
    }
}
