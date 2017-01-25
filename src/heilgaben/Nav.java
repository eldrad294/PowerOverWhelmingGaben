package heilgaben;

import battlecode.common.*;

import java.util.ArrayList;
import heilgaben.Vector;

/**
 * Navigation Class
 */
public class Nav extends BotState {

    public static boolean moveTo(MapLocation destination){
        Direction direction = getMoveDirection(destination);
        return move(direction);
    }

    public static boolean move(Direction moveDirection) {
        try {
            if(rc.hasMoved())
                return false;

            if(moveDirection == null)
                return false;

            float maxMoveDistance = Nav.getMaxMoveDistance(moveDirection);
            if (rc.canMove(moveDirection, maxMoveDistance)) {
                rc.move(moveDirection, maxMoveDistance);

                MapLocation nextLocation = new MapLocation(myLocation.x + moveDirection.getDeltaX(2), myLocation.y + moveDirection.getDeltaY(2));
                rc.setIndicatorLine(myLocation, nextLocation, 255, 0, 255);

                if(targetLocation != null)
                    rc.setIndicatorLine(myLocation, targetLocation, 255, 0, 255);

                return true;
            }
        } catch (Exception e){
            Debug.out("Move Exception");
            e.printStackTrace();
        }
        return false;

    }

    public static Direction getMoveDirection(MapLocation destination){
        ArrayList<Vector> allDirections = new ArrayList<>();

        if(destination != myLocation) {
            Direction directionToDestination = new Direction(myLocation, destination);
            allDirections.add(new Vector(directionToDestination, 2));
        }

        ArrayList<Vector> repulsiveDirections = getRepulsiveVectors(myBodyRadius*2);
        ArrayList<Vector> attractiveDirections = getAttractiveVectors(myBodyRadius*2);
        allDirections.addAll(repulsiveDirections);
        allDirections.addAll(attractiveDirections);


        Vector resultant = computeResultantVector(allDirections);
        if(resultant != null)
            return resultant.direction;

        return null;
    }

    public static ArrayList<Vector> getRepulsiveVectors(float repulsionRadius){
        ArrayList<Vector> repulsionVectors = new ArrayList<>();

        // Avoid robots, unless you're a scout or a lumberjack
        for(RobotInfo robot: nearbyRobots) {
            MapLocation robotLocation = robot.getLocation();
            float robotRepulsion = repulsionRadius + (robot.getRadius() * 2);

            switch (myType) {
                case SCOUT:
                    if (robot.team == enemyTeam && robot.type == RobotType.GARDENER)
                        continue;
                    break;
                case LUMBERJACK:
                    if (robot.team == enemyTeam)
                        continue;

                    if (robot.type == myType)
                        robotRepulsion = myType.sensorRadius;
                    break;
                case GARDENER:
                    if (robot.type == myType)
                        robotRepulsion = myType.sensorRadius;
                    if(state != State.IDLE && state != State.SEARCHING_GARDEN_SPOT)
                        return null;
                    break;
            }

            if (myLocation.distanceTo(robotLocation) <= robotRepulsion)
                repulsionVectors.add(new Vector(robotLocation, myLocation));
        }

        // Avoid trees unless you're a scout
        for (TreeInfo tree : nearbyTrees) {
            MapLocation treeLocation = tree.getLocation();
            float treeRepulsion = repulsionRadius + (tree.getRadius()*1);

            switch(myType) {
                case SCOUT:
                    continue;
                case GARDENER:
                    if(state != State.IDLE && state != State.SEARCHING_GARDEN_SPOT)
                        return null;
            }

            if (myLocation.distanceTo(treeLocation) <= treeRepulsion)
                repulsionVectors.add(new Vector(treeLocation, myLocation));
        }

        // Avoid or run away from bullets
        for(BulletInfo bullet: nearbyBullets) {
            MapLocation bulletLocation = bullet.getLocation();
            if(willCollideWithMe(bullet)) {
                repulsionVectors.add(new Vector(new Direction(bulletLocation, myLocation).rotateLeftDegrees(25), 3));
            } else
                repulsionVectors.add(new Vector(new Direction(bulletLocation, myLocation), 0.25f));
        }

        // Stay away from borders
        try {
            Direction closestBorder = Map.getClosestBorder();
            if (!rc.onTheMap(myLocation, repulsionRadius)) {
                if(closestBorder.radians%Math.PI != 0) {
                    if (Signal.isBorderXDetected())
                        repulsionVectors.add(new Vector(Map.getClosestBorder(), 3));
                } else if (Signal.isBorderYDetected()) {
                    repulsionVectors.add(new Vector(Map.getClosestBorder(), 3));
                }
            }
        } catch (Exception e) {
            Debug.out("Get Repulsive Directions Exception");
            e.printStackTrace();
        }

        return repulsionVectors;
    }

    private static ArrayList<Vector> getAttractiveVectors(float attractionMin) {
        ArrayList<Vector> attractionVectors = new ArrayList<>();

        for (RobotInfo robot : nearbyEnemies) {
            MapLocation robotLocation = robot.getLocation();

            float attackRange = myRobotSightRadius;
            switch(myType){
                case SCOUT:
                    if(robot.getType() != RobotType.GARDENER && (state == State.SCOUTING || state == State.HARASSING || state == State.DETECTING_BORDER_X || state == State.DETECTING_BORDER_Y))
                        continue;
                    break;
                case LUMBERJACK:
                    if(state != State.STRIKING)
                        continue;
                    attackRange = GameConstants.LUMBERJACK_STRIKE_RADIUS;
                    break;
            }

            if (myLocation.distanceTo(robotLocation) >= myBodyRadius + robot.getRadius() + attackRange) {
                attractionVectors.add(new Vector(myLocation, robotLocation));
            }
        }


        for (TreeInfo tree : nearbyTrees) {
            MapLocation treeLocation = tree.getLocation();

            switch(state) {
                case SHAKING_TREES:
                    if (tree.containedBullets == 0)
                        continue;
                case CHOPPING:
                    break;
                default:
                    continue;
            }

            if (myLocation.distanceTo(treeLocation) <= (attractionMin + (tree.getRadius() * 2))) {
                attractionVectors.add(new Vector(myLocation, treeLocation));
                break;
            }
        }


        return attractionVectors;
    }

    public static Vector computeResultantVector(ArrayList<Vector> vectorList){
        if(vectorList.size() == 0)
            return null;

        Vector result = vectorList.get(0);
        vectorList.remove(0);

        for(Vector v: vectorList){
            float x1 = result.getDeltaX();
            float y1 = result.getDeltaY();
            float x2 = v.getDeltaX();
            float y2 = v.getDeltaY();

            result = new Vector((x1 + x2), (y1 + y2));
        }

        return result;
    }

    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }

    public static float getMaxMoveDistance(Direction direction){
        float stride = myStrideRadius;
        int maxPrecision = 4;
        int precisionCounter = 0;

        while(!rc.canMove(direction, stride) && precisionCounter++ < maxPrecision)
            stride /= 2;

        return stride;
    }
}
