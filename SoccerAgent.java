import jason.architecture.AgArch;
import jason.asSemantics.*;
import jason.asSyntax.Literal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SoccerAgent extends AgArch {
    private static Logger logger = Logger.getLogger(SoccerAgent.class.getName());

    public SoccerAgent(char side,
                       int number,
                       String playMode) {
        // set up the Jason agent
        try {
            Agent ag = new Agent();
            new TransitionSystem(ag, null, null, this);
            ag.initAg();
            ag.load("demo.asl");
            ag.addBel(Literal.parseLiteral("side(" + side + ")"));
            ag.addBel(Literal.parseLiteral("number(" + number + ")"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Init error", e);
        }
    }

    public List<String> run() {
        List<String> intents = new ArrayList<>();
        try {
            getTS().reasoningCycle();

            // 获取当前的意图
            List<Intention> currentIntentions = getTS().getC().getIntentions();
            for (Intention intention : currentIntentions) {
                for (IntendedMeans means : intention) {
                    intents.add(means.getPlan().getTrigger().toString());
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Run error", e);
        }
        return intents;
    }

    // this method just add some perception for the agent
    @Override
    public List<Literal> perceive() {
        List<Literal> l = new ArrayList<>();
//        l.add(Literal.parseLiteral("x(10)"));
        //TODO
        return l;
    }

    @Override
    public boolean canSleep() {
        return false;
    }

    @Override
    public boolean isRunning() {
        return true;
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
