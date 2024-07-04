package fr.uga.pddl4j.myproject;

import fr.uga.pddl4j.heuristics.state.StateHeuristic;
import fr.uga.pddl4j.planners.InvalidConfigurationException;
import fr.uga.pddl4j.planners.LogLevel;
import fr.uga.pddl4j.planners.Planner;
import fr.uga.pddl4j.planners.statespace.HSP;

public class PlannerConfiguration {

    public static void main(String[] args) {
        // The path to the benchmarks directory
        final String benchmark = "src/main/java/fr/uga/pddl4j/myproject/benchmark/";

        // Gets the default configuration from the planner
        fr.uga.pddl4j.planners.PlannerConfiguration config = HSP.getDefaultConfiguration();
        // Sets the domain of the problem to solve
        config.setProperty(HSP.DOMAIN_SETTING, benchmark + "domain.pddl");
        // Sets the problem to solve
        config.setProperty(HSP.PROBLEM_SETTING, benchmark + "problems/p02.pddl");
        // Sets the timeout allocated to the search.
        config.setProperty(HSP.TIME_OUT_SETTING, 1000);
        // Sets the log level
        config.setProperty(HSP.LOG_LEVEL_SETTING, LogLevel.INFO);
        // Sets the heuristic used to search
        config.setProperty(HSP.HEURISTIC_SETTING, StateHeuristic.Name.MAX);
        // Sets the weight of the heuristic
        config.setProperty(HSP.WEIGHT_HEURISTIC_SETTING, 1.2);

        // Creates an instance of the HSP planner with the specified configuration
        Planner planner = Planner.getInstance(Planner.Name.HSP, config);

        // Runs the planner and print the solution
        try {
            planner.solve();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }
}
