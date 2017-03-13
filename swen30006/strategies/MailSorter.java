package strategies;

import automail.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.sun.xml.internal.rngom.dt.builtin.BuiltinDatatypeLibrary;
import com.sun.xml.internal.ws.api.pipe.Tube;
import exceptions.TubeFullException;
import sun.misc.ConditionLock;

import java.util.PriorityQueue;

/**
 * A sample class for sorting mail:  this strategy just takes a MailItem
 * from the MailPool (if there is one) and attempts to add it to the Robot's storageTube.
 * If the MailItem doesn't fit, it will tell the robot to start delivering (return true).
 */
public class MailSorter implements IMailSorter{

    MailPool MailPool;
    private static int WAITING_TO_DELIVERY_TIME = 1;
    private static int DELIVERY_TIME = 1;
    public MailSorter(MailPool MailPool) {

        this.MailPool = MailPool;
    }
    /**
     * Fills the storage tube
     */
    @Override
    public boolean fillStorageTube(StorageTube tube) {

        System.out.println("Hello! Filling tube at: " + Clock.Time());

        int maxCapacity = tube.MAXIMUM_CAPACITY;
        int numItems = MailPool.getLength();

        double values[][]= new double[numItems + 1][maxCapacity + 1];

        for(int col = 0; col < maxCapacity; col++) {

            values[0][col] = 0;

        }

        for(int row = 0; row < numItems; row++) {
            values[row][0] = 0;
        }
        ArrayList<MailItem> itemsToAdd = new ArrayList<>();
        ArrayList<MailItem> mailItems = MailPool.getMailItems();
        for(int item = 1; item <= numItems; item++) {

            for(int weight = 1; weight <= maxCapacity; weight++) {

                MailItem currentItem = mailItems.get(item - 1);
                if(currentItem.getSize() > weight) {
                    values[item][weight] = values[item - 1][weight];
                }
                else {

                    values[item][weight] = Math.max(values[item - 1][weight],
                            values[item - 1][weight - currentItem.getSize()]
                                    + calculateDeliveryScore(currentItem));


                }
            }

        }

        int item = numItems;
        int capacity = maxCapacity;

        while(capacity > 0 && item > 0) {
            if(values[item][capacity] != values[item - 1][capacity]) {
                MailItem mailItem = mailItems.get(item - 1);
                itemsToAdd.add(mailItem);
                capacity = capacity -  mailItem.getSize();
            }
            item = item - 1;
        }
        int count = 0;
        while(count < itemsToAdd.size()) {

            MailItem mi = itemsToAdd.get(count);
            System.out.println("Adding to the tube " + mi);

            mailItems.remove(mi);
            try {
                tube.addItem(mi);
            } catch (TubeFullException e) {
                System.out.println("Knapsack caused tube to be ");
                return true;
            }

            count++;
        }
        if(!tube.isEmpty())
            return true;

        return false;

    }

    private static double calculateDeliveryScore(MailItem deliveryItem) {



        // Penalty for longer delivery times
        final double penalty = 1.1;
        // Take (delivery time - arrivalTime)**penalty * priority_weight
        double priority_weight = 0;
        double max_priority = 2;
            // Determine the priority_weight

        switch(deliveryItem.getPriorityLevel()) {
            case "LOW":
                priority_weight = 1;
                break;
            case "MEDIUM":
                priority_weight = 2;
                break;
            case "HIGH":
                priority_weight = 3;
                break;
        }

        double score = Math.pow(Clock.Time() - deliveryItem.getArrivalTime() + 1, penalty)*(priority_weight) -
                Math.abs(deliveryItem.getDestFloor() - Building.MAILROOM_LOCATION)*(max_priority - priority_weight);
        return score;
    }
}
