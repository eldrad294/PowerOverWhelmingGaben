package heilgaben.Actions;

import battlecode.common.*;
import heilgaben.*;

import java.util.ArrayList;

import static heilgaben.SignalConstants.*;

/**
 *
 * param  : conditionStateList  : A list of condition-state transitions. Condition must be met in order to transition to State
 * return : boolean             : True if state changed. False otherwise
 */

public class Action extends BotState {
    // TODO: WIP
    public static boolean clearForest(ConditionState[] conditionStateList) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        Action.spawn(new ConditionState[0], RobotType.LUMBERJACK);
        return false;
    }

    public static boolean detectBorderX(ConditionState[] conditionStateList, Direction borderDirection){
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state;
        if(!Nav.moveTo(myLocation.add(borderDirection))) {
            float[] westEast = {0, 0};
            if (borderDirection.radians == Direction.getWest().radians) {
                westEast[0] = myLocation.x - myBodyRadius;
                westEast[1] = center.x + (center.x - westEast[0]) + (2*myBodyRadius);
            } else {
                westEast[1] = myLocation.x + myBodyRadius;
                westEast[0] = center.x - (westEast[1] - center.x) - (2*myBodyRadius);
            }

            Signal.broadcastCoordinate(NORTH_WEST | DATA_CHANNEL_X, SOUTH_EAST | DATA_CHANNEL_X, westEast);
            Signal.broadcastSignal(BORDER | DATA_CHANNEL_X, DETECTED);
        }

        return false;
    }

    public static boolean detectBorderY(ConditionState[] conditionStateList, Direction borderDirection) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state;
        if(!Nav.moveTo(myLocation.add(borderDirection))) {
            float[] northSouth = {0, 0};
            if (borderDirection.radians == Direction.getSouth().radians) {
                northSouth[1] = myLocation.y - myBodyRadius;
                northSouth[0] = center.y + (center.y - northSouth[1]) + (2*myBodyRadius);
            } else {
                northSouth[0] = myLocation.y + myBodyRadius;
                northSouth[1] = center.y - (northSouth[0] - center.y) - (2*myBodyRadius);
            }

            Signal.broadcastCoordinate(NORTH_WEST | DATA_CHANNEL_Y, SOUTH_EAST | DATA_CHANNEL_Y, northSouth);
            Signal.broadcastSignal(BORDER | DATA_CHANNEL_Y, DETECTED);
        }

        return false;
    }

    public static boolean harass(ConditionState[] conditionStateList){
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        RobotInfo closestEnemy = null;
        for (RobotInfo enemy : nearbyEnemies) {
            if(closestEnemy == null || myLocation.distanceTo(enemy.location) < myLocation.distanceTo(closestEnemy.location))
                closestEnemy = enemy;
        }

        if(closestEnemy == null)
            return false;

        try {
            MapLocation enemyLocation = closestEnemy.location;
            if (rc.canFireSingleShot()) {
                rc.fireSingleShot(new Direction(myLocation, enemyLocation));
            }

            if (closestEnemy.getType() == RobotType.GARDENER && !enemyLocation.isWithinDistance(myLocation, 2)) {
                Nav.moveTo(enemyLocation);
            }
            else {
                Nav.moveTo(myLocation);
            }

        } catch (Exception e) {
            Debug.out("Harass Exception");
            e.printStackTrace();
        }

        return false;
    }

    public static boolean idle(ConditionState[] conditionStateList){
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        Nav.moveTo(myLocation);
        return false;
    }

    public static boolean plant(ConditionState[] conditionStateList, Direction plantDirection) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state;
        try {
            if (plantDirection != null) {
                rc.plantTree(plantDirection);
            }

        } catch (Exception e) {
            Debug.out("Plant Exception");
            e.printStackTrace();
        }

        return false;
    }

    public static boolean spawn(ConditionState[] conditionStateList, RobotType robotType) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        try {
            Direction spawnDirection = Util.getRobotSpawnDirection(robotType);
            if(spawnDirection != null) {
                if(robotType == RobotType.GARDENER) {
                    rc.hireGardener(spawnDirection);
                } else {
                    rc.buildRobot(robotType, spawnDirection);
                }
            }
        } catch (Exception e) {
            Debug.out("Spawn Exception");
            e.printStackTrace();
        }

        return false;
    }


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

    public static boolean signalBorders(ConditionState[] conditionStateList) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        Signal.broadcastSignal(BORDER, DETECTED);
        border = Signal.receiveBorders();
        return false;
    }

    // TODO: WIP
    public static boolean scout(ConditionState[] conditionStateList){
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state

        MapLocation[] broadcastLocations = rc.senseBroadcastingRobotLocations();
        MapLocation scoutLocation;
        if(broadcastLocations.length == 0)
            scoutLocation = startingLocations[scoutCount%startingLocations.length];
        else
            scoutLocation = broadcastLocations[scoutCount%broadcastLocations.length];
        if(scoutLocation.isWithinDistance(myLocation, myRobotSightRadius))
            scoutCount++;

        Nav.moveTo(scoutLocation);

        return false;
    }

    // TODO: WIP
    public static boolean search(ConditionState[] conditionStateList) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        Nav.moveTo(center);
        return false;
    }
}
