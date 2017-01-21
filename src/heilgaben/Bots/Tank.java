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
            new ConditionState(() -> nearbyEnemies.length == 0, State.DRIVE)
    };

    private static ConditionState[] driveTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0, State.ATTACKING),
    };

    private static ConditionState[] idleTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0, State.ATTACKING),
            new ConditionState(() -> true, State.DRIVE)
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
                case DRIVE:
                    Action.drive(driveTransitions);
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
