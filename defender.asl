// Initial beliefs and goals
!defend.

// When we can't see the ball
+!defend : unknown(ball) <-
    find(ball);
    !defend.

// Ball is on their side - just track it
+!defend : far(ball) & ball_dir(diff) <-
    turn_to(ball);
    !defend.

// Ball is on our side and aligned - DASH to it
+!defend : far(ball) & ball_dir(same) & side(l) & ball_on(l) <-
    dash(ball);
    !defend.

// Ball is on our side and aligned - DASH to it
+!defend : far(ball) & ball_dir(same) & side(r) & ball_on(r) <-
    dash(ball);
    !defend.

// Ball is close and we can see target goal - kick
+!defend : close(ball) & side(l) & seen(goal_r) <- 
    kick(goal_r);
    !defend.

+!defend : close(ball) & side(r) & seen(goal_l) <- 
    kick(goal_l);
    !defend.

// Ball is close but can't see target goal - keep turning to find it
+!defend : close(ball) & side(l) & unknown(goal_r) & seen(goal_l) <-
    find(goal_r);
    !defend.

+!defend : close(ball) & side(r) & unknown(goal_l) & seen(goal_r) <-
    find(goal_l);
    !defend.

+!defend : close(ball) & side(l) & unknown(goal_r) & not seen(line_l) <-
    kick(ball);
    !defend.

+!defend : close(ball) & side(r) & unknown(goal_l) & not seen(line_r) <-
    kick(ball);
    !defend.

+!defend : true <-
    find(ball);
    !defend.