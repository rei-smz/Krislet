class Find implements PlayerAction {
    private SendCommand m_krislet;
    private static Find instance;
    private final static int TURN_DEG = 40;

    private Find(SendCommand krislet) {
        m_krislet = krislet;
    }

    public static Find getInstance(SendCommand krislet) {
        if (instance == null) {
            instance = new Find(krislet);
        }

        return instance;
    }

    @Override
    public void execute(ObjectInfo target) {
        if (target == null) {
            m_krislet.turn(TURN_DEG);
        }
    }
}

class TurnTo implements PlayerAction {
    private SendCommand m_krislet;
    private static TurnTo instance;
    private final static int DEFAULT_TURN = 40;  // Match original Brain behavior

    private TurnTo(SendCommand krislet) {
        m_krislet = krislet;
    }

    public static TurnTo getInstance(SendCommand krislet) {
        if (instance == null) {
            instance = new TurnTo(krislet);
        }

        return instance;
    }

    @Override
    public void execute(ObjectInfo target) {
        if (target == null) {
            // If no target visible, turn default amount
            m_krislet.turn(DEFAULT_TURN);
            System.out.println("TurnTo: No target, turning " + DEFAULT_TURN);
        } else if (target.getDirection() != 0) {
            // If target visible and not aligned, turn towards it
            m_krislet.turn(target.getDirection());
            System.out.println("TurnTo: Target found, turning " + target.getDirection());
        } else {
            System.out.println("TurnTo: Target aligned, no turn needed");
        }
    }
}

class Kick implements PlayerAction {
    private SendCommand m_krislet;
    private static Kick instance;
    private static final int KICK_PWR = 100;

    private Kick(SendCommand krislet) {
        m_krislet = krislet;
    }

    public static Kick getInstance(SendCommand krislet) {
        if (instance == null) {
            instance = new Kick(krislet);
        }

        return instance;
    }

    @Override
    public void execute(ObjectInfo target) {
        if (target != null) {
            System.out.println("Kick: Kicking towards goal at direction " + target.getDirection());
            m_krislet.kick(KICK_PWR, target.getDirection());
        } else {
            // Just turn to find the goal, like in original Brain.java
            System.out.println("Kick: No goal visible, turning to find it");
            m_krislet.turn(40);
        }
    }
}

class Dash implements PlayerAction {
    private SendCommand m_krislet;
    private static Dash instance;
    private static final float TARGET_DIST = 0.9f;
    private static final int MAX_DASH_PWR = 100;


    private Dash(SendCommand krislet) {
        m_krislet = krislet;
    }

    public static Dash getInstance(SendCommand krislet) {
        if (instance == null) {
            instance = new Dash(krislet);
        }

        return instance;
    }

    @Override
    public void execute(ObjectInfo target) {
        if (target != null && target.getDistance() > TARGET_DIST) {
            float power = target.getDistance() * 10;
            power = Math.min(power, MAX_DASH_PWR);
            System.out.println("Dash: Distance to target = " + target.getDistance());
            m_krislet.dash(power);
        }
    }
}
