// Initial beliefs and goals
!play_soccer.

// Main play_soccer plan
+!play_soccer : true <- 
    !handle_ball;
    !play_soccer.

// Ball not visible - search for it
+!handle_ball : ball_unknown <- 
    turn_to;
    !handle_ball.

// Ball visible but not aligned - turn towards it
+!handle_ball : ball_far & ball_diff_dir <- 
    turn_to;
    !handle_ball.

// Ball visible and aligned - dash to it
+!handle_ball : ball_far & ball_same_dir <- 
    dash;
    !handle_ball.

// At ball, look for correct goal based on side
// Left side player looks for right goal
+!handle_ball : ball_close & side(l) & goal_r_unknown <- 
    kick;  // Using kick to turn to find goal
    !handle_ball.

// Right side player looks for left goal
+!handle_ball : ball_close & side(r) & goal_l_unknown <- 
    kick;  // Using kick to turn to find goal
    !handle_ball.

// Kick to correct goal when visible
// Left side player kicks to right goal
+!handle_ball : ball_close & side(l) & goal_r_known <- 
    kick;
    !handle_ball.

// Right side player kicks to left goal
+!handle_ball : ball_close & side(r) & goal_l_known <- 
    kick;
    !handle_ball.

// Default behavior - keep turning to search
-!handle_ball : true <- 
    turn_to;
    !handle_ball.