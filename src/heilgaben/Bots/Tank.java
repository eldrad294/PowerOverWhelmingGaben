package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.*;
import heilgaben.Actions.*;

public class Tank extends BotState {

    /**
     * BotType Specific Variables
     */

    /**
     * State Transitions
     */

    private static ConditionState[] attackTransitions = {
            new ConditionState(() -> nearbyEnemies.length == 0, State.PATROL)
    };

    private static ConditionState[] patrolTransition = {
            new ConditionState(() -> nearbyEnemies.length > 0, State.ATTACKING),
            new ConditionState(() -> Map.getClosestNonemptyBulletTree() != null, State.SHAKING_TREES),
    };

    private static ConditionState[] idleTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0, State.ATTACKING),
            new ConditionState(() -> true, State.PATROL)
    };

    /**
     * BotType specific run - called every loop
     */
    public static void run() throws GameActionException {
        /* Tank specific init */
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
                Debug.out("Tank Exception");
                e.printStackTrace();
            }
        }
    }

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

    private static void act() {
        try {
            Map.updateBorders();

            switch(state){
                case ATTACKING:
                    Action.attack(attackTransitions);
                    break;
                case PATROL:
                    Action.patrol(patrolTransition);
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
}
