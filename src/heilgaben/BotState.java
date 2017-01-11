package heilgaben;

import battlecode.common.*;

public class BotState {
    /* Robot Info */
    static RobotController rc;
    public static int myID;
    public static MapLocation myLocation;

    /* Type Info */
    public static RobotType myType;
    public static float myBodyRadius;
    public static float myBulletSightRadius;
    public static float myRobotSightRadius;
    public static float myAttackPower;
    public static float myCost;
    public static float myBulletSpeed;
    public static int myBytecodeLimit;
    public static int myBuildCooldown;
    public static float myMaxHealth;
    public static RobotType mySpawnSource;
    public static float myStrideRadius;

    /* Team Info */
    public static Team myTeam;
    public static Team enemyTeam;

    /* Surroundings Info */
    public static RobotInfo[] nearbyRobots;
    public static RobotInfo[] nearbyEnemies;
    public static RobotInfo[] nearbyAllies;
    public static TreeInfo[] nearbyTrees;
    public static BulletInfo[] nearbyBullets;

    public static void init(RobotController inRc) {
        /* Robot Info */
        rc = inRc;
        myID = rc.getID();
        myLocation = rc.getLocation();

        /* Type Info */
        myType = rc.getType();
        myBodyRadius = myType.bodyRadius;
        myBulletSightRadius = myType.bulletSightRadius;
        myRobotSightRadius = myType.sensorRadius;
        myAttackPower = myType.attackPower;
        myCost = myType.bulletCost;
        myBulletSpeed = myType.bulletSpeed;
        myBytecodeLimit = myType.bytecodeLimit;
        myBuildCooldown = myType.buildCooldownTurns;
        myMaxHealth = myType.maxHealth;
        mySpawnSource = myType.spawnSource;
        myStrideRadius = myType.strideRadius;

        /* Team Info */
        myTeam = rc.getTeam();
        enemyTeam = rc.getTeam().opponent();
    }

    public static void update() {
        /* Robot Info */
        myLocation = rc.getLocation();

        /* Surroundings Info */
        nearbyRobots = rc.senseNearbyRobots();
        nearbyEnemies = rc.senseNearbyRobots(-1, enemyTeam);
        nearbyAllies = rc.senseNearbyRobots(-1, myTeam);
        nearbyBullets = rc.senseNearbyBullets();
        nearbyTrees = rc.senseNearbyTrees();

        donate();
    }

    private static boolean donate() {
        try {
            if (rc.getTeamBullets() > 310) {
                rc.donate(10);
                return true;
            }
            return false;

        } catch (Exception e){
            Debug.out("Donate");
            e.printStackTrace();
            return false;
        }
    }
}
