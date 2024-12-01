!play_soccer.

+!play_soccer : true <- 
    !move_to_goal;
    !play_soccer.

+!move_to_goal : side(r) & goal_r_unknown <- 
    !find_goal;

+!find_goal : true <-
    turn
    !move_to_goal

+!move_to_goal : side(r) & goal_r_known <-
    dash;

