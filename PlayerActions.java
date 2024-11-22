class Find implements PlayerAction {
    private SendCommand m_krislet;
    private static Find instance;
    private final static int TURN_DEG = 90;

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
        if (target != null && target.getDirection() != 0) {
            m_krislet.turn(target.getDirection());
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
            m_krislet.kick(KICK_PWR, target.getDirection());
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
            m_krislet.dash(power> MAX_DASH_PWR ? MAX_DASH_PWR : power);
        }
    }
}
