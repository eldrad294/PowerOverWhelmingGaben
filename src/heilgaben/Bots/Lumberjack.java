package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.*;
import heilgaben.Actions.*;

public class Lumberjack extends BotState {
    /**
     * BotType Specific Variables
     */

    /**
     * State Transitions
     */
//    private static ConditionState[] chopTransitions = {
//            new ConditionState(() -> Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES),
//            new ConditionState(() -> nearbyEnemies.length > 0, State.STRIKING),
//            new ConditionState(() -> true, State.PATROL)
//    };
//    private static ConditionState[] strikeTransitions = {
//            new ConditionState(() -> Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES),
//            new ConditionState(() -> nearbyTrees.length > 0, State.CHOPPING),
//            new ConditionState(() -> true, State.PATROL)
//    };
//    private static ConditionState[] shakeTransitions = {
//            new ConditionState(() -> nearbyTrees.length > 0, State.CHOPPING),
//            new ConditionState(() -> nearbyEnemies.length > 0, State.STRIKING),
//            new ConditionState(() -> true, State.PATROL)
//    };
//    private static ConditionState[] idleTransitions = {
//            new ConditionState(() -> nearbyEnemies.length > 0, State.STRIKING),
//            new ConditionState(() -> nearbyTrees.length > 0, State.CHOPPING),
//            new ConditionState(() -> Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES),
//            new ConditionState(() -> true, State.PATROL)
//    };
//    private static ConditionState[] patrolTransition = {
//            new ConditionState(() -> Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES),
//            new ConditionState(() -> nearbyTrees.length > 0, State.CHOPPING),
//            new ConditionState(() -> nearbyEnemies.length > 0, State.STRIKING),
//            new ConditionState(() -> true, State.IDLE)
//    };

    private static ConditionState[] chopTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0, State.STRIKING),
            new ConditionState(() -> nearbyTrees.length == 0, State.PATROL)
    };
    private static ConditionState[] strikeTransitions = {
            new ConditionState(() -> nearbyEnemies.length == 0 && nearbyTrees.length > 0, State.CHOPPING),
            new ConditionState(() -> nearbyEnemies.length == 0 && nearbyTrees.length == 0, State.PATROL)
    };
    private static ConditionState[] patrolTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0, State.STRIKING),
            new ConditionState(() -> nearbyTrees.length > 0, State.CHOPPING)
    };
    private static ConditionState[] shakeTransitions = {
            new ConditionState(() -> Map.getClosestNonemptyBulletTree() == null && nearbyEnemies.length > 0, State.STRIKING),
            new ConditionState(() -> Map.getClosestNonemptyBulletTree() == null && nearbyTrees.length > 0, State.CHOPPING),
            new ConditionState(() -> Map.getClosestNonemptyBulletTree() == null, State.PATROL)
    };
    private static ConditionState[] idleTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0, State.STRIKING),
            new ConditionState(() -> nearbyTrees.length > 0, State.CHOPPING),
            new ConditionState(() -> true, State.PATROL)
    };
    /**
     * BotType specific run - called every loop
     */
    public static void run() throws GameActionException {
        /* Lumberjack specific init */
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
                Debug.out("Lumberjack Exception");
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

            switch (state) {
                case CHOPPING:
                    Action.chop(chopTransitions);
                    break;
                case STRIKING:
                    Action.strike(strikeTransitions);
                    break;
                case PATROL:
                    Action.patrol(patrolTransitions);
                    break;
                case SHAKING_TREES:
                    Action.shake(shakeTransitions);
                case IDLE:
                    Action.idle(idleTransitions);
                    break;
            }
        } catch (Exception e){
            Debug.out("Act Exception");
            e.printStackTrace();
        }
    }
    /**
     * Initialisation functions
     */
}
