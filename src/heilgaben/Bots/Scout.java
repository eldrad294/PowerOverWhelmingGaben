package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.*;
import heilgaben.Actions.*;

import static heilgaben.SignalConstants.*;

public class Scout extends BotState {

    /**
     * BotType Specific Variables
     */
    private static Direction[] closestBorderDirection = new Direction[2];

    /**
     * State Transitions
     */
    private static ConditionState[] detectBorderXTransitions = {
            new ConditionState(() -> Signal.isBorderXDetected(), State.DETECTING_BORDER_Y)
    };
    private static ConditionState[] detectBorderYTransitions = {
            new ConditionState(() -> Signal.isBorderYDetected(), State.SIGNALING_BORDERS)
    };
    private static ConditionState[] signalBordersTransitions = {
            new ConditionState(() -> Signal.isBorderDetected(), State.SHAKING_TREES)
    };
    private static ConditionState[] shakeTransitions = {
            new ConditionState(() -> Map.getClosestNonemptyBulletTree() == null, State.SCOUTING)
    };
    private static ConditionState[] scoutTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0 && !Util.willBeBlockedByTree(nearbyEnemies), State.HARASSING)
    };
    private static ConditionState[] harassTransitions = {
            new ConditionState(() -> nearbyEnemies.length == 0, State.SCOUTING)
    };
    private static ConditionState[] idleTransitions = {
            new ConditionState(() -> nearbyEnemies.length > 0 && !Util.willBeBlockedByTree(nearbyEnemies), State.HARASSING)
    };

    /**
     * BotType specific run - called every loop
     */
    public static void run() {
        /* Scout specific init */
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
                Debug.out("Scout Exception");
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

            if(!Signal.isBorderDetected()) {
                initClosestBorders();
                state = State.DETECTING_BORDER_X;
            } else {
                state = State.SHAKING_TREES;
            }
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
                case DETECTING_BORDER_X:
                    Action.detectBorderX(detectBorderXTransitions, closestBorderDirection[0]);
                    break;
                case DETECTING_BORDER_Y:
                    Action.detectBorderY(detectBorderYTransitions, closestBorderDirection[1]);
                    break;
                case SIGNALING_BORDERS:
                    Action.signalBorders(signalBordersTransitions);
                case SHAKING_TREES:
                    Action.shake(shakeTransitions);
                    break;
                case SCOUTING:
                    Action.scout(scoutTransitions);
                    break;
                case HARASSING:
                    Action.harass(harassTransitions);
                    break;
                case IDLE:
                default:
                    Action.idle(idleTransitions);
                    break;
            }

            Debug.drawMapBorder();

        } catch (Exception e){
            Debug.out("Act Exception");
            e.printStackTrace();
        }
    }

    /**
     * Initialisation functions
     */

    private static void initClosestBorders() {
        Direction closestX;
        Direction closestY;
        if(myLocation.x < center.x)
            closestX = Direction.getWest();
        else
            closestX = Direction.getEast();

        if(myLocation.y > center.y)
            closestY = Direction.getNorth();
        else
            closestY = Direction.getSouth();

        closestBorderDirection[0] = closestX;
        closestBorderDirection[1] = closestY;
    }
}