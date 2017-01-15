package heilgaben;

import battlecode.common.*;

import java.util.ArrayList;
import heilgaben.Vector;

/**
 * Navigation Class
 */
public class Nav extends BotState {
    public static boolean move(Direction moveDirection) {
        try {
            if(rc.hasMoved())
                return false;

            if(moveDirection == null)
                return false;

            float maxMoveDistance = Nav.getMaxMoveDistance(moveDirection);
            if (rc.canMove(moveDirection, maxMoveDistance)) {
                rc.move(moveDirection, maxMoveDistance);

                MapLocation nextLocation = new MapLocation(myLocation.x + moveDirection.getDeltaX(1), myLocation.y + moveDirection.getDeltaY(1));
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

        if(destination != myLocation)
            allDirections.add(new Vector(myLocation, destination));

        ArrayList<Vector> repulsiveDirections = getRepulsiveVectors(myBodyRadius*2);
        allDirections.addAll(repulsiveDirections);

//        ArrayList<Direction> attractiveDirections = getAttractiveDirections(myBodyRadius*2);
//        allDirections.addAll(attractiveDirections);


        Vector resultant = computeResultantVector(allDirections);
        if(resultant != null)
            return resultant.direction;
        return null;
    }

    public static ArrayList<Vector> getRepulsiveVectors(float repulsionRadius){
        ArrayList<Vector> repulsionDirections = new ArrayList<>();

        for(RobotInfo robot: nearbyRobots) {
            MapLocation robotLocation = robot.getLocation();
            if (myLocation.distanceTo(robotLocation) <= (repulsionRadius + (robot.getRadius()*2)))
                repulsionDirections.add(new Vector(robotLocation, myLocation));
        }

        if(myType != RobotType.SCOUT) {
            for (TreeInfo tree : nearbyTrees) {
                MapLocation treeLocation = tree.getLocation();
                if (myLocation.distanceTo(treeLocation) <= (repulsionRadius + (tree.getRadius() * 1)))
                    repulsionDirections.add(new Vector(treeLocation, myLocation));
            }
        }

        for(BulletInfo bullet: nearbyBullets) {
            MapLocation bulletLocation = bullet.getLocation();
            if(willCollideWithMe(bullet)) {
                repulsionDirections.add(new Vector(bulletLocation, myLocation).rotateLeftDegrees(90));
            }
        }

        try {
            Direction closestBorder = Util.getClosestBorder();
            if (!rc.onTheMap(myLocation, repulsionRadius)) {
                if(closestBorder.radians%Math.PI != 0) {
                    if (Signal.isBorderXDetected())
                        repulsionDirections.add(new Vector(Util.getClosestBorder(), 1));
                } else if (Signal.isBorderYDetected()) {
                    repulsionDirections.add(new Vector(Util.getClosestBorder(), 1));
                }
            }
        } catch (Exception e) {
            Debug.out("Get Repulsive Directions Exception");
            e.printStackTrace();
        }

        return repulsionDirections;
    }

    private static ArrayList<Direction> getAttractiveDirections(float attractionMin) {
        ArrayList<Direction> attractionDirections = new ArrayList<>();

        for(RobotInfo robot: nearbyRobots) {
            MapLocation robotLocation = robot.getLocation();
            if (myLocation.distanceTo(robotLocation) >= attractionMin + (robot.getRadius()*2))
                attractionDirections.add(new Direction(myLocation, robotLocation));
        }

        for(TreeInfo tree: nearbyTrees) {
            MapLocation treeLocation = tree.getLocation();
            if (myLocation.distanceTo(treeLocation) <= (attractionMin + (tree.getRadius()*2)))
                attractionDirections.add(new Direction(myLocation, treeLocation));
        }

        return attractionDirections;
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
