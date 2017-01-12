package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.*;

public class Scout extends BotState {

    static Direction[] closestBorderDirection = new Direction[2];

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
            Util.initCenter();
            Util.initBorders();
            initClosestBorders();
        } catch (Exception e){
            Debug.out("Init Exception");
            e.printStackTrace();
        }
    }

    private static void act() {
        try {
            if(!isBorderDetected())
                estimateBorders();

            Util.updateBorders();
            Nav.move(Nav.getMoveDirection(center));

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

    private static void detectArchons() {

    }

    private static void estimateBorders(){
        for (Direction direction : closestBorderDirection) {
            if (!estimateBorder(direction)) break;
        }

        state = State.NONE;
        Signal.broadcastSignal(Signal.BORDER, Signal.DETECTED);
    }

    private static boolean estimateBorder(Direction borderDirection) {
        try {
            if(borderDirection.radians == Direction.getNorth().radians|| borderDirection.radians == Direction.getSouth().radians){
                if(isBorderYDetected()) return true;
            } else {
                if(isBorderXDetected()) return true;
            }

            if(rc.hasMoved())
                return false;

            if(borderDirection.radians%Math.PI == 0)
                state = State.DETECTING_BORDER_X;
            else
                state = State.DETECTING_BORDER_Y;

            if(!Nav.move(Nav.getMoveDirection(myLocation.add(borderDirection)))) {
                if(borderDirection.radians == Direction.getWest().radians) {
                    Debug.out("West Map: " + (myLocation.x - myBodyRadius));
                    border[0] = (int) Math.ceil(myLocation.x - myBodyRadius);
                    border[2] = (int) Math.floor(center.x + (center.x - border[0]));
                    int[] data = {border[0], border[2]};
                    Signal.broadcastCoordinate(Signal.DATA_CHANNEL_X | Signal.NORTH_WEST, Signal.DATA_CHANNEL_X | Signal.SOUTH_EAST, data);
                }
                if(borderDirection.radians == Direction.getNorth().radians) {
                    Debug.out("North Map: " + (myLocation.y + myBodyRadius));
                    border[1] = (int) Math.floor(myLocation.y + myBodyRadius);
                    border[3] = (int) Math.ceil(center.y - (border[1] - center.y));
                    int[] data = {border[1], border[3]};
                    Signal.broadcastCoordinate(Signal.DATA_CHANNEL_Y | Signal.NORTH_WEST, Signal.DATA_CHANNEL_Y | Signal.SOUTH_EAST, data);
                }
                if(borderDirection.radians == Direction.getEast().radians) {
                    Debug.out("East Map: " + (myLocation.x + myBodyRadius));
                    border[2] = (int) Math.floor(myLocation.x + myBodyRadius);
                    border[0] = (int) Math.ceil(center.x - (border[2] - center.x));
                    int[] data = {border[0], border[2]};
                    Signal.broadcastCoordinate(Signal.DATA_CHANNEL_X | Signal.NORTH_WEST, Signal.DATA_CHANNEL_X | Signal.SOUTH_EAST, data);
                }
                if(borderDirection.radians == Direction.getSouth().radians) {
                    Debug.out("South Map: " + (myLocation.y - myBodyRadius));
                    border[3] = (int) Math.ceil(myLocation.y - myBodyRadius);
                    border[1] = (int) Math.floor(center.y + (center.y - border[3]));
                    int[] data = {border[1], border[3]};
                    Signal.broadcastCoordinate(Signal.DATA_CHANNEL_Y | Signal.NORTH_WEST, Signal.DATA_CHANNEL_Y | Signal.SOUTH_EAST, data);
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