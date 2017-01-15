package heilgaben.Bots;

import battlecode.common.*;

import heilgaben.*;
import static heilgaben.SignalConstants.*;

import java.util.ArrayList;


public class Gardener extends BotState {

    /**
     * BotType Specific Variables
     */

    public static ArrayList<Direction> spawnDirections;

    /**
     * BotType specific run - called every loop
     */
    public static void run() throws GameActionException {
        /* Gardener specific init */
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
                Debug.out("Gardener Exception");
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

            state = State.SEARCHING_GARDEN_SPOT;
        } catch (Exception e) {
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
            globalState = Signal.getGlobalState();

            switch(globalState) {
                case OPENING:
            }
            switch (state) {
                case SEARCHING_GARDEN_SPOT:
                    search();
                    break;
                case PLANTING_GARDEN:
                    plant();
                    water();
                    break;
                case TENDING_GARDEN:
                    spawn();
                    water();
                    break;
                default:
                    idle();
                    break;
            }
        } catch (Exception e) {
            Debug.out("Act Exception");
            e.printStackTrace();
        }
    }

    /**
     * Initialisation functions
     */

    /**
     * State specific functions
     *
     * @return true if state changed
     */

    private static boolean search() {
        // Check for state change
        if (rc.senseNearbyRobots(3).length == 0 && rc.senseNearbyTrees(2).length == 0) {
            spawnDirections = Util.getSpawnableDirections(1);
            state = State.PLANTING_GARDEN;
            return true;
        }

        // Act according to state
        Nav.moveTo(center);
        return false;
    }

    private static boolean plant() {
        // Check for state change
        if (rc.senseNearbyTrees(3, myTeam).length >= spawnDirections.size()-1) {
            state = State.TENDING_GARDEN;
            return true;
        }

        Debug.out("Spawn Directions: " + spawnDirections.size());
        Debug.out("Nearby Trees: " + rc.senseNearbyTrees(3, myTeam).length);
        // Act according to state;
        try {
            Direction spawnDirection = getPlantDirection();
            if (spawnDirection != null) {
                rc.plantTree(spawnDirection);
            }

        } catch (Exception e) {
            Debug.out("Plant Exception");
            e.printStackTrace();
        }

        return false;
    }

    private static boolean water() {
        try {
            TreeInfo[] treesInRange = rc.senseNearbyTrees(2, myTeam);
            if (treesInRange.length > 0) {
                TreeInfo lowestTree = treesInRange[0];

                for (TreeInfo tree : treesInRange) {
                    MapLocation treeLocation = tree.location;
                    if (rc.canInteractWithTree(treeLocation))
                        if (rc.canWater(treeLocation)) {
                            if (lowestTree.health > tree.health) {
                                lowestTree = tree;
                            }
                        }
                }

                rc.water(lowestTree.location);
            }
        } catch (Exception e) {
            Debug.out("Water Exception");
            e.printStackTrace();
        }

        return false;
    }

    private static boolean spawn() {
//        if(Signal.isBorderDetected())
//            spawn(RobotType.SOLDIER);
//        else
        spawn(RobotType.SCOUT);
        return false;
    }

    private static boolean spawn(RobotType robotType) {
        try {
            Direction spawnDirection = Util.getRobotSpawnDirection(robotType);
            if (spawnDirection != null) {
                rc.buildRobot(robotType, spawnDirection);
            }
        } catch (Exception e) {
            Debug.out("Spawn Exception");
            e.printStackTrace();
        }

        return false;
    }

    private static boolean idle() {
        Nav.moveTo(myLocation);
        return false;
    }

    /**
     * Helper Functions
     *
     * @return
     */
    private static Direction getPlantDirection() {
        try {
            for (Direction spawnDirection : spawnDirections) {
                if (rc.canPlantTree(spawnDirection))
                    return spawnDirection;
            }
        } catch (Exception e) {
            Debug.out("Get Spawn Direction Exception");
            e.printStackTrace();
        }

        return null;
    }
}
