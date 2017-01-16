package heilgaben;

import battlecode.common.*;

/**
 * Generic Actions
 */

public interface Condition {
    boolean isValid();
}

public class ConditionState {
    Condition condition;
    State nextState;

    ConditionState(Condition condition, State state){
        this.condition = condition;
        this.nextState = state;
    }
}

public class Action extends BotState {
    public static boolean shake(ConditionState[] conditionStateList) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        TreeInfo closestBulletTree = Map.getClosestNonemptyBulletTree();
        try {
            rc.setIndicatorLine(myLocation, closestBulletTree.location, 255, 255, 255);
            if (rc.canShake(closestBulletTree.location)) {
                rc.shake(closestBulletTree.location);
            } else {
                Nav.moveTo(closestBulletTree.location);
            }
        } catch (Exception e){
            Debug.out("Shake Exception");
            e.printStackTrace();
        }

        return false;
    }
}
