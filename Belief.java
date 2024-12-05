public class Belief {
    public final static String SEEN = "seen";
    public final static String UNKNOWN = "unknown";
    public final static String FAR = "far";
    public final static String CLOSE = "close";
    public final static String BALL = "ball";
    public final static String GOAL_L = "goal_l";
    public final static String GOAL_R = "goal_r";
    public final static String LINE_L = "line_l";
    public final static String LINE_R = "line_r";
    public final static String SAME = "same";
    public final static String DIFF = "diff";
    public final static String BALL_DIR = "ball_dir";
    public final static String BALL_ON = "ball_on";
    public final static String LEFT = "l";
    public final static String RIGHT = "r";


    public static String buildBelief(String functor, String term) {
        return functor + "(" + term + ")";
    }
}
