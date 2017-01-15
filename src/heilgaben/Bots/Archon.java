package heilgaben.Bots;

import battlecode.common.*;

import heilgaben.*;
import static heilgaben.SignalConstants.*;

public class Archon extends BotState {

    /**
     * BotType specific variables
     */

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

            spawn();
            Nav.moveTo(myLocation);
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

    private static boolean spawn() {
        try {
            Direction spawnDirection = Util.getRobotSpawnDirection(RobotType.GARDENER);
            if(spawnDirection != null) {
                rc.hireGardener(spawnDirection);
                return true;
            }
        } catch (Exception e) {
            Debug.out("Spawn Exception");
            e.printStackTrace();
        }

        return false;
    }

    private static boolean updateGlobalState() {
        int globalState = Signal.receiveSignal(GLOBAL_STATE);
        switch(globalState){
            case NO_DATA:
                Signal.broadcastSignal(GLOBAL_STATE, OPENING);
            case OPENING:
                if(rc.getRoundNum() > 300) {
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
