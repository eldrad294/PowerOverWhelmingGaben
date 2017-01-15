package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.*;

public class Scout extends BotState {

    /**
     * BotType Specific Variables
     */
    private static Direction[] closestBorderDirection = new Direction[2];

    /**
     * BotType specific run - called every loop
     */
    public static void run() {
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

    /**
     * BotType specific initialisation
     */
    private static void init() {
        try {
            Util.initCenter();
            Util.initBorders();

            if(!Signal.isBorderDetected()) {
                initClosestBorders();
                state = State.DETECTING_BORDER_X;
            } else {
                state = State.NONE;
            }
        } catch (Exception e){
            Debug.out("Init Exception");
            e.printStackTrace();
        }
    }

    /**
     * Bot state machine
     */
    private static void act() {
        try {
            Util.updateBorders();

            switch(state){
                case DETECTING_BORDER_X:
                    detectBorderX();
                    break;
                case DETECTING_BORDER_Y:
                    detectBorderY();
                    break;
                case SIGNALING_BORDERS:
                    signalBorders();
                case NONE:
                    Nav.move(Nav.getMoveDirection(myLocation));
                    break;
            }

            Debug.drawMapBorder();

        } catch (Exception e){
            Debug.out("Act Exception");
            e.printStackTrace();
        }
    }

    /**
     * Initialisation functions
     */

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

    /**
     * State specific functions
     * @return true if state changed
     */

    private static boolean detectBorderX(){
        // Check for state change
        if(Signal.isBorderXDetected()) {
            state = State.DETECTING_BORDER_Y;
            return true;
        }

        // Act according to state;
        Direction borderDirection = closestBorderDirection[0];

        if(!Nav.move(Nav.getMoveDirection(myLocation.add(borderDirection)))) {
            float[] westEast = {0, 0};
            if (borderDirection.radians == Direction.getWest().radians) {
                westEast[0] = myLocation.x - myBodyRadius;
                westEast[1] = center.x + (center.x - westEast[0]);
            } else {
                westEast[1] = myLocation.x + myBodyRadius;
                westEast[0] = center.x - (westEast[1] - center.x);
            }

            Signal.broadcastCoordinate(Signal.NORTH_WEST | Signal.DATA_CHANNEL_X, Signal.SOUTH_EAST | Signal.DATA_CHANNEL_X, westEast);
            Signal.broadcastSignal(Signal.BORDER | Signal.DATA_CHANNEL_X, Signal.DETECTED);
        }

        return false;
    }

    private static boolean detectBorderY() {
        // Check for state change
        if(Signal.isBorderYDetected()) {
            state = State.SIGNALING_BORDERS;
            return true;
        }

        // Act according to state;
        Direction borderDirection = closestBorderDirection[1];

        if(!Nav.move(Nav.getMoveDirection(myLocation.add(borderDirection)))) {
            float[] northSouth = {0, 0};
            if (borderDirection.radians == Direction.getSouth().radians) {
                northSouth[1] = myLocation.y - myBodyRadius;
                northSouth[0] = center.y + (center.y - northSouth[1]);
            } else {
                northSouth[0] = myLocation.y + myBodyRadius;
                northSouth[1] = center.y - (northSouth[0] - center.y);
            }

            Signal.broadcastCoordinate(Signal.NORTH_WEST | Signal.DATA_CHANNEL_Y, Signal.SOUTH_EAST | Signal.DATA_CHANNEL_Y, northSouth);
            Signal.broadcastSignal(Signal.BORDER | Signal.DATA_CHANNEL_Y, Signal.DETECTED);
        }

        return false;
    }

    private static boolean signalBorders() {
        // Check for state change
        if(Signal.isBorderDetected()) {
            state = State.NONE;
            return true;
        }

        // Act according to state
        Signal.broadcastSignal(Signal.BORDER, Signal.DETECTED);
        border = Signal.receiveBorders();
        return false;
    }
}