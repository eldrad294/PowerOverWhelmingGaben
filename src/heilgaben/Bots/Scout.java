package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.*;

import static heilgaben.SignalConstants.*;

public class Scout extends BotState {

    /**
     * BotType Specific Variables
     */
    private static Direction[] closestBorderDirection = new Direction[2];
    private static int scoutCount = 0;

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
            Map.initCenter();
            Map.initBorders();

            if(!Signal.isBorderDetected()) {
                initClosestBorders();
                state = State.DETECTING_BORDER_X;
            } else {
                state = State.SHAKING_TREES;
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
            Map.updateBorders();

            switch(state){
                case DETECTING_BORDER_X:
                    detectBorderX(State.DETECTING_BORDER_Y);
                    break;
                case DETECTING_BORDER_Y:
                    detectBorderY(State.SIGNALING_BORDERS);
                    break;
                case SIGNALING_BORDERS:
                    signalBorders(State.SHAKING_TREES);
                case SHAKING_TREES:
                    shake(State.HARASSING);
                    break;
                case SCOUTING:
                    scout(State.HARASSING);
                    break;
                case HARASSING:
                    harass(State.SCOUTING);
                    break;
                case IDLE:
                default:
                    idle();
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

    private static boolean detectBorderX(State nextState){
        // Check for state change
        if(Signal.isBorderXDetected()) {
            state = nextState;
            return true;
        }

        // Act according to state;
        Direction borderDirection = closestBorderDirection[0];

        if(!Nav.moveTo(myLocation.add(borderDirection))) {
            float[] westEast = {0, 0};
            if (borderDirection.radians == Direction.getWest().radians) {
                westEast[0] = myLocation.x - myBodyRadius;
                westEast[1] = center.x + (center.x - westEast[0]) + (2*myBodyRadius);
            } else {
                westEast[1] = myLocation.x + myBodyRadius;
                westEast[0] = center.x - (westEast[1] - center.x) - (2*myBodyRadius);
            }

            Signal.broadcastCoordinate(NORTH_WEST | DATA_CHANNEL_X, SOUTH_EAST | DATA_CHANNEL_X, westEast);
            Signal.broadcastSignal(BORDER | DATA_CHANNEL_X, DETECTED);
        }

        return false;
    }

    private static boolean detectBorderY(State nextState) {
        // Check for state change
        if(Signal.isBorderYDetected()) {
            state = nextState;
            return true;
        }

        // Act according to state;
        Direction borderDirection = closestBorderDirection[1];

        if(!Nav.moveTo(myLocation.add(borderDirection))) {
            float[] northSouth = {0, 0};
            if (borderDirection.radians == Direction.getSouth().radians) {
                northSouth[1] = myLocation.y - myBodyRadius;
                northSouth[0] = center.y + (center.y - northSouth[1]) + (2*myBodyRadius);
            } else {
                northSouth[0] = myLocation.y + myBodyRadius;
                northSouth[1] = center.y - (northSouth[0] - center.y) - (2*myBodyRadius);
            }

            Signal.broadcastCoordinate(NORTH_WEST | DATA_CHANNEL_Y, SOUTH_EAST | DATA_CHANNEL_Y, northSouth);
            Signal.broadcastSignal(BORDER | DATA_CHANNEL_Y, DETECTED);
        }

        return false;
    }

    private static boolean signalBorders(State nextState) {
        // Check for state change
        if(Signal.isBorderDetected()) {
            state = nextState;
            return true;
        }

        // Act according to state
        Signal.broadcastSignal(BORDER, DETECTED);
        border = Signal.receiveBorders();
        return false;
    }

    private static boolean shake(State nextState) {
        // Init state variables
        TreeInfo closestBulletTree = Map.getClosestNonemptyBulletTree();

        // Check for state change
        if(closestBulletTree == null) {
            state = nextState;
            return true;
        }

        // Act according to state
        try {
            rc.setIndicatorLine(myLocation, closestBulletTree.location, 255, 255, 255);
            if (rc.canShake(closestBulletTree.location)) {
                rc.shake(closestBulletTree.location);
            } else {
                Nav.moveTo(closestBulletTree.location);
            }
        } catch (Exception e){
            Debug.out("Shake Exception");
            e.printStackTrace();
        }

        return false;
    }

    private static boolean scout(State nextState){
        // Check for state change
        if(nearbyEnemies.length > 0) {
            state = nextState;
            return true;
        }

        // Act according to state
        MapLocation scoutLocation = enemyStartingLocations[scoutCount%enemyStartingLocations.length];
        if(scoutLocation.isWithinDistance(myLocation, myRobotSightRadius))
            scoutCount++;

        Nav.moveTo(scoutLocation);

        return false;
    }

    private static boolean harass(State nextState){
        // Check for state change
        if(nearbyEnemies.length == 0) {
            state = nextState;
            return true;
        }

        // Act according to state
        RobotInfo closestEnemy = null;
        for (RobotInfo enemy : nearbyEnemies) {
            if(closestEnemy == null || myLocation.distanceTo(enemy.location) < myLocation.distanceTo(closestEnemy.location))
                closestEnemy = enemy;
        }

        if(closestEnemy == null)
            return false;

        try {
            MapLocation enemyLocation = closestEnemy.location;
            if (rc.canFireSingleShot()) {
                rc.fireSingleShot(new Direction(myLocation, enemyLocation));
            }

            if (closestEnemy.getType() == RobotType.GARDENER && !enemyLocation.isWithinDistance(myLocation, 2)) {
                Nav.moveTo(enemyLocation);
            }
            else {
                Nav.moveTo(myLocation);
            }

        } catch (Exception e) {
            Debug.out("Harass Exception");
            e.printStackTrace();
        }

        return false;
    }

    private static boolean idle(){
        Nav.moveTo(myLocation);
        return false;
    }
}