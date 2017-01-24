package heilgaben;

import battlecode.common.*;

import static heilgaben.SignalConstants.*;

public class Map extends BotState {
    public static void initCenter() throws GameActionException{
        if(Signal.receiveSignal(CENTER) == DETECTED) {
            float[] centerArray = Signal.receiveCoordinate(CENTER | DATA_CHANNEL_X, CENTER | DATA_CHANNEL_Y);
            center = new MapLocation(centerArray[0], centerArray[1]);

            return;
        }

        int xavg = 0, yavg = 0;

        for (int i = 0; i < ourStartingLocations.length; i++) {
            xavg += ourStartingLocations[i].x;
            yavg += ourStartingLocations[i].y;

            xavg += enemyStartingLocations[i].x;
            yavg += enemyStartingLocations[i].y;
        }

        center = new MapLocation(Math.round(xavg / (ourStartingLocations.length + enemyStartingLocations.length)), Math.round(yavg / (ourStartingLocations.length + enemyStartingLocations.length)));
        float[] centerArray = {center.x, center.y};
        Signal.broadcastCoordinate(CENTER | DATA_CHANNEL_X, CENTER | DATA_CHANNEL_Y, centerArray);
        Signal.broadcastSignal(CENTER, DETECTED);
    }

    public static Direction getClosestBorder(){
        float west = myLocation.x - border[0];
        float north = border[1] - myLocation.y;
        float east = border[2] - myLocation.x;
        float south = myLocation.y - border[3];

        float closest = west;
        Direction direction = new Direction(Direction.getWest().radians);

        if(closest > east) {
            closest = east;
            direction = new Direction(Direction.getEast().radians);
        }

        if(closest > north){
            closest = north;
            direction = new Direction(Direction.getNorth().radians);
        }

        if(closest > south){
            direction = new Direction(Direction.getSouth().radians);
        }

        try {
            //rc.setIndicatorLine(myLocation, myLocation.add(direction, closest), 255, 255, 0);
        } catch (Exception e){

        }

        return direction.opposite();
    }

    public static void initBorders() throws GameActionException{
        if(Signal.receiveSignal(BORDER) == DETECTED) {
            border = Signal.receiveBorders();
        }
    }

    public static void updateBorders() throws GameActionException{
        if(Signal.receiveSignal(BORDER) != DETECTED)
            return;

        border = Signal.receiveBorders();

        // WEST
        if(myLocation.x < border[0]) {
            float[] westEast = {0, 0};
            westEast[0] = myLocation.x - myBodyRadius;
            westEast[1] = center.x + (center.x - westEast[0]) + (2*myBodyRadius);

            Signal.broadcastCoordinate(DATA_CHANNEL_X | NORTH_WEST, DATA_CHANNEL_X | SOUTH_EAST, westEast);
            return;
        }

        // NORTH
        if(myLocation.y > border[1]){
            float[] northSouth = {0, 0};

            northSouth[0] = myLocation.y + myBodyRadius;
            northSouth[1] = center.y - (northSouth[1] - center.y) - (2*myBodyRadius);

            Signal.broadcastCoordinate(DATA_CHANNEL_Y | NORTH_WEST, DATA_CHANNEL_Y | SOUTH_EAST, northSouth);
            return;
        }

        // EAST
        if(myLocation.x > border[2]) {
            float[] westEast = {0, 0};

            westEast[1] = myLocation.x + myBodyRadius;
            westEast[0] = center.x - (westEast[1] - center.x) - (2*myBodyRadius);

            Signal.broadcastCoordinate(DATA_CHANNEL_X | NORTH_WEST, DATA_CHANNEL_X | SOUTH_EAST, westEast);
            return;
        }

        // SOUTH
        if( myLocation.y < border[3]) {
            float[] northSouth = {0, 0};

            northSouth[1] = myLocation.y - myBodyRadius;
            northSouth[0] = center.y + (center.y - northSouth[1]) + (2*myBodyRadius);

            Signal.broadcastCoordinate(DATA_CHANNEL_Y | NORTH_WEST, DATA_CHANNEL_Y | SOUTH_EAST, northSouth);
            return;
        }

        try {
            rc.setIndicatorDot(new MapLocation(border[0], border[1]), 255, 255, 0);
            rc.setIndicatorDot(new MapLocation(border[2], border[3]), 255, 255, 0);
        } catch (Exception e){
            Debug.out("Indicator Dot Exception: (" + border[0] + ", " + border[1] + ") - (" + border[2] + ", " + border[3] + ")");
            e.printStackTrace();
        }
    }

    public static TreeInfo getClosestNonemptyBulletTree(){
        TreeInfo closestBulletTree = null;
        for(TreeInfo tree : nearbyTrees){
            if(tree.getContainedBullets() > 0)
                if(closestBulletTree == null || myLocation.distanceTo(tree.location) < myLocation.distanceTo(closestBulletTree.location))
                    closestBulletTree = tree;
        }

        return closestBulletTree;
    }
}
