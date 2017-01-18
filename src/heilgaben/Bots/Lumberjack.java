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
    private static ConditionState[] chopTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0, State.STRIKING),
            new ConditionState(() -> nearbyTrees.length == 0, State.SCOUTING),
            new ConditionState(() -> Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES)
    };
    private static ConditionState[] strikeTransitions = {
            new ConditionState(() -> nearbyEnemies.length == 0 && nearbyTrees.length > 0, State.CHOPPING),
            new ConditionState(() -> nearbyEnemies.length == 0 && nearbyTrees.length == 0, State.SCOUTING)
//            new ConditionState(() -> Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES)
    };
    private static ConditionState[] scoutTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0, State.STRIKING),
            new ConditionState(() -> nearbyTrees.length > 0, State.CHOPPING)
//            new ConditionState(() -> Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES)
    };
    private static ConditionState[] shakeTransitions = {
            new ConditionState(() -> Map.getClosestNonemptyBulletTree() == null && nearbyEnemies.length > 0, State.STRIKING),
            new ConditionState(() -> Map.getClosestNonemptyBulletTree() == null && nearbyTrees.length > 0, State.CHOPPING),
            new ConditionState(() -> Map.getClosestNonemptyBulletTree() == null, State.SCOUTING)
    };
    private static ConditionState[] idleTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0, State.STRIKING),
            new ConditionState(() -> nearbyTrees.length > 0, State.CHOPPING),
//            new ConditionState(() -> Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES),
            new ConditionState(() -> true, State.SCOUTING)
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
                case SCOUTING:
                    Action.scout(scoutTransitions);
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
