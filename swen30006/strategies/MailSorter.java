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
    private static int itemsDelivered = 0;

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
        int totalNumItems = mailPool.getLength();


        int referenceFloor = Building.MAILROOM_LOCATION;

        // Gives the index of the start location for a specific floor (or anything higher)
        // Returns -1 if this is not the case.
        int indexDivider = this.mailPool.getIndexForFloor(referenceFloor);

        //
        int numTopItems = totalNumItems - indexDivider;
        double valuesTop[][];
        double valuesBottom[][];
        double values[][];
        int startIndex = 0;
        if(indexDivider > -1) {
            valuesTop = Knapsack(indexDivider + 1, totalNumItems, maxCapacity);
            valuesBottom = Knapsack(1, indexDivider, maxCapacity);

            double [] lastTopRow = valuesTop[valuesTop.length - 1];
            double lastTopValue = lastTopRow[lastTopRow.length - 1];

            double [] lastBottomRow = valuesBottom[valuesBottom.length - 1];
            double lastBottomValue = lastBottomRow[lastBottomRow.length - 1];

            if(lastTopValue > lastBottomValue) {
                values = valuesTop;
                startIndex = indexDivider;
            }
            else {
                values = valuesBottom;
            }
        }
        else {
            values = Knapsack(1, totalNumItems, maxCapacity);
        }

        ArrayList<MailItem> itemsToAdd = determineItems(values, startIndex, values.length  - 1, maxCapacity);

        int count = 0;
        // Get rid of the try catch block. Needs to be gotten rid of. !!!!!!
        while(count < itemsToAdd.size()) {

            MailItem mi = itemsToAdd.get(count);
            System.out.println("Adding to the tube " + mi);



            try {
                tube.addItem(mi);
                mailPool.removeMailItem(mi);
                itemsDelivered ++;
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
            System.out.println("Filled the tube " + this.fillingTube);
            System.out.println("Items delivered being delivered " + this.itemsDelivered);
            return true;
        }
        return false;

    }


    // Start item is the number of the first item in the knapsack, last is the index of the last item.
    private double[][] Knapsack(int startItem, int lastItem, int maxCapacity) {


        double values[][]= new double[lastItem - startItem  + 2][maxCapacity + 1];
        int times[][] = new int[lastItem - startItem + 2][maxCapacity + 1];
        int locations[][] = new int[lastItem - startItem + 2][maxCapacity + 1];

        for(int itemTimeRow[] : times) {
            Arrays.fill(itemTimeRow, Clock.Time());
        }

        for(int itemLocationArray[] : locations) {
            Arrays.fill(itemLocationArray, Building.MAILROOM_LOCATION);
        }

        for(int col = 0; col <= maxCapacity; col++) {
            values[0][col] = 0;
        }

        for(int row = 0; row <= (lastItem - startItem  + 1); row++) {
            values[row][0] = 0;
        }

        for(int item = 1; item <= (lastItem - startItem + 1); item++) {
            for(int weight = 1; weight <= maxCapacity; weight++) {
                //System.out.println("StartItem = " + startItem + " item = " + item);
                MailItem currentItem = this.mailPool.getMailItem(startItem + item - 2);
                if(currentItem.getSize() > weight) {
                    values[item][weight] = values[item - 1][weight];
                }
                else {
                    double altScore = values[item - 1][weight - currentItem.getSize()] +
                            calculateDeliveryScore(currentItem, times[item - 1][weight - currentItem.getSize()],
                                    locations[item - 1][weight - currentItem.getSize()]);
                    double prevScore = values[item - 1][weight];
                    if(prevScore > altScore) {
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

        return values;
    }

    private ArrayList<MailItem> determineItems(double [][]values, int startIndex, int numItems, int maxCapacity) {
        int capacity = maxCapacity;
        int item = numItems;
        ArrayList<MailItem> itemsToAdd = new ArrayList<>();
        while(capacity > 0 && item > 0) {
            //System.out.println("item = " + item + " capacity = " + capacity);
            if(values[item][capacity] != values[item - 1][capacity]) {
                MailItem mailItem = mailPool.getMailItem(startIndex + item - 1);
                itemsToAdd.add(mailItem);
                capacity = capacity -  mailItem.getSize();
            }
            item = item - 1;
        }

        return itemsToAdd;

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


        double score =  ((Math.pow((simulationTime - deliveryItem.getArrivalTime() + 1), penalty)*(priority_weight)*scale)
                /(Math.pow((Math.abs(deliveryItem.getDestFloor() - referenceFloor) + 1), penalty)));

        return score;
    }
}
