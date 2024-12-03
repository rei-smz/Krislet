public class Belief {
    public final static String SEEN = "seen";
    public final static String UNKNOWN = "known";
    public final static String FAR = "far";
    public final static String CLOSE = "close";
    public final static String BALL = "ball";
    public final static String GOAL_L = "goal_l";
    public final static String GOAL_R = "goal_r";
    public final static String BALL_SAME_DIR = "ball_same_dir";
    public final static String BALL_DIFF_DIR = "ball_diff_dir";

    public static String buildBelief(String status, String object) {
        return status + "(" + object + ")";
    }
}
