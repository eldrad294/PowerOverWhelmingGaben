package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.Debug;
import heilgaben.BotState;
import heilgaben.Signal;
import heilgaben.Util;

public class Lumberjack extends BotState {
    /**
     * BotType Specific Variables
     */

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
            Util.initCenter();
            Util.initBorders();
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
            Util.updateBorders();
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
