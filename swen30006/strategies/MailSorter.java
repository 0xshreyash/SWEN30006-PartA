package strategies;

import automail.*;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.sun.xml.internal.rngom.dt.builtin.BuiltinDatatypeLibrary;
import com.sun.xml.internal.ws.api.pipe.Tube;
import exceptions.TubeFullException;

import java.util.PriorityQueue;

/**
 * A sample class for sorting mail:  this strategy just takes a MailItem
 * from the MailPool (if there is one) and attempts to add it to the Robot's storageTube.
 * If the MailItem doesn't fit, it will tell the robot to start delivering (return true).
 */
public class MailSorter implements IMailSorter{

    MailPool MailPool;
    private int maxFloorDifferenceUp;
    private int maxFloorDifferenceDown;
    private int maxFloorDifference;
    private boolean goUp;
    private boolean goDown;

    public MailSorter(MailPool MailPool) {
        this.MailPool = MailPool;
        this.maxFloorDifferenceUp = (Building.FLOORS - Building.MAILROOM_LOCATION);
        this.maxFloorDifferenceDown = (Building.MAILROOM_LOCATION - Building.LOWEST_FLOOR);
        goUp = false;
        goDown = false;
        if(maxFloorDifferenceUp > maxFloorDifferenceDown)
        {
            goUp =  true;
            this.maxFloorDifference = maxFloorDifferenceUp - maxFloorDifferenceDown;
        }
        else
        {
            goDown = true;
            this.maxFloorDifference = this.maxFloorDifferenceDown - this.maxFloorDifferenceUp;
        }

        System.out.println("Go up is:" + goUp + " Go down is:" + goDown);
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(InterruptedException e)
        {
        }


    }
    /**
     * Fills the storage tube
     */
    @Override
    public boolean fillStorageTube(StorageTube tube) {

        System.out.println("Hello running at " + Clock.Time());

        int floorDifferenceUp = 0;
        int floorDifferenceDown = 1;

        int count = 0;


        if(Clock.Time() > Clock.LAST_DELIVERY_TIME)
        {
            System.out.println("Over the last delivery time");
        }

        addToTube:
        while(!(tube.getTotalOfSizes() == tube.MAXIMUM_CAPACITY)  && count <= (this.maxFloorDifferenceUp)) {
            int floorAbove = Building.MAILROOM_LOCATION + floorDifferenceUp;
            int floorBelow = Building.MAILROOM_LOCATION - floorDifferenceDown;
            PriorityQueue<MailItem> itemsAbove;
            PriorityQueue<MailItem> itemsBelow;
            System.out.println("Running with floorAbove = " + floorAbove + " and floorBelow = " + floorBelow);
            if(goUp && floorAbove <= Building.FLOORS) {
                if ((itemsAbove = MailPool.getFloorMail(floorAbove)) != null && itemsAbove.size() != 0) {
                    System.out.println("Check out floor " + floorAbove);
                    try {
                        System.out.println("Adding items from floor above :");

                        Iterator<MailItem> iterator = itemsAbove.iterator();
                        System.out.println(itemsAbove);
                        while (iterator.hasNext()) {
                            MailItem mi;
                            System.out.println("Tube currently has:" + tube.getTotalOfSizes());

                            mi = iterator.next();
                            if ((tube.getTotalOfSizes() + mi.getSize()) <= tube.MAXIMUM_CAPACITY) {
                                tube.addItem(mi);
                                iterator.remove();
                            }
                            if (tube.getTotalOfSizes() == tube.MAXIMUM_CAPACITY) {
                                System.out.println("Tube is now full");

                                MailPool.isEmptyPool();
                                return true;
                            }
                        }

                    } catch (TubeFullException e) {
                        return true;
                    }
                }
            }

            if(goDown && floorBelow >= Building.LOWEST_FLOOR && floorBelow != floorAbove)
            {
                if ((itemsBelow = MailPool.getFloorMail(floorBelow)) != null && itemsBelow.size() != 0) {
                    try {
                        System.out.println("Adding items from floor below :");
                        Iterator<MailItem> iterator = itemsBelow.iterator();
                        while(iterator.hasNext()) {
                            MailItem mi = iterator.next();
                            if((tube.getTotalOfSizes() + mi.getSize()) <= tube.MAXIMUM_CAPACITY) {
                                tube.addItem(mi);
                                System.out.println(mi);
                                iterator.remove();
                            }
                            if(tube.getTotalOfSizes() == tube.MAXIMUM_CAPACITY)
                            {
                                System.out.println("Tube is now full");

                                MailPool.isEmptyPool();
                                return true;
                            }
                        }
                    } catch (TubeFullException e) {
                        return true;
                    }
                }
            }

            if(goUp && floorDifferenceUp == maxFloorDifferenceUp)
            {
                floorDifferenceUp = 0;
                goUp = false;
                goDown = true;
            }
            else if(goUp)
            {
                floorDifferenceUp++;
            }
            if(goDown && floorDifferenceDown == maxFloorDifferenceDown)
            {
                floorDifferenceDown = 0;
                goUp = true;
                goDown = false;
            }
            else if(goDown)
            {
                floorDifferenceDown++;
            }

            count++;
        }

        if(count >= this.maxFloorDifference && tube.getTotalOfSizes() > 0)
        {
            return true;

        }


        /*try{
            if (!simpleMailPool.isEmptyPool()) {*/
        /** Gets the first item from the ArrayList */
        //MailItem mailItem = simpleMailPool.get();
        /** Add the item to the tube */
        //tube.addItem(mailItem);
        /** Remove the item from the ArrayList */
        //simpleMailPool.remove();
        //}
        //}
        /** Refer to TubeFullException.java --
         *  Usage below illustrates need to handle this exception. However you should
         *  structure your code to avoid the need to catch this exception for normal operation
         */
        //catch(TubeFullException e){
        //return true;
        //}
        /**
         * Handles the case where the last delivery time has elapsed and there are no more
         * items to deliver.
         */
        //if(Clock.Time() > Clock.LAST_DELIVERY_TIME && simpleMailPool.isEmptyPool() && !tube.isEmpty()){
        // return true;
        //}
        //return false;*/



        if((Clock.Time() > Clock.LAST_DELIVERY_TIME && MailPool.isEmptyPool() && !tube.isEmpty())) {
            return true;
        }

        return false;

    }
}
