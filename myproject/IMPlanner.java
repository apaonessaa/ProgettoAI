package fr.uga.pddl4j.myproject;

import fr.uga.pddl4j.heuristics.state.StateHeuristic;
import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.Expression;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.plan.SequentialPlan;
import fr.uga.pddl4j.planners.*;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.planners.statespace.search.StateSpaceSearch;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;

import java.util.*;

public class IMPlanner extends AbstractPlanner {
    private double heuristic_weigth = 1.5;

    public static void main(String[] args) {
        // The path to the benchmarks directory
        final String benchmark = "src/main/java/fr/uga/pddl4j/myproject/benchmark/";
        // Creates the planner
        final IMPlanner planner = new IMPlanner();
        // Sets the domain of the problem to solve
        planner.setDomain(benchmark + "domain2.pddl");
        // Sets the problem to solve
        planner.setProblem(benchmark + "/problems/instance2.pddl");
        // Sets the timeout of the search in seconds
        planner.setTimeout(1000);
        // Sets log level
        planner.setLogLevel(LogLevel.INFO);

        // Solve and print the result
        try {
            planner.solve();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Problem instantiate(DefaultParsedProblem problem) {
        DefaultProblem p = new DefaultProblem(problem);
        p.instantiate();
        return p;
    }

    @Override
    public Plan solve(Problem problem) throws ProblemNotSupportedException {
        // utility structs
        Problem[] subProblems = split(problem);
        // reduce the size of subproblems, because there are few items will null value.

        Node[] subSolutions = new Node[subProblems.length];
        Plan[] subplans = new Plan[subProblems.length];

        // params
        SearchStrategy.Name strategyName = SearchStrategy.Name.ASTAR;
        StateHeuristic.Name heuristic = StateHeuristic.Name.IM_HEURISTIC;
        int timeout = 1000000;

        // A*
        StateSpaceSearch alg0 = StateSpaceSearch.getInstance(strategyName, heuristic, heuristic_weigth);
        subSolutions[0] = alg0.searchSolutionNode(subProblems[0]);
        subplans[0] = alg0.extractPlan(subSolutions[0],subProblems[0]);
        System.out.println("\nPlan 0:");
        if(subplans[0] != null)
            printPlan(subplans[0],problem);
        else System.out.println("Plan empty");
        System.out.println("End plan");
        // subproblems solving
        for(int i = 1; i<subProblems.length; i++){
            //subSolutions[i-1].setParent(null);
            StateSpaceSearch alg = new IMAStar(timeout,heuristic,heuristic_weigth,subSolutions[i-1]);
            subSolutions[i] = alg.searchSolutionNode(subProblems[i]);
            subplans[i] = alg.extractPlan(subSolutions[i],subProblems[i]);
            System.out.printf("\nPlan %d: \n",i);
            if(subplans[i] != null)
                printPlan(subplans[i],problem);
            else System.out.println("Plan empty");
            System.out.println("End plan");
        }
        List<Action> sol = new LinkedList<>();
        for(Plan p: subplans)
            if(p != null)
                sol.addAll(p.actions());
        Plan solution = new SequentialPlan();
        Iterator<Action> it = sol.iterator();
        for(int i = 0; i<sol.size(); i++)
            solution.add(i,it.next());
        return solution;
    }

    private void printPlan(Plan p, Problem problem) {
        System.out.print(problem.toString(p));
    }

    @SuppressWarnings("unchecked")
    private  Problem[] split(Problem problem) {
        DefaultParsedProblem pp = problem.getParsedProblem();

        // Extract locations from objects
        List<String> locations = pp.getObjects().stream()
            .filter(stringTypedSymbol -> stringTypedSymbol.getValue().startsWith("loc"))
            .map(obj -> obj.getImage().toString())
            .toList();
        //System.out.println("Locations: " + locations);

        // Extract ws from init and build map {ws -> location index}
        Map<String, Integer> wsMap = new HashMap<>();
        for (Expression<String> stringExpression : pp.getInit()) {
            String[] stringExpr = stringExpression.toString().split(" ");
            if (stringExpr.length >= 3 && stringExpr[0].equals("(at-ws")) {
                //System.out.println(stringExpr[1]);
                //System.out.println(stringExpr[2].substring(0, stringExpr[2].length() - 1));
                int index = locations.indexOf(stringExpr[2].substring(0, stringExpr[2].length() - 1));
                if (index != -1) {
                    wsMap.put(stringExpr[1], index);
                    //System.out.println(ws[index].toString());
                }
            }
        }

        System.out.println(locations);

        List<Expression<String>>[] goals = new LinkedList[locations.size()];
        List<Expression<String>> inGoal = pp.getGoal().getChildren();
        // Categorize goals by workstation
        for (Expression<String> goal : inGoal) {
            String[] parts = goal.toString().split(" ");
            String wss = parts[1];
            int index = wsMap.get(wss);
            if(goals[index]==null)
                goals[index] = new LinkedList<>();
            goals[index].add(goal);
        }

        // foreach Expression<String> in goals.
        //  if it can be splitted in split=3 parts
        // add to goals new items builded as:
        //  goals[i] -> subgoals[i],  subgoals[i+1], subgoals[i+2]
        // each component has less than split elements.
        // Add this subgoals to array of goals and remove the item splitted.
        // Split goals into sub-goals

        int splitSize = 2; // Max number of goals per sub-goal
        List<Expression<String>>[] splitGoals = new LinkedList[locations.size() * splitSize + 1];
        int splitIndex = 0;

        for (int i = 0; i < goals.length; i++) {
            if (goals[i] != null) {
                for (int j = 0; j < goals[i].size(); j += splitSize) {
                    List<Expression<String>> subGoals = new LinkedList<>();
                    for (int k = 0; k < splitSize && j + k < goals[i].size(); k++)
                        subGoals.add(goals[i].get(j + k));
//                    if(splitIndex==0 && subGoals.size()>1) {
//                        splitGoals[splitIndex] = new LinkedList<>();
//                        splitGoals[splitIndex++].add(subGoals.remove(0));
//                    }
                    splitGoals[splitIndex++] = subGoals;
                }
            }
        }

        Problem[] ret = new DefaultProblem[splitIndex];
        for (int i = 0; i < ret.length; i++) {
            Expression<String> tmp = new Expression<>();
            if (splitGoals[i] == null) continue;
            tmp.setChildren(splitGoals[i]);
            System.out.println("Goal" + i + ": " + tmp);
            pp.setGoal(tmp);
            ret[i] = new DefaultProblem(pp);
            ret[i].instantiate();
        }
        return ret;
    }

    @Override
    public boolean isSupported(Problem problem) { return true; }
}
