package heilgaben.Bots;

import battlecode.common.*;

import heilgaben.*;
import heilgaben.Actions.*;

import static heilgaben.SignalConstants.*;
import static heilgaben.Util.*;

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
            new ConditionState(() -> rc.senseNearbyRobots(myBodyRadius + 2).length == 0 && rc.senseNearbyTrees(myBodyRadius + 2).length == 0, State.PLANTING_GARDEN),
            new ConditionState(() -> rc.senseNearbyTrees(-1, Team.NEUTRAL).length > 0, State.SPAWNING_LUMBERJACK),
    };
    private static ConditionState[] plantTransitions = {
            new ConditionState(() -> rc.senseNearbyTrees(-1, Team.NEUTRAL).length > 0, State.SPAWNING_LUMBERJACK),
            new ConditionState(() -> nearbyEnemies.length > 0, State.SPAWNING_SOLDIER),
            new ConditionState(() -> rc.getTeamBullets() < 50 || getSpawnableDirections(1).size() == 1, State.TENDING_GARDEN),
    };
    private static ConditionState[] idleTransitions = {
            new ConditionState(() -> globalState == OPENING, State.SPAWNING_SCOUT),
            new ConditionState(() -> nearbyEnemies.length == 0, State.SEARCHING_GARDEN_SPOT),
            new ConditionState(() -> nearbyEnemies.length > 0, State.SPAWNING_SOLDIER)
    };
    private static ConditionState[] spawnScoutTransitions = {
            new ConditionState(() -> globalState != OPENING, State.SEARCHING_GARDEN_SPOT)
    };
    private static ConditionState[] spawnSoldierTransition = {
            new ConditionState(() -> rc.senseNearbyTrees(-1, Team.NEUTRAL).length > 0, State.SPAWNING_LUMBERJACK),
            new ConditionState(() -> rc.getTeamBullets() < 100, State.TENDING_GARDEN)
    };
    private static ConditionState[] spawnLumberjackTransition = {
            new ConditionState(() -> rc.senseNearbyTrees(-1, Team.NEUTRAL).length == 0, State.SEARCHING_GARDEN_SPOT),
    };
    private static ConditionState[] spawnTankTransition = {
            new ConditionState(() -> rc.senseNearbyTrees(-1, Team.NEUTRAL).length > 0, State.SPAWNING_LUMBERJACK),
            new ConditionState(() -> rc.getTeamBullets() < 300, State.TENDING_GARDEN)
    };
    private static ConditionState[] waterTransition = {
            new ConditionState(() -> rc.getTeamBullets() >= 50 && getSpawnableDirections(1).size() > 1, State.PLANTING_GARDEN),
            new ConditionState(() -> rc.getTeamBullets() >= 150, State.SPAWNING_TANK),
            new ConditionState(() -> rc.getTeamBullets() >= 100, State.SPAWNING_SOLDIER)
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
                    Action.search(searchTransitions);
                    break;
                case PLANTING_GARDEN:
                    Action.plant(plantTransitions, Util.getPlantDirection());
                    break;
                case TENDING_GARDEN:
                    Action.water(waterTransition);
                    break;
                case SPAWNING_SCOUT:
                    Action.spawn(spawnScoutTransitions, RobotType.SCOUT);
                    break;
                case SPAWNING_SOLDIER:
                    Action.spawn(spawnSoldierTransition, RobotType.SOLDIER);
                    break;
                case SPAWNING_TANK:
                    Action.spawn(spawnTankTransition, RobotType.TANK);
                    break;
                case SPAWNING_LUMBERJACK:
                    Action.spawn(spawnLumberjackTransition, RobotType.LUMBERJACK);
                    break;
                case IDLE:
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
}
