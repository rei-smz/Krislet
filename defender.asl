// Initial beliefs and goals
!play_soccer.

// Main play_soccer plan
+!play_soccer : true <- 
    !defend;
    !play_soccer.

// When we can't see the ball
+!defend : ball_unknown <- 
    turn_to;
    !defend.

// Ball is on our side but not aligned - keep turning until aligned
+!defend : ball_on_our_side & ball_diff_dir <- 
    turn_to;
    !defend.

// Ball is on our side and aligned - DASH to it
+!defend : ball_on_our_side & ball_same_dir <- 
    dash;
    !defend.

// Ball is on their side - just track it
+!defend : ball_on_their_side <- 
    turn_to;
    !defend.

// Ball is close and we can see target goal - kick
+!defend : ball_close & side(l) & goal_r_known <- 
    kick;
    !defend.

+!defend : ball_close & side(r) & goal_l_known <- 
    kick;
    !defend.

// Ball is close but can't see target goal - keep turning to find it
+!defend : ball_close & side(l) & goal_r_unknown <- 
    turn_to;
    !defend.

+!defend : ball_close & side(r) & goal_l_unknown <- 
    turn_to;
    !defend.

// Default fallback - keep looking
-!defend : true <- 
    turn_to;
    !defend.