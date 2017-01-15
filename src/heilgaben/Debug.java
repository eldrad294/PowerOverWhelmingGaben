package heilgaben;

import battlecode.common.MapLocation;

/**
 * Created by Alarak on 11/01/2017.
 */
public class Debug extends BotState {
    public static void out(String message){
        System.out.println(myType + ": " + message);
    }

    public static void out(float x, float y) { out("(" + x + ", " + y + ")"); }

    public static void out(int x, int y) { out("(" + x + ", " + y + ")"); }

    public static void drawMapBorder() {
        MapLocation topLeft = new MapLocation(border[0], border[1]);
        MapLocation topRight = new MapLocation(border[0], border[3]);
        MapLocation bottomLeft = new MapLocation(border[2], border[1]);
        MapLocation bottomRight = new MapLocation(border[2], border[3]);

        try {
            rc.setIndicatorLine(topLeft, topRight, 0, 0, 0);
            rc.setIndicatorLine(topLeft, bottomLeft, 0, 0, 0);
            rc.setIndicatorLine(topRight, bottomRight, 0, 0, 0);
            rc.setIndicatorLine(bottomRight, bottomLeft, 0, 0, 0);
        } catch (Exception e) {
            out( "Draw Map Border Exception");
        }
    }
}
