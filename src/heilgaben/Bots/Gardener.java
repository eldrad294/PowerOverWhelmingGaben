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
            new ConditionState(() -> rc.senseNearbyRobots(myBodyRadius + 4).length == 0 && rc.senseNearbyTrees(3).length == 0, State.PLANTING_GARDEN),
            new ConditionState(() -> rc.senseNearbyTrees(-1, Team.NEUTRAL).length > 0, State.SPAWNING_LUMBERJACK),
    };
    private static ConditionState[] plantTransitions = {
            new ConditionState(() -> rc.senseNearbyTrees(-1, Team.NEUTRAL).length > 0, State.SPAWNING_LUMBERJACK),
            new ConditionState(() -> rc.senseNearbyTrees(3, myTeam).length >= spawnDirections.size()-1, State.TENDING_GARDEN)
    };
    private static ConditionState[] idleTransitions = {
            new ConditionState(() -> nearbyEnemies.length == 0, State.SEARCHING_GARDEN_SPOT)
    };
    private static ConditionState[] spawnScoutTransitions = {
            new ConditionState(() -> globalState != OPENING, State.SPAWNING_SOLDIER),
            new ConditionState(() -> rc.senseNearbyTrees(-1, Team.NEUTRAL).length > 0, State.SPAWNING_LUMBERJACK),
            new ConditionState(() -> rc.getTeamBullets() < 80, State.SEARCHING_GARDEN_SPOT),
    };
    private static ConditionState[] spawnSoldierTransition = {
            new ConditionState(() -> rc.senseNearbyTrees(-1, Team.NEUTRAL).length > 0, State.SPAWNING_LUMBERJACK),
            new ConditionState(() -> rc.getTeamBullets() < 100, State.SEARCHING_GARDEN_SPOT)
    };
    private static ConditionState[] spawnLumberjackTransition = {
            new ConditionState(() -> rc.getTeamBullets() < 100, State.SEARCHING_GARDEN_SPOT),
    };
    private static ConditionState[] spawnTankTransition = {
            new ConditionState(() -> rc.getTeamBullets() < 300, State.SPAWNING_SOLDIER)
    };

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
                    rc.setIndicatorDot(myLocation, 255, 255, 0);
                    Action.search(searchTransitions);
                    break;
                case PLANTING_GARDEN:
                    rc.setIndicatorDot(myLocation, 0, 255, 0);
                    Action.plant(plantTransitions, getPlantDirection());
                    water();
                    break;
                case TENDING_GARDEN:
                    rc.setIndicatorDot(myLocation, 0, 0, 255);
                    Action.spawn(spawnSoldierTransition, RobotType.SOLDIER);
                    water();
                    break;
                case SPAWNING_SCOUT:
                    rc.setIndicatorDot(myLocation, 0, 0, 0);
                    Action.spawn(spawnScoutTransitions, RobotType.SCOUT);
                    break;
                case SPAWNING_SOLDIER:
                    rc.setIndicatorDot(myLocation, 50, 50, 50);
                    Action.spawn(spawnSoldierTransition, RobotType.SOLDIER);
                    break;
                case SPAWNING_TANK:
                    rc.setIndicatorDot(myLocation, 100, 100, 100);
                    Action.spawn(spawnTankTransition, RobotType.TANK);
                    break;
                case SPAWNING_LUMBERJACK:
                    rc.setIndicatorDot(myLocation, 200, 200, 200);
                    Action.spawn(spawnLumberjackTransition, RobotType.LUMBERJACK);
                    break;
                case IDLE:
                    rc.setIndicatorDot(myLocation, 200, 200, 255);
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
