package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.*;

public class Soldier extends BotState {

    /**
     * BotType Specific Variables
     */

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
