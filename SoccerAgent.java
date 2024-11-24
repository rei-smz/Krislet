import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SoccerAgent extends AgArch {
    private static Logger logger = Logger.getLogger(SoccerAgent.class.getName());
    private Memory m_memory;
    private boolean m_running;
    private String m_currentIntent;

    public SoccerAgent(char side,
                       int number,
                       String playMode,
                       Memory memory) {
        // set up the Jason agent
        try {
            Agent ag = new Agent();
            new TransitionSystem(ag, null, null, this);
            ag.initAg();
//            ag.load(playMode + ".asl");
            ag.loadInitialAS("player.asl");
            ag.addBel(Literal.parseLiteral("side(" + side + ")"));
            ag.addBel(Literal.parseLiteral("number(" + number + ")"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Init error", e);
        }
        m_running = false;
        m_currentIntent = "";
        m_memory = memory;
    }

    public String getReasoningResult() {
        run();
        return m_currentIntent;
    }

    public void run() {
        m_running = true;
        try {
            while (isRunning()) {
                // calls the Jason engine to perform one reasoning cycle
                logger.fine("Reasoning....");
                getTS().reasoningCycle();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Run error", e);
        }
    }

    @Override
    public void act(ActionExec action) {
        getTS().getLogger().info("Agent " + getAgName() + " is doing: " + action.getActionTerm());
        // set that the execution was ok
        action.setResult(true);
        actionExecuted(action);
        m_currentIntent = action.getActionTerm().toString();
        m_running = false;
    }

    // this method just add some perception for the agent
    @Override
    public List<Literal> perceive() {
        List<Literal> l = new ArrayList<>();
        final float TARGET_BALL_DIST = 0.9f;
//        l.add(Literal.parseLiteral("x(10)"));
        //TODO
        ObjectInfo ball = m_memory.getObject("ball");
        ObjectInfo goalL = m_memory.getObject("goal l");
        ObjectInfo goalR = m_memory.getObject("goal r");
        if (ball == null) {
            l.add(Literal.parseLiteral(Belief.BALL_UNKNOWN));
        } else {
            if (ball.getDistance() > TARGET_BALL_DIST) {
                l.add(Literal.parseLiteral(Belief.BALL_FAR));
            } else {
                l.add(Literal.parseLiteral(Belief.BALL_CLOSE));
            }

            if (ball.getDirection() != 0) {
                l.add(Literal.parseLiteral(Belief.BALL_DIFF_DIR));
            } else {
                l.add(Literal.parseLiteral(Belief.BALL_SAME_DIR));
            }
        }

        if (goalL == null) {
            l.add(Literal.parseLiteral(Belief.GOAL_L_UNKNOWN));
        } else {
            l.add(Literal.parseLiteral(Belief.GOAL_L_KNOWN));
        }

        if (goalR == null) {
            l.add(Literal.parseLiteral(Belief.GOAL_R_UNKNOWN));
        } else {
            l.add(Literal.parseLiteral(Belief.GOAL_R_KNOWN));
        }
        return l;
    }

    @Override
    public boolean canSleep() {
        return false;
    }

    @Override
    public boolean isRunning() {
        return m_running;
    }

    // Not used methods
    // This simple agent does not need messages/control/...
    @Override
    public void sendMsg(jason.asSemantics.Message m) throws Exception {
    }

    @Override
    public void broadcast(jason.asSemantics.Message m) throws Exception {
    }

    @Override
    public void checkMail() {
    }
}
