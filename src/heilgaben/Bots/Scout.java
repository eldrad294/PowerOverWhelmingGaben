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
            Util.updateBorders();
            if(!Signal.isBorderDetected())
                estimateBorders();
            else
                Debug.drawMapBorder();
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
        if(!Signal.isBorderXDetected())
            estimateBorder(closestBorderDirection[0]);
        else if(!Signal.isBorderYDetected())
            estimateBorder(closestBorderDirection[1]);
        else {
            state = State.NONE;
            Signal.broadcastSignal(Signal.BORDER, Signal.DETECTED);
        }
    }

    private static boolean estimateBorder(Direction borderDirection) {
        try {
            if(rc.hasMoved())
                return false;

            if(borderDirection.radians%Math.PI == 0)
                state = State.DETECTING_BORDER_X;
            else
                state = State.DETECTING_BORDER_Y;

            if(!Nav.move(Nav.getMoveDirection(myLocation.add(borderDirection)))) {
                if(borderDirection.radians == Direction.getWest().radians) {
                    border[0] = myLocation.x - myBodyRadius;
                    border[2] = center.x + (center.x - border[0]);

                    float[] data = {border[0], border[2]};

                    Signal.broadcastCoordinate(Signal.DATA_CHANNEL_X | Signal.NORTH_WEST, Signal.DATA_CHANNEL_X | Signal.SOUTH_EAST, data);
                    Signal.broadcastSignal(Signal.BORDER | Signal.DATA_CHANNEL_X, Signal.DETECTED);

                    Debug.out("West Border: " + border[0]);
                    Debug.out("East Border: " + border[2]);
                }
                if(borderDirection.radians == Direction.getNorth().radians) {
                    border[1] = myLocation.y + myBodyRadius;
                    border[3] = center.y - (border[1] - center.y);

                    float[] data = {border[1], border[3]};

                    Signal.broadcastCoordinate(Signal.DATA_CHANNEL_Y | Signal.NORTH_WEST, Signal.DATA_CHANNEL_Y | Signal.SOUTH_EAST, data);
                    Signal.broadcastSignal(Signal.BORDER | Signal.DATA_CHANNEL_Y, Signal.DETECTED);

                    Debug.out("North Border: " + border[1]);
                    Debug.out("South Border: " + border[3]);
                }
                if(borderDirection.radians == Direction.getEast().radians) {
                    border[2] = myLocation.x + myBodyRadius;
                    border[0] = center.x - (border[2] - center.x);

                    float[] data = {border[0], border[2]};

                    Signal.broadcastCoordinate(Signal.DATA_CHANNEL_X | Signal.NORTH_WEST, Signal.DATA_CHANNEL_X | Signal.SOUTH_EAST, data);
                    Signal.broadcastSignal(Signal.BORDER | Signal.DATA_CHANNEL_X, Signal.DETECTED);

                    Debug.out("West Border: " + border[0]);
                    Debug.out("East Border: " + border[2]);
                }
                if(borderDirection.radians == Direction.getSouth().radians) {
                    border[3] = myLocation.y - myBodyRadius;
                    border[1] = center.y + (center.y - border[3]);

                    float[] data = {border[1], border[3]};

                    Signal.broadcastCoordinate(Signal.DATA_CHANNEL_Y | Signal.NORTH_WEST, Signal.DATA_CHANNEL_Y | Signal.SOUTH_EAST, data);
                    Signal.broadcastSignal(Signal.BORDER | Signal.DATA_CHANNEL_Y, Signal.DETECTED);

                    Debug.out("South Border: " + border[3]);
                    Debug.out("North Border: " + border[1]);
                }
            }

            float[] borders = Signal.receiveBorders();
            Debug.out(borders[0], borders[1]);
            Debug.out(borders[2], borders[3]);
        } catch (Exception e) {
            Debug.out("Detect Border Exception");
            e.printStackTrace();
        }

        return false;
    }
}