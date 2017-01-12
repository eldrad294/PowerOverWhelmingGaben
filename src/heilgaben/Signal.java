package heilgaben;

import java.io.Serializable;

/**
 * Signaling Class
 */
public class Signal extends BotState {
    public static final int NO_DATA = 0x000000;
    public static final int NO_CHANNEL = 0x000000;

    /**
     * CHANNEL MASKS
     */
    public static final int ARCHON = 0x000001;
    public static final int SCOUT = 0x000002;
    public static final int SOLDIER = 0x000003;
    public static final int TANK = 0x000004;
    public static final int GARDENER = 0x000005;
    public static final int LUMBERJACK = 0x000006;
    public static final int TREE = 0x000007;
    public static final int BORDER = 0x000008;
    public static final int ENEMY = 0x000009;
    public static final int CENTER = 0x00000a;
    public static final int NORTH_WEST = 0x00000b;
    public static final int SOUTH_EAST = 0x00000c;

    /**
     * CHANNELS
     */
    public static final int DATA_CHANNEL_X = 0x000010;
    public static final int DATA_CHANNEL_Y = 0x000020;
    public static final int COMMAND_CHANNEL = 0x000030;
    public static final int REPORT_CHANNEL = 0x000040;
    public static final int COUNT_CHANNEL = 0x000050;

    /**
     * COMMANDS
     */
    public static final int STOP = 0xf00000;

    public static final int DETECT = 0x000100;
    public static final int ATTACK = 0x000300;
    public static final int RETREAT = 0x000400;

    /**
     * REPORTS
     */
    public static final int DETECTED = 0x0f0000;

    public static void broadcastCommand(int channel, int command){
        try {
            rc.broadcast(channel, command);
        } catch (Exception e){
            Debug.out("Command Exception");
            e.printStackTrace();
        }
    }

    public static void broadcastReport(int channel, int report){
        try {
            rc.broadcast(channel, report);
        } catch (Exception e) {
            Debug.out("Report Exception");
            e.printStackTrace();
        }
    }

    public static void broadcastData(int channelX, int channelY, int[] data){
        try {
            if(channelX != NO_DATA)
                rc.broadcast(channelX, data[0]);
            if(channelY != NO_DATA)
                rc.broadcast(channelY, data[1]);
        } catch (Exception e) {
            Debug.out("Data Exception");
            e.printStackTrace();
        }
    }

    public static int receiveCommand(int channel){
        try {
            return rc.readBroadcast(channel);
        } catch (Exception e) {
            Debug.out("Receive Command Exception");
            e.printStackTrace();
            return NO_DATA;
        }
    }

    public static int receiveReport(int channel){

        try{
            return rc.readBroadcast(channel);
        } catch (Exception e) {
            Debug.out("Receive Command Exception");
            e.printStackTrace();
            return NO_DATA;
        }
    }

    public static int[] receiveData(int channelX, int channelY){
        try{
            int[] coord = {rc.readBroadcast(channelX), rc.readBroadcast(channelY)};
            return coord;
        } catch (Exception e) {
            Debug.out("Receive Command Exception");
            e.printStackTrace();
            int[] coord = {NO_CHANNEL, NO_CHANNEL};
            return coord;
        }
    }
}
