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
    private boolean m_isPositioning;
    private Agent m_agent;
    private char m_side;

    public SoccerAgent(char side,
                      int number,
                      String playMode,
                      Memory memory) {
        m_running = false;
        m_currentIntent = "";
        m_memory = memory;
        m_side = side;
        
        // Initialize Jason agent immediately for play_on mode
        initializeJasonAgent(side);
    }

    private void initializeJasonAgent(char side) {
        try {
            m_agent = new Agent();
            new TransitionSystem(m_agent, null, null, this);
            m_agent.initAg();
            m_agent.load("player.asl");
            m_agent.addBel(Literal.parseLiteral("side(" + side + ")"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Init error", e);
        }
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
        
        ObjectInfo ball = m_memory.getObject("ball");
        ObjectInfo goalL = m_memory.getObject("goal l");
        ObjectInfo goalR = m_memory.getObject("goal r");

        System.out.println("\nPerception Cycle:");
        System.out.println("Side: " + m_side);
        
        if (ball == null) {
            System.out.println("Ball not visible");
            l.add(Literal.parseLiteral(Belief.BALL_UNKNOWN));
        } else {
            System.out.println("Ball: distance=" + ball.getDistance() + ", direction=" + ball.getDirection());
            if (ball.getDistance() > TARGET_BALL_DIST) {
                l.add(Literal.parseLiteral(Belief.BALL_FAR));
            } else {
                l.add(Literal.parseLiteral(Belief.BALL_CLOSE));
            }

            if (ball.getDirection() != 0) {
                l.add(Literal.parseLiteral(Belief.BALL_SEEN));
            } else {
                l.add(Literal.parseLiteral(Belief.BALL_UNSEEN));
            }
        }

        // Log goal perceptions
        String targetGoal = (m_side == 'l') ? "goal r" : "goal l";
        System.out.println("Looking for goal: " + targetGoal);

        if (m_side == 'l')
        {
            if(goalR != null)
            {
                System.out.println("Right goal visible: distance=" + goalR.getDistance() + ", direction=" + goalR.getDirection());
                l.add(Literal.parseLiteral(Belief.OPPONENT_GOAL_KNOWN));
            }
            else
            {
                System.out.println("Right goal not visible");
                l.add(Literal.parseLiteral(Belief.OPPONENT_GOAL_UNKNOWN));
            }

            if(goalL != null)
            {
                System.out.println("Left goal visible: distance=" + goalL.getDistance() + ", direction=" + goalL.getDirection());
                l.add(Literal.parseLiteral(Belief.OWN_GOAL_KNOWN));
            }
            else
            {
                System.out.println("Left goal not visible");
                l.add(Literal.parseLiteral(Belief.OWN_GOAL_UNKNOWN));
            }
        }
        else
        {
            if(goalL != null)
            {
                System.out.println("Left goal visible: distance=" + goalL.getDistance() + ", direction=" + goalL.getDirection());
                l.add(Literal.parseLiteral(Belief.OPPONENT_GOAL_KNOWN));
            }
            else
            {
                System.out.println("Left goal not visible");
                l.add(Literal.parseLiteral(Belief.OPPONENT_GOAL_UNKNOWN));
            }

            if(goalR != null)
            {
                System.out.println("Right goal visible: distance=" + goalR.getDistance() + ", direction=" + goalR.getDirection());
                l.add(Literal.parseLiteral(Belief.OWN_GOAL_KNOWN));
            }
            else
            {
                System.out.println("Right goal not visible");
                l.add(Literal.parseLiteral(Belief.OWN_GOAL_UNKNOWN));
            }

        }
        

        System.out.println("Generated beliefs: " + l);
        return l;
    }

    public void startGameplay(String playMode) {
        m_isPositioning = false;
        initializeJasonAgent(m_side);
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