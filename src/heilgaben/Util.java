package heilgaben;

import battlecode.common.*;

public class Util extends BotState {

    public static float getRadialSpawnInterval(float spawnRobotRadius) {
        float radius = myBodyRadius + spawnRobotRadius;
        double perimeter = 2 * Math.PI * radius;
        int spawnCount = (int) (Math.floor(perimeter) / (spawnRobotRadius * 2));
        return (float) (Math.PI * 2) / spawnCount;
    }

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
}
