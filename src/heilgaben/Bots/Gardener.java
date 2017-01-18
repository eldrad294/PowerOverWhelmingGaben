package heilgaben.Bots;

import battlecode.common.*;

import heilgaben.*;
import heilgaben.Actions.*;

import static heilgaben.SignalConstants.*;

import java.util.ArrayList;


public class Gardener extends BotState {

    /**
     * BotType Specific Variables
     */

    public static ArrayList<Direction> spawnDirections;

    /**
     * State Transitions
     */
    private static ConditionState[] searchTransitions = {
            new ConditionState(() -> rc.senseNearbyRobots(3).length == 0 && rc.senseNearbyTrees(3).length == 0, State.PLANTING_GARDEN)
    };
    private static ConditionState[] plantTransitions = {
            new ConditionState(() -> rc.senseNearbyTrees(-1, Team.NEUTRAL).length > 0, State.CLEARING_FOREST),
            new ConditionState(() -> rc.senseNearbyTrees(3, myTeam).length >= spawnDirections.size()-1, State.TENDING_GARDEN)
    };
    private static ConditionState[] clearForestTransitions = {
            new ConditionState(() -> rc.senseNearbyTrees(-1, Team.NEUTRAL).length == 0, State.PLANTING_GARDEN)
    };
    private static ConditionState[] idleTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0, State.TENDING_GARDEN)
    };
    private static ConditionState[] spawnTransitions = {};

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

            state = State.IDLE;
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

            switch (state) {
                case SEARCHING_GARDEN_SPOT:
                    Action.search(searchTransitions);
                    break;
                case PLANTING_GARDEN:
                    switch(globalState) {
                        case OPENING:
                            Action.spawn(spawnTransitions, RobotType.SCOUT);
                            break;
                        default:
                            Action.plant(plantTransitions, getPlantDirection());
                            water();
                            break;
                    }
                    break;
                case TENDING_GARDEN:
                    switch(globalState) {
                        case OPENING:
                            Action.spawn(spawnTransitions, RobotType.SCOUT);
                            break;
                        case MIDGAME:
                        case ENDGAME:
                            Action.spawn(spawnTransitions, RobotType.SOLDIER);
                            break;
                    }
                    water();
                    break;
                case CLEARING_FOREST:
                    Action.clearForest(clearForestTransitions);
                    break;
                default:
                    Action.idle(idleTransitions);
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
     * Helper Functions
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
}
