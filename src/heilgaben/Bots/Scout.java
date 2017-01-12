package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.Debug;
import heilgaben.BotState;
import heilgaben.Nav;
import heilgaben.Signal;

public class Scout extends BotState {

    static Direction[] closestBorderDirection = new Direction[2];
    static int[] border = {-1, -1, -1, -1};

    public static void run() throws GameActionException {
        /* Scout specific init */
        init();

        while (true) {
            try {
                /* Update State */
                BotState.update();

                /* Round Actions */
                act();

                /* Yield round */
                Clock.yield();

            } catch (Exception e) {
                Debug.out("Scout Exception");
                e.printStackTrace();
            }
        }
    }

    private static void init() {
        try {
            initBorders();
            initClosestBorders();
        } catch (Exception e){
            Debug.out("Init Exception");
            e.printStackTrace();
        }
    }

    private static void act() {
        try {
            Debug.out("Borders: (" + border[0] + ", " + border[1] + ") - (" + border[2] + ", " + border[3] + ")");
            if(!isBorderDetected()) {
                detectBorders();
            }
        } catch (Exception e){
            Debug.out("Act Exception");
            e.printStackTrace();
        }
    }

    private static void initClosestBorders() {
        Direction closestX;
        Direction closestY;
        if(myLocation.x < center.x)
            closestX = Direction.getWest();
        else
            closestX = Direction.getEast();

        if(myLocation.y > center.y)
            closestY = Direction.getNorth();
        else
            closestY = Direction.getSouth();

        closestBorderDirection[0] = closestX;
        closestBorderDirection[1] = closestY;
    }

    private static void initBorders() {
        if(Signal.receiveReport(Signal.BORDER) == Signal.DETECTED) {
            int[] northWest = Signal.receiveData(Signal.DATA_CHANNEL_X | Signal.NORTH_WEST, Signal.DATA_CHANNEL_Y | Signal.NORTH_WEST);
            int[] southEast = Signal.receiveData(Signal.DATA_CHANNEL_X | Signal.SOUTH_EAST, Signal.DATA_CHANNEL_Y | Signal.SOUTH_EAST);
            border[0] = northWest[0];
            border[1] = northWest[1];
            border[2] = southEast[0];
            border[3] = southEast[1];
        }
    }

    private static void detectBorders(){
        for (Direction direction : closestBorderDirection) {
            if (!detectBorder(direction)) break;
        }

        Signal.broadcastReport(Signal.BORDER, Signal.DETECTED);
    }

    private static boolean detectBorder(Direction borderDirection) {
        try {
            if(borderDirection.radians == Direction.getNorth().radians|| borderDirection.radians == Direction.getSouth().radians){
                if(isBorderYDetected()) return true;
            } else {
                if(isBorderXDetected()) return true;
            }

            if(rc.hasMoved())
                return false;

            float stride = Nav.getMaxMoveDistance(borderDirection);
            if(rc.canMove(borderDirection, stride)) {
                rc.move(borderDirection, stride);
            }
            else {
                if(borderDirection.radians == Direction.getWest().radians) {
                    Debug.out("West Map: " + (myLocation.x - myBodyRadius));
                    border[0] = (int) Math.ceil(myLocation.x - myBodyRadius);
                    border[2] = (int) ((center.x - border[0]) + center.x);
                    int[] data = {border[0], border[2]};
                    Signal.broadcastData(Signal.DATA_CHANNEL_X | Signal.NORTH_WEST, Signal.DATA_CHANNEL_X | Signal.SOUTH_EAST, data);
                }
                if(borderDirection.radians == Direction.getNorth().radians) {
                    Debug.out("North Map: " + (myLocation.y - myBodyRadius));
                    border[1] = (int) Math.ceil(myLocation.y - myBodyRadius);
                    border[3] = (int) ((center.y - border[1]) + center.y);
                    int[] data = {border[1], border[3]};
                    Signal.broadcastData(Signal.DATA_CHANNEL_Y | Signal.NORTH_WEST, Signal.DATA_CHANNEL_Y | Signal.SOUTH_EAST, data);
                }
                if(borderDirection.radians == Direction.getEast().radians) {
                    Debug.out("East Map: " + (myLocation.x + myBodyRadius));
                    border[2] = (int) Math.floor(myLocation.x + myBodyRadius);
                    border[0] = (int) (border[2] - (2 * (border[2] - center.x)));
                    int[] data = {border[0], border[2]};
                    Signal.broadcastData(Signal.DATA_CHANNEL_X | Signal.SOUTH_EAST, Signal.DATA_CHANNEL_X | Signal.NORTH_WEST, data);
                }
                if(borderDirection.radians == Direction.getSouth().radians) {
                    Debug.out("South Map: " + (myLocation.y + myBodyRadius));
                    border[3] = (int) Math.floor(myLocation.y + myBodyRadius);
                    border[1] = (int) (border[3] - (2 * (border[3] - center.y)));
                    int[] data = {border[3], border[1]};
                    Signal.broadcastData(Signal.DATA_CHANNEL_Y | Signal.SOUTH_EAST, Signal.DATA_CHANNEL_Y | Signal.NORTH_WEST, data);
                }
            }
        } catch (Exception e) {
            Debug.out("Detect Border Exception");
            e.printStackTrace();
        }

        return false;
    }

    private static boolean isBorderDetected(){
        return isBorderXDetected() && isBorderYDetected();
    }
    private static boolean isBorderXDetected(){
        return border[0] > -1 && border[2] > -1;
    }
    private static boolean isBorderYDetected(){
        return border[1] > -1 && border[3] > -1;
    }
}