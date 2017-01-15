package heilgaben;

public enum State {
    NONE,
    INIT,

    // SCOUT
    DETECTING_BORDER_X,
    DETECTING_BORDER_Y,
    SIGNALING_BORDERS,
    HARASS,

    // GARDENER
    SEARCHING_GARDEN_SPOT,
    PLANTING_GARDEN,
    TENDING_GARDEN,
}
