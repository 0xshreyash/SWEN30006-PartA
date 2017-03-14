package strategies;


/** Remove the imports that are not used */
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import automail.Building;
import automail.Clock;
import automail.MailItem;
import automail.IMailPool;


/**
 * Sample of what a MailPool could look like.
 * This one tosses the incoming mail on a pile and takes the outgoing mail from the top.
 */
public class MailPool implements IMailPool {

    private HashMap<Integer, ArrayList<MailItem>> mailItems;

    public MailPool(){

        this.mailItems = new HashMap<>();

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

    public int getNumItemsForFloor(Integer floor) {

        return this.mailItems.get(floor).size();
    }

    public MailItem getMailItem(int floor, int index) {
        return this.mailItems.get(floor).get(index);
    }

    public boolean isEmptyPool() {
        return mailItems.isEmpty();
    }

    public void removeMailItem(MailItem mailItem) {
        int floor = mailItem.getDestFloor();
        this.mailItems.remove(floor mailItem);
        return;
    }
}
