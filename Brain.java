//
//	File:			Brain.java
//	Author:		Krzysztof Langner
//	Date:			1997/04/28
//
//    Modified by:	Paul Marlow

//    Modified by:      Edgar Acosta
//    Date:             March 4, 2008

import java.lang.Math;
import java.util.regex.*;
import jason.architecture.AgArch;

class Brain extends Thread implements SensorInput{
    private SendCommand m_krislet;            // robot which is controled by this brain
    private Memory m_memory;                // place where all information is stored
    volatile private boolean m_timeOver;
    private String m_playMode;
    private SoccerAgent m_agent;
    private char m_side;
    private boolean m_positioned;

    //---------------------------------------------------------------------------
    // This constructor:
    // - stores connection to krislet
    // - starts thread for this object
    public Brain(SendCommand krislet,
                 String team,
                 char side,
                 int number,
                 String playMode) {
        m_timeOver = false;
        m_krislet = krislet;
        m_memory = new Memory();
        m_playMode = playMode;
        m_side = side;
        m_positioned = false;

        m_agent = new SoccerAgent(side, number, playMode, m_memory);

        start();
    }


    //---------------------------------------------------------------------------
    // This is main brain function used to make decision
    // In each cycle we decide which command to issue based on
    // current situation. the rules are:
    //
    //	1. If you don't know where is ball then turn right and wait for new info
    //
    //	2. If ball is too far to kick it then
    //		2.1. If we are directed towards the ball then go to the ball
    //		2.2. else turn to the ball
    //
    //	3. If we dont know where is opponent goal then turn wait 
    //				and wait for new info
    //
    //	4. Kick ball
    //
    //	To ensure that we don't send commands to often after each cycle
    //	we waits one simulator steps. (This of course should be done better)

    // ***************  Improvements ******************
    // Allways know where the goal is.
    // Move to a place on my side on a kick_off
    // ************************************************

    public void run() {
        // first put it somewhere on my side
         if (!m_positioned) {
            m_krislet.move(-Math.random() * 52.5, 34 - Math.random() * 68.0);
            m_positioned = true;
            try {
                // Give some time for the move to complete
                Thread.sleep(2 * SoccerParams.simulator_step);
            } catch (Exception e) {}
        }

        while (!m_timeOver) {
            String intent = m_agent.getReasoningResult();
            PlayerAction action = getAction(intent);
            if (intent.equals("kick")) {
                String targetGoal = (m_side == 'l') ? "goal r" : "goal l";
                action.execute(m_memory.getObject(targetGoal));
            } else {
                action.execute(m_memory.getObject("ball"));
            }

            // sleep one step to ensure that we will not send
            // two commands in one cycle.
            try {
                Thread.sleep(2 * SoccerParams.simulator_step);
            } catch (Exception e) {
            }
        }
        m_krislet.bye();
    }

    private PlayerAction getAction(String name) {
        return switch (name) {
            case "turn_to" -> TurnTo.getInstance(m_krislet);
            case "dash" -> Dash.getInstance(m_krislet);
            case "kick" -> Kick.getInstance(m_krislet);
            default -> throw new IllegalArgumentException("No action available for: " + name);
        };
    }

    //===========================================================================
    // Here are suporting functions for implement logic


    //===========================================================================
    // Implementation of SensorInput Interface

    //---------------------------------------------------------------------------
    // This function sends see information
    public void see(VisualInfo info) {
        m_memory.store(info);
    }


    //---------------------------------------------------------------------------
    // This function receives hear information from player
    public void hear(int time, int direction, String message) {
    }

    //---------------------------------------------------------------------------
    // This function receives hear information from referee
    public void hear(int time, String message) {
        if (message.compareTo("time_over") == 0) {
            m_timeOver = true;
        }
    }
}
