package strategies;


/** Remove the imports that are not used */
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

    private ArrayList<MailItem> mailItems;

    public MailPool(){

        this.mailItems = new ArrayList<>();

    }

   @Override
    public void addToPool(MailItem mailItem) {

       System.out.println("Adding to the pool " + mailItem);
       mailItems.add(mailItem);
       return;
    }

    public int getLength() {

        return mailItems.size();
    }

    public MailItem getMailItem(int index) {

        return this.mailItems.get(index);
    }

    public boolean isEmptyPool() {

        return mailItems.isEmpty();
    }

    public void removeMailItem(MailItem mailItem) {

        this.mailItems.remove(mailItem);
        return;
    }

    public void sortByFloor(int referenceFloor) {

        FloorComparator comparator = new FloorComparator(referenceFloor);

        Collections.sort(this.mailItems, comparator);

        printPool();

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
        private int referenceFloor;
        public FloorComparator(int referenceFloor) {
            this.referenceFloor = referenceFloor;

        }
        @Override
        public int compare(MailItem one, MailItem two)
        {
            int floorOneDiff = one.getDestFloor() - this.referenceFloor;
            int floorTwoDiff = two.getDestFloor() - this.referenceFloor;
            int absfloorOneDiff = Math.abs(floorOneDiff);
            int absfloorTwoDiff = Math.abs(floorTwoDiff);

            if ((absfloorOneDiff < absfloorTwoDiff) ||
                    ((absfloorOneDiff == absfloorTwoDiff) && floorOneDiff > floorTwoDiff))
            {
                return -1;
            }
            if ((absfloorOneDiff > absfloorTwoDiff) ||
                    ((absfloorOneDiff == absfloorTwoDiff) && floorOneDiff < floorTwoDiff))
            {
                return 1;
            }
            return 0;
        }
    }
}
