package heilgaben.Bots;

import battlecode.common.*;

import heilgaben.*;

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
}
