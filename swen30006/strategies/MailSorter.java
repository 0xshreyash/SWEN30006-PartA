/**
 * Author: Shreyash Patodia
 * Student Number: 767336
 * Subject: SWEN30006 Software Modelling and Design.
 * Project: Assignment 1 (Part A)
 * Semester 1, 2017
 * */

/** Package name is strategies */
package strategies;

/** Import all classes from automail */
import automail.*;

/** Importing relevant classes from java libraries */
import java.util.ArrayList;
import exceptions.TubeFullException;
import java.util.Arrays;


/**
 * A MailSorter class that implements Knapsack in order to find the maximum value items that
 * the robot can deliver at each trip. The class implements 2 separate knapsacks for the floors
 * that are below the mail room floors and the rest of the floors repsectively. I did this
 * because the it does not make sense for us to cross the mail room floor without actually
 * stopping at it and collecting more items.
 */
public class MailSorter implements IMailSorter{

    /**
     * The pool of the mail items
     * */
    private MailPool mailPool;


    /**
     * Constructor that tells the sorter which mailPool it is to sort.
     * @param mailPool the mailPool to take items from, and then get the robot to deliver
     * items from.
     */
    public MailSorter(MailPool mailPool) {

        this.mailPool = mailPool;
    }

    /**
     * Function that is called in order to fill the storage tube of the robot in with the
     * highest priority items so that the score can be minimized.
     * @param tube the storage tube to be filled by the sorting algorithm.
     */
    /* 18 LOC */
    @Override
    public boolean fillStorageTube(StorageTube tube) {

        // System.out.println("Hello! Filling tube at: " + Clock.Time());

        /* Capacity of the tube */
        int maxCapacity = tube.MAXIMUM_CAPACITY;

        /* Total items in the mail pool */
        int totalNumItems = mailPool.getLength();

        /* The floor we are on, by default the mail room floor */
        int referenceFloor = Building.MAILROOM_LOCATION;

        /* The index of the first item with floor greater than or equal to the referenceFloor
         * getIndexForFloor sorts the mailItems so that we can iterate through the mailItems
         * in an ordered manner, also keeps the control of the mailItems to the pool and not
         * to the sorter.
         */
        int indexDivider = this.mailPool.getIndexForFloor(referenceFloor);

        /* These are the items to add to the tube */
        ArrayList<MailItem> itemsToAdd = chooseKnapsackValues( indexDivider, totalNumItems, maxCapacity);

        int count = 0;

        // Get rid of the try catch block. Needs to be gotten rid of. !!!!!!
        /* Adding items to the tube */
        while(count < itemsToAdd.size()) {

            MailItem mi = itemsToAdd.get(count);

            try {
                this.mailPool.removeMailItem(mi);
                tube.addItem(mi);

            } catch (TubeFullException e) {
                return true;
            }
            count++;
        }

        /* Send robot if tube is not empty i.e. Knapsack gives the current best possible values */
        if(!tube.isEmpty()) {

            return true;
        }

        return false;
    }

    /** This function calculates the performs Knapsack algorithm for the items from startItem
     *  (index: startIndex - 1) to the last item. It finds the best value based on the
     *  calculateDeliveryScore function.
     *  I used numbers of the items since that is conventional when doing Knapsack since 0th
     *  rows and columns are occupied by the base cases.
     * @param startItem the number (not index) of the start item
     * @param lastItem the number (not index) of the last item
     * @param maxCapacity the max weight that the knapsack can hold
     * @return a 2D array of values in the Knapsack.
     */
     /* Function looks long due to lots of commenting.
        28 LOC */
    private double[][] Knapsack(int startItem, int lastItem, int maxCapacity) {

        /* + 1 to have the number of items */
        int numItems = lastItem - startItem + 1;

        /* + 1 on either dimension for the base case i.e. 0 weight knapsack and 0 items */
        double values[][]= new double[numItems + 1][maxCapacity + 1];
        int times[][] = new int[numItems + 1][maxCapacity + 1];
        int locations[][] = new int[numItems + 1][maxCapacity + 1];

        /* Initializing the 0th row & 0th column of values with 0, times with the current time,
           locations with the mailRoom location, since that is the current start of the robot
           when we start selecting the items to be put into the knapsack.
         */
        initialiseKnapsackArrays(values, times, locations, maxCapacity, numItems);

        /* Running knapsack */
        for(int item = 1; item <= numItems; item++) {
            for(int weight = 1; weight <= maxCapacity; weight++) {

                /* index of startItem = (startItem - 1), index of item number 1 is 0 so,
                   we need to add startItem - 1 + item - 1 to get the index of the current
                   item.
                 */
                MailItem currentItem = this.mailPool.getMailItem((startItem - 1) + (item - 1));

                /* If the new item can't fit into the weight just use the value from the top row */
                if(currentItem.getSize() > weight) {

                    values[item][weight] = values[item - 1][weight];
                }
                else {

                    /* If item can be fit into the bag, then calculate the value for that item using
                       calculateDeliveryScore and then add to the best value for the remaining space.
                       This gives us the alternate score
                     */
                    double altScore = values[item - 1][weight - currentItem.getSize()] +
                            calculateDeliveryScore(currentItem, times[item][weight - currentItem.getSize()],
                                    locations[item - 1][weight - currentItem.getSize()]);

                    /* If the alternate score is better than the score on the row above i.e. if
                       the current item was not included then have value, time and location reflect that
                       otherwise just copy values from the prev row
                     */
                    if(values[item - 1][weight] >= altScore) {
                        copyPrevValues(values, times, locations, item, weight);
                    }
                    else {
                        /* New time is current time + time to deliver item which is proportional to
                           the floor of the item i.e. higher the floor more time added, + 1 to say that at least one
                           second is need to deliver the item even if the floor is the same as the current floor.
                           (+1 represents the change of state time for the robot in our simulation)
                         */
                        times[item][weight] = times[item - 1][weight - currentItem.getSize()] +
                                currentItem.getDestFloor() + 1;
                        values[item][weight] = altScore;
                        locations[item][weight] = currentItem.getDestFloor();
                    }
                }
            }
        }
        return values;
    }

    private void copyPrevValues(double values[][], int times[][],
                               int locations[][], int item, int weight) {

        values[item][weight] = values[item - 1][weight];
        times[item][weight] = times[item - 1][weight];
        locations[item][weight] = locations[item - 1][weight];

    }
    private void initialiseKnapsackArrays(double values[][], int times[][],
                                          int locations[][], int maxCapacity, int numItems) {

        for(int itemTimeRow[] : times) {
            Arrays.fill(itemTimeRow, Clock.Time());
        }

        for(int itemLocationArray[] : locations) {
            Arrays.fill(itemLocationArray, Building.MAILROOM_LOCATION);
        }

        for(int col = 0; col <= maxCapacity; col++) {
            values[0][col] = 0;
        }

        for(int row = 0; row <= numItems; row++) {
            values[row][0] = 0;
        }

        return;

    }

    /** The mail sorter runs knapsack twice, once for the Mailroom floor and above and once for all floors below
     *  in this way the complexity of the program is reduced whenever the mailroom is not on the top or bottom
     *  floor and allows  assertion of the fact that the robot should not be crossing the mailroom floor without
     *  filling his knapsack.
     * @param indexDivider The location of the first item with floor >= mailroom floor in sorted mailPool.
     * @param totalNumItems Total number of items in the pool.
     * @param maxCapacity The max weight the robot can carry at once.
     * @return The arrayList of mail items to be added to the tube.
     */
    private ArrayList<MailItem> chooseKnapsackValues( int indexDivider, int totalNumItems, int maxCapacity) {

        double valuesTop[][];
        double valuesBottom[][];
        int startIndex = 0;
        double values[][];

        /* indexDivider = -1 means that there are no items with floor >= mailRoom floor */
        if(indexDivider > -1) {
            /* Here we have items with delivery on either side of the mailRoom floor */
            /* values for the top and bottom Knapsack solution */
            /* We are passing the item number starting at 1 (not the index of the item) */
            valuesTop = Knapsack(indexDivider + 1, totalNumItems, maxCapacity);
            valuesBottom = Knapsack(1, indexDivider, maxCapacity);

            /* Find the highest value for to top knapsack */
            /* length - 1 since we want the value at the end i.e. the value at the end of knapsack */
            double [] lastTopRow = valuesTop[valuesTop.length - 1];
            double lastTopValue = lastTopRow[lastTopRow.length - 1];

            /* Find the highest value for the bottom knapsack */
            /* length - 1 since we want the value at the end i.e. the value at the end of knapsack */
            double [] lastBottomRow = valuesBottom[valuesBottom.length - 1];
            double lastBottomValue = lastBottomRow[lastBottomRow.length - 1];

            /* Take the top floors if they have higher value else take the bottom floors */
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

        /* Determine the items to be delivered and return them */
        return determineItems(values, startIndex, values.length  - 1, maxCapacity);

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

    /**
     * Function takes the a mailItem, the current time in the simulation (an overestimate) and a reference floor i.e.
     * the floor the robot was at when considering whether to deliver the mail item passed as parameter so that we
     * can measure the relative distance of the floors, to make sure the robot doesn't have to travel large distances.
     * If the robot ends up travelling too far then it will take it too long to come back when we could have just
     * delivered something else (thus, the distance is factored into the score).
     * @param deliveryItem the item being considered to be delivered.
     * @param simulationTime the overestimated time in the simulation.
     * @param referenceFloor the floor the robot is at when considering the deliveryItem.
     * @return the score of the item, higher means the item is more likely to be selected.
     */
    private double calculateDeliveryScore(MailItem deliveryItem, int simulationTime, int referenceFloor) {

        // Penalty for longer delivery times
        final double penalty = 1.2;
        // Take (delivery time - arrivalTime)**penalty * priority_weight
        double priority_weight = 0.1;
        // double priority_additive_value = 0.1;
        double scale = 10.0;
            // Determine the priority_weight

        switch(deliveryItem.getPriorityLevel()) {
            case "LOW":
                priority_weight = 1;
                break;
            case "MEDIUM":
                priority_weight = 1.6;
                break;
            case "HIGH":
                priority_weight = 2;
                break;
        }

        double numerator = (simulationTime - deliveryItem.getArrivalTime() + Math.pow(priority_weight, 2));

        double denominator = (Math.abs(deliveryItem.getDestFloor() - referenceFloor) + 1);

        double score =  ((Math.pow(numerator, penalty)*(priority_weight))
                /(Math.pow(denominator*penalty, penalty*penalty) - 1));
        // System.out.println(score);

        return score;
    }
}
