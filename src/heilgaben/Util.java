package heilgaben;

import battlecode.common.*;

public class Util extends BotState {
    public static Direction getRobotSpawnDirection(RobotType robotType) {
        float interval = getRadialSpawnInterval(robotType.bodyRadius);

        try {
            for (float radians = 0; radians < Math.PI * 2; radians += interval) {
                Direction spawnDirection = new Direction(radians);
                if(robotType == RobotType.GARDENER) {
                    if (rc.canHireGardener(spawnDirection))
                        return spawnDirection;
                }
                else {
                    if(rc.canBuildRobot(robotType, spawnDirection))
                        return spawnDirection;
                }
            }
        } catch (Exception e){
            Debug.out("Get Spawn Direction Exception");
            e.printStackTrace();
        }

        return null;
    }

    private static float getRadialSpawnInterval(float spawnObjectRadius) {
        float radius = myBodyRadius + spawnObjectRadius;
        double perimeter = 2 * Math.PI * radius;
        int spawnCount = (int) (Math.floor(perimeter) / (spawnObjectRadius * 2));
        return (float) (Math.PI * 2) / spawnCount;
    }

    public static void setCenter() {
        int xavg = 0, yavg = 0;

        for (int i = 0; i < ourStartingLocations.length; i++) {
            xavg += ourStartingLocations[i].x;
            yavg += ourStartingLocations[i].y;

            xavg += enemyStartingLocations[i].x;
            yavg += enemyStartingLocations[i].y;
        }

        center = new MapLocation(Math.round(xavg / (ourStartingLocations.length + enemyStartingLocations.length)), Math.round(yavg / (ourStartingLocations.length + enemyStartingLocations.length)));
        Debug.out("Center at: " + center);
    }
}
