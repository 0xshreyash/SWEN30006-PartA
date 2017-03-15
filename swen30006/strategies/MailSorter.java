package strategies;

import automail.*;
import java.util.ArrayList;
import exceptions.TubeFullException;
import java.util.Arrays;


/**
 * A sample class for sorting mail:  this strategy just takes a MailItem
 * from the MailPool (if there is one) and attempts to add it to the Robot's storageTube.
 * If the MailItem doesn't fit, it will tell the robot to start delivering (return true).
 */
public class MailSorter implements IMailSorter{

    private MailPool mailPool;

    private static int WAITING_TO_DELIVERY_TIME = 1;
    private static int DELIVERY_TIME = 1;
    private static int fillingTube = 0;

    public MailSorter(MailPool mailPool) {

        this.mailPool = mailPool;
    }


    /**
     * Fills the storage tube
     */
    @Override
    public boolean fillStorageTube(StorageTube tube) {

        System.out.println("Hello! Filling tube at: " + Clock.Time());

        int maxCapacity = tube.MAXIMUM_CAPACITY;
        int numItems = mailPool.getLength();

        mailPool.sortByFloor();

        double values[][]= new double[numItems + 1][maxCapacity + 1];
        int times[][] = new int[numItems + 1][maxCapacity + 1];
        int locations[][] = new int[numItems + 1][maxCapacity + 1];
        int maxFloor[][] = new int[numItems + 1][maxCapacity + 1];
        int minFloor[][] = new int[numItems + 1][maxCapacity + 1];

        for(int itemTimeRow[] : times) {
            Arrays.fill(itemTimeRow, Clock.Time());


        }

        for(int itemLocationArray[] : locations) {
            Arrays.fill(itemLocationArray, Building.MAILROOM_LOCATION);
        }


        for(int col = 0; col < maxCapacity; col++) {

            values[0][col] = 0;

        }

        for(int row = 0; row < numItems; row++) {
            values[row][0] = 0;
        }

        ArrayList<MailItem> itemsToAdd = new ArrayList<>();

        for(int item = 1; item <= numItems; item++) {

            for(int weight = 1; weight <= maxCapacity; weight++) {

                MailItem currentItem = mailPool.getMailItem(item - 1);
                if(currentItem.getSize() > weight) {
                    values[item][weight] = values[item - 1][weight];
                }
                else {

                    double altScore = values[item - 1][weight - currentItem.getSize()] + calculateDeliveryScore(currentItem, times[item - 1][weight - currentItem.getSize()], locations[item - 1][weight - currentItem.getSize()]);
                    double prevScore = values[item - 1][weight];

                    if(prevScore > altScore)
                    {
                        values[item][weight] = prevScore;
                        times[item][weight] = times[item - 1][weight];
                        locations[item][weight] = locations[item - 1][weight];

                    }
                    else {

                        values[item][weight] = altScore;
                        times[item][weight] = times[item - 1][weight]  + currentItem.getSize();
                        locations[item][weight] = currentItem.getDestFloor();
                    }



                }
            }

        }

        int item = numItems;
        int capacity = maxCapacity;

        while(capacity > 0 && item > 0) {
            if(values[item][capacity] != values[item - 1][capacity]) {
                MailItem mailItem = mailPool.getMailItem(item - 1);
                itemsToAdd.add(mailItem);
                capacity = capacity -  mailItem.getSize();
            }
            item = item - 1;
        }
        int count = 0;


        while(count < itemsToAdd.size()) {

            MailItem mi = itemsToAdd.get(count);
            System.out.println("Adding to the tube " + mi);


            mailPool.removeMailItem(mi);
            try {

                tube.addItem(mi);
            } catch (TubeFullException e) {
                System.out.println("Knapsack caused tube to overflow");
                this.fillingTube ++;
                System.out.println(this.fillingTube);
                return true;
            }

            count++;
        }
        System.out.println("==============================");
        if(!tube.isEmpty()) {
            this.fillingTube++;
            System.out.println(this.fillingTube);
            return true;
        }

        return false;

    }

    private static double calculateDeliveryScore(MailItem deliveryItem, int simulationTime, int referenceFloor) {



        // Penalty for longer delivery times
        final double penalty = 1.1;
        // Take (delivery time - arrivalTime)**penalty * priority_weight
        double priority_weight = 0;
        double scale = 10.0;
            // Determine the priority_weight

        switch(deliveryItem.getPriorityLevel()) {
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

        double score =  ((Math.pow((simulationTime - deliveryItem.getArrivalTime() + 1), penalty)*(priority_weight + 1))
                /(Math.abs(deliveryItem.getDestFloor() - referenceFloor) + 1));
        return score;
    }
}
