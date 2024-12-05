import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SoccerAgent extends AgArch {
    private static Logger logger = Logger.getLogger(SoccerAgent.class.getName());
    private Memory m_memory;
    private boolean m_running;
    private Structure m_currentIntent;

    public SoccerAgent(char side,
                       int number,
                       Memory memory) {
        // set up the Jason agent
        try {
            Agent ag = new Agent();
            new TransitionSystem(ag, null, null, this);
            ag.initAg();
//            ag.load("player.asl");
            if (number <= 2) { // Assuming position 2 is defender
                ag.load("defender.asl");
            } else {
                ag.load("player.asl"); // Default player
            }
            ag.addBel(Literal.parseLiteral("side(" + side + ")"));
            ag.addBel(Literal.parseLiteral("number(" + number + ")"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Init error", e);
        }
        m_running = false;
        m_currentIntent = null;
        m_memory = memory;
    }

    public Structure getReasoningResult() {
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
//        getTS().getLogger().info("Agent " + getAgName() + " is doing: " + action.getActionTerm());
        // set that the execution was ok
        action.setResult(true);
        actionExecuted(action);
//        m_currentIntent = new Pair<>(action.getActionTerm().getFunctor(), action.getActionTerm().getTerm(0).toString());
        m_currentIntent = action.getActionTerm();
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
        ObjectInfo flagC = m_memory.getObject("flag c");
        ObjectInfo flagPLT = m_memory.getObject("flag p l t");
        ObjectInfo flagPLB = m_memory.getObject("flag p l b");
        ObjectInfo flagPRT = m_memory.getObject("flag p r t");
        ObjectInfo flagPRB = m_memory.getObject("flag p r b");

        List<ObjectInfo> lines = m_memory.getObjects("line");
        final int LINE_CNT = 4;
        final int LINE_L = 0;
        final int LINE_R = 1;
        final int LINE_T = 2;
        final int LINE_B = 3;
        boolean[] lineVisible = {false, false, false, false};
        for (ObjectInfo line : lines) {
            char lineType = ((LineInfo)line).m_kind;
            switch (lineType) {
                case 'l':
                    lineVisible[LINE_L] = true;
                    break;
                case 'r':
                    lineVisible[LINE_R] = true;
                    break;
                case 't':
                    lineVisible[LINE_T] = true;
                    break;
                case 'b':
                    lineVisible[LINE_B] = true;
                    break;
            }
        }
//        logger.info(lineL.toString() + " " + lineR.toString());
//        if (lineL != null) {
//            logger.info("line l seen");
//        }

        if (ball == null) {
            l.add(Literal.parseLiteral(Belief.buildBelief(Belief.UNKNOWN, Belief.BALL)));
        } else {
            l.add(Literal.parseLiteral(Belief.buildBelief(Belief.SEEN, Belief.BALL)));
            if (ball.getDistance() > TARGET_BALL_DIST) {
                l.add(Literal.parseLiteral(Belief.buildBelief(Belief.FAR, Belief.BALL)));
            } else {
                l.add(Literal.parseLiteral(Belief.buildBelief(Belief.CLOSE, Belief.BALL)));
            }

            if (ball.getDirection() != 0) {
                l.add(Literal.parseLiteral(Belief.buildBelief(Belief.BALL_DIR, Belief.DIFF)));
            } else {
                l.add(Literal.parseLiteral(Belief.buildBelief(Belief.BALL_DIR, Belief.SAME)));
            }

            if (flagC == null){
                if (lineVisible[LINE_L]) {
//                    logger.info(String.valueOf(((LineInfo) lineL).m_kind));
                    l.add(Literal.parseLiteral(Belief.buildBelief(Belief.BALL_ON, Belief.LEFT)));
                } else if (lineVisible[LINE_R]) {
//                    logger.info("line r seen");
                    l.add(Literal.parseLiteral(Belief.buildBelief(Belief.BALL_ON, Belief.RIGHT)));
                } else {
                    if ((flagPLT != null && lineVisible[LINE_T]) || (flagPLB != null && lineVisible[LINE_B])) {
                        l.add(Literal.parseLiteral(Belief.buildBelief(Belief.BALL_ON, Belief.LEFT)));
                    } else if ((flagPRT != null && lineVisible[LINE_T]) || (flagPRB != null && lineVisible[LINE_B])) {
                        l.add(Literal.parseLiteral(Belief.buildBelief(Belief.BALL_ON, Belief.RIGHT)));
                    }
                }
            } else {
                if (flagC.getDistance() > ball.getDistance()) {
                    if (lineVisible[LINE_L]) {
                        l.add(Literal.parseLiteral(Belief.buildBelief(Belief.BALL_ON, Belief.RIGHT)));
                    } else if (lineVisible[LINE_R]) {
                        l.add(Literal.parseLiteral(Belief.buildBelief(Belief.BALL_ON, Belief.LEFT)));
                    }
                } else {
                    if (lineVisible[LINE_L]) {
                        l.add(Literal.parseLiteral(Belief.buildBelief(Belief.BALL_ON, Belief.LEFT)));
                    } else if (lineVisible[LINE_R]) {
                        l.add(Literal.parseLiteral(Belief.buildBelief(Belief.BALL_ON, Belief.RIGHT)));
                    }
                }
            }
        }

        if (goalL == null) {
            l.add(Literal.parseLiteral(Belief.buildBelief(Belief.UNKNOWN, Belief.GOAL_L)));
        } else {
            l.add(Literal.parseLiteral(Belief.buildBelief(Belief.SEEN, Belief.GOAL_L)));
        }

        if (goalR == null) {
            l.add(Literal.parseLiteral(Belief.buildBelief(Belief.UNKNOWN, Belief.GOAL_R)));
        } else {
            l.add(Literal.parseLiteral(Belief.buildBelief(Belief.SEEN, Belief.GOAL_R)));
        }
//        logger.info(l.toString());
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
