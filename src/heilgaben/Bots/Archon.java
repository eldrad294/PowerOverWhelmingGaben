package heilgaben.Bots;

import battlecode.common.*;

import heilgaben.*;
import heilgaben.Actions.Action;
import heilgaben.Actions.ConditionState;

import static heilgaben.SignalConstants.*;

public class Archon extends BotState {

    /**
     * BotType specific variables
     */

    /**
     * State Transitions
     */
    private static ConditionState[] hireTransitions = {
            new ConditionState(() -> rc.getRoundNum()%50 > 10, State.SHAKING_TREES)
    };
    private static ConditionState[] shakeTransitions = {
            new ConditionState(() -> Map.getClosestNonemptyBulletTree() == null, State.IDLE)
    };
    private static ConditionState[] idleTransitions = {
            new ConditionState(() -> rc.getRoundNum()%50 <= 10, State.HIRING_GARDENERS)
    };

    /**
     * BotType specific run - called every loop
     */
    public static void run() {

        /* Archon specific init */
        init();

        while (true) {
            try {
                /* Update State */
                update();

                /* Round Actions */
                act();

                /* Yield round */
                Clock.yield();

            } catch (Exception e) {
                Debug.out("Exception");
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
            updateGlobalState();

            switch(state){
                case HIRING_GARDENERS:
                    Action.spawn(hireTransitions, RobotType.GARDENER);
                    break;
                case SHAKING_TREES:
                    Action.shake(shakeTransitions);
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

    //...

    /**
     * Helper Functions
     * @return
     */

    private static boolean updateGlobalState() throws GameActionException{
        int globalState = Signal.receiveSignal(GLOBAL_STATE);
        switch(globalState){
            case NO_CHANNEL:
                Signal.broadcastSignal(GLOBAL_STATE, OPENING);
            case OPENING:
                if(rc.getRoundNum() > 100) {
                    Signal.broadcastSignal(GLOBAL_STATE, MIDGAME);
                    return true;
                }
            case MIDGAME:
                if(rc.getRoundNum() > 1500) {
                    Signal.broadcastSignal(GLOBAL_STATE, ENDGAME);
                    return true;
                }
            default:
                return false;
        }
    }
}
