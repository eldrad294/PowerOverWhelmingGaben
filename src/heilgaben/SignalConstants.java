package heilgaben;

public class SignalConstants {
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
    public static final int GLOBAL_STATE = 0x00000d;

    /**
     * CHANNELS
     */
    public static final int DATA_CHANNEL_X = 0x000010;
    public static final int DATA_CHANNEL_Y = 0x000020;
    public static final int COMMAND_CHANNEL = 0x000030;
    public static final int REPORT_CHANNEL = 0x000040;
    public static final int COUNT_CHANNEL = 0x000050;

    public static final int SCOUT_START = 0x0026E8;
    public static final int SCOUT_END = 0x002710;

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

    /**
     * GLOBAL STATES
     */
    public static final int OPENING = 0x100000;
    public static final int MIDGAME = 0x200000;
    public static final int ENDGAME = 0x300000;
}
