/**
 * Author: Shreyash Patodia
 * Student Number: 767336
 * Subject: SWEN30006 Software Modelling and Design.
 * Project: Assignment 1 (Part A)
 * Semester 1, 2017
 * */

/** Package name */
package strategies;


/** Importing relevant classes from automail */
import automail.MailItem;
import automail.IMailPool;

/** Importing java libraries */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Class contains all the mailItems delivered to the building. The class initially chucks all the
 * mail into an ArrayList (due to easy access and insertion) and sorts the mail according to the
 * floor when the robot comes to take some mail to deliver.
 */
public class MailPool implements IMailPool {


    /**
     * List of the items in the Mail Pool
     * */
    private ArrayList<MailItem> mailItems;

    /**
     * Constructor that initializes mailItems to an empty ArrayList.
     */
    public MailPool(){

        this.mailItems = new ArrayList<>();

    }

    /**
     * Takes a new mail item as parameter and adds it to the ArrayList of already existing items.
     * @param mailItem the mail item being added to the pool.
     */
    @Override
    public void addToPool(MailItem mailItem) {

        System.out.println("Adding to the pool " + mailItem);
        mailItems.add(mailItem);
        return;
    }

    /**
     * Fidns the length of the mailItems ArrayList.
     * @return current length of the mailItems ArrayList.
     */
    public int getLength() {

        return mailItems.size();
    }

    /**
     * Allows user to get the mailItem at a certain index. In this way we can iterate through
     * the mailItems without having to pass all the items to the MailSorter.
     * @param index the index of the mail item to find from the pool.
     * @return the mail item at the given index.
     */
    public MailItem getMailItem(int index) {

        return this.mailItems.get(index);
    }

    /**
     * Checks if the pool is empty
     * @return boolean value indicating whether the pool is empty.
     */
    public boolean isEmptyPool() {

        return mailItems.isEmpty();
    }

    /**
     * Removes the mail item provided as parameter.
     * @param mailItem the mail item to be removed.
     */
    public void removeMailItem(MailItem mailItem) {


        this.mailItems.remove(mailItem);
        return;
    }

    /**
     * Sorts the list of mail items by their floor (in ascending order).
     */
    private void sortByFloor() {

        FloorComparator comparator = new FloorComparator();

        Collections.sort(this.mailItems, comparator);

        printPool();

        return;
    }

    /**
     * Prints the whole list of mailItems, used mainly for debugging.
     */
    public void printPool() {
        System.out.println("==============================");
        //System.out.println("Result of sorting");
        for(MailItem mi : this.mailItems) {
            System.out.println(mi);

        }

        System.out.println("==============================");
    }

    /**
     * Sorts the list, and finds the index of the first item with floor greater
     * than or equal to the floor in the parameter.
     * @param referenceFloor the floor that we need to find the first item of.
     * @return index of the first item with floor greater than or equal to the
     * parameter floor.  -1 otherwise.
     */
    public int getIndexForFloor(int referenceFloor) {

        /* Calling sort here because this is the time we would need the items in
         * a sorted order, and thus, don't ned to do the extra work of sorting
         * items as they come in.
         */
        this.sortByFloor();

        for(MailItem mailItem : this.mailItems) {
            if(mailItem.getDestFloor() >= referenceFloor)
            {
                return this.mailItems.indexOf(mailItem);
            }
        }

        return -1;
    }

    /**
     * Comparator that is used in order to sort the floor, so that we can sort the
     * mail items by the floor.
     */
    public class FloorComparator implements Comparator<MailItem>
    {

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
