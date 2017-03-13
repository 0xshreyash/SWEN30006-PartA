package strategies;


/** Remove the imports that are not used */
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

    private ArrayList<MailItem> mailItems;
    private int FRONT;

    public MailPool(){

        this.mailItems = new ArrayList<>();
        this.FRONT = 0;

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

    public ArrayList<MailItem> getMailItems() {
        return this.mailItems;
    }

    public boolean isEmptyPool() {
        return mailItems.isEmpty();
    }

    public MailItem getMailItem()
    {

        return this.mailItems.get(this.FRONT);
    }

    public void removeMailItem(MailItem mailItem) {
        this.mailItems.remove(mailItem);
        return;
    }
}
