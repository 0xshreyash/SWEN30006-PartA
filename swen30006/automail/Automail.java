/**
 * Author: Shreyash Patodia
 * Student Number: 767336
 * Subject: SWEN30006 Software Modelling and Design.
 * Project: Assignment 1 (Part A)
 * Semester 1, 2017
 * */

/** Package name */
package automail;

/** Importing all classes from strategies */
import strategies.*;

/**
 * Automail is the class that that creates the robot, the mailPool
 * and the sorter. It is the class that uses the other classes in
 * the Automail by Robotic Mailing Solutions Inc. in order to provide
 * a complete product.
 */
public class Automail {

    public Robot robot;
    public IMailPool mailPool;

    Automail(IMailDelivery delivery) {

        /** CHANGE NOTHING ABOVE HERE */

        /** Initialize the MailPool */
        MailPool MailPool = new MailPool();
        mailPool = MailPool;

        /** Initialize the MailSorter */
        IMailSorter sorter = new MailSorter(MailPool);

        /** CHANGE NOTHING BELOW HERE */

        /** Initialize robot */
        robot = new Robot(sorter, delivery);

    }

}
