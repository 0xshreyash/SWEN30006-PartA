package strategies;

import automail.*;
import java.util.Iterator;
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
    private int maxFloorDifference;
    public MailSorter(MailPool MailPool) {
        this.MailPool = MailPool;
        this.maxFloorDifference = (Building.FLOORS - Building.MAILROOM_LOCATION)>
                                    (Building.MAILROOM_LOCATION - Building.LOWEST_FLOOR)?
                                    (Building.FLOORS - Building.MAILROOM_LOCATION):
                                    (Building.MAILROOM_LOCATION - Building.LOWEST_FLOOR);
    }
    /**
     * Fills the storage tube
     */
    @Override
    public boolean fillStorageTube(StorageTube tube) {

        System.out.println("Hello running at " + Clock.Time());

        int floorDifference = 0;
        int count = 0;


        if(Clock.Time() > Clock.LAST_DELIVERY_TIME)
        {
            System.out.println("Over the last delivery time");
        }

        addToTube:
        while(!(tube.getTotalOfSizes() == tube.MAXIMUM_CAPACITY)  && count <= this.maxFloorDifference) {
            int floorAbove = Building.MAILROOM_LOCATION + floorDifference;
            int floorBelow = Building.MAILROOM_LOCATION - floorDifference;
            PriorityQueue<MailItem> itemsAbove;
            PriorityQueue<MailItem> itemsBelow;
            System.out.println("Running with floorAbove = " + floorAbove + " and floorBelow = " + floorBelow);
            if(floorAbove <= Building.FLOORS)
            {
                if ((itemsAbove = MailPool.getFloorMail(floorAbove)) != null && itemsAbove.size() != 0)
                {
                    System.out.println("Check out floor " + floorAbove);
                    try{
                        System.out.println("Adding items from floor above :");
                        Iterator<MailItem> iterator = itemsAbove.iterator();
                        while(iterator.hasNext()) {
                            MailItem mi = iterator.next();
                            if ((tube.getTotalOfSizes() + mi.getSize()) <= tube.MAXIMUM_CAPACITY) {
                                tube.addItem(mi);
                                MailPool.removeItem(floorAbove, mi);
                            }
                            if (tube.getTotalOfSizes() == tube.MAXIMUM_CAPACITY) {
                                System.out.println("Tube is now full");

                                MailPool.isEmptyPool();
                                return true;
                            }
                        }

                    }
                    catch (TubeFullException e)
                    {
                        return true;
                    }

                }
            }

            if(floorBelow >= Building.LOWEST_FLOOR && floorBelow != floorAbove)
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
                                itemsBelow.remove(mi);
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
            count++;
            floorDifference ++;
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
