package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.*;
import heilgaben.Actions.*;

public class Soldier extends BotState {

    /**
     * BotType Specific Variables
     */

    /**
     * State Transitions
     */
    private static ConditionState[] attackTransitions = {
        new ConditionState(() -> nearbyEnemies.length == 0 && Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES),
        new ConditionState(() -> nearbyEnemies.length == 0, State.SCOUTING)
    };

    private static ConditionState[] shakeTransitions = {
        new ConditionState(() -> nearbyEnemies.length > 0, State.ATTACKING),
        new ConditionState(() -> Map.getClosestNonemptyBulletTree() == null, State.SCOUTING),
    };
    private static ConditionState[] scoutTransitions = {
        new ConditionState(() -> nearbyEnemies.length > 0, State.ATTACKING),
        new ConditionState(() -> Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES)
    };
    private static ConditionState[] idleTransitions = {
        new ConditionState(() -> nearbyEnemies.length > 0, State.ATTACKING),
        new ConditionState(() -> Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES),
        new ConditionState(() -> true, State.SCOUTING)
    };

    /**
     * BotType specific run - called every loop
     */
    public static void run() throws GameActionException {
        /* Soldier specific init */
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
                Debug.out("Soldier Exception");
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

            switch(state){
                case ATTACKING:
                    Action.harass(attackTransitions);
                    break;
                case SHAKING_TREES:
                    Action.shake(shakeTransitions);
                    break;
                case SCOUTING:
                    Action.scout(scoutTransitions);
                    break;
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

    /**
     * State specific functions
     * @return true if state changed
     */

}
