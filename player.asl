// Initial beliefs and goals
!handle_ball.

// Ball not visible - search for it
+!handle_ball : unknown(ball) <- 
    find(ball);
    !handle_ball.

// Ball visible but not aligned - turn towards it
+!handle_ball : far(ball) & ball_dir(diff) <- 
    turn_to(ball);
    !handle_ball.

// Ball visible and aligned - dash to it
+!handle_ball : far(ball) & ball_dir(same) <- 
    dash(ball);
    !handle_ball.

// At ball, look for correct goal based on side
// Left side player looks for right goal
+!handle_ball : close(ball) & side(l) & unknown(goal_r) <- 
    find(goal_r);  // Using kick to turn to find goal
    !handle_ball.

// Right side player looks for left goal
+!handle_ball : close(ball) & side(r) & unknown(goal_l) <-
    find(goal_l);  // Using kick to turn to find goal
    !handle_ball.

// Kick to correct goal when visible
// Left side player kicks to right goal
+!handle_ball : close(ball) & side(l) & seen(goal_r) <-
    kick(goal_r);
    !handle_ball.

// Right side player kicks to left goal
+!handle_ball : close(ball) & side(r) & seen(goal_l) <-
    kick(goal_l);
    !handle_ball.

// Default fallback - keep looking
-!handle_ball : true <-
    find(ball);
    !handle_ball.