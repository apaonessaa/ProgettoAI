package fr.uga.pddl4j.project;

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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IMPlanner extends AbstractPlanner {
    public static void main(String[] args) {
        // The path to the benchmarks directory
        final String benchmark = "src/main/java/fr/uga/pddl4j/project/benchmark/";
        final String domainName = "domain.pddl";
        final String problemName = "problems/p01.pddl";
        final String outputName = "results/p01.pddl.txt";

        // Creates the planner
        final IMPlanner planner = new IMPlanner();
        // Sets the domain of the problem to solve
        planner.setDomain(benchmark + domainName);
        // Sets the problem to solve
        planner.setProblem(benchmark + problemName);
        // Sets the timeout of the search in seconds
        planner.setTimeout(1000);
        // Sets log level
        planner.setLogLevel(LogLevel.INFO);
        // Set heuristic weight and split size
        planner.setHeuristicWeight(1.2);
        planner.setSplitSize(2);

        // Solve and print the result
        try {
            planner.solve();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        File file = null;
        FileWriter fw = null;
        try {
            file = new File(benchmark + outputName);
            if (file.exists())
                file.delete();
            file.createNewFile();

            // Write the stats
            fw = new FileWriter(file);
            fw.write(planner.getStatistics().toString());
            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int splitSize = 2; // Max number of goals per sub-goal

    private double heuristicWeigth = 1.5;

    public void setHeuristicWeight(double heuristicWeigth) {
        if (heuristicWeigth>0) this.heuristicWeigth = heuristicWeigth;
    }

    public void setSplitSize(int splitSize) {
        if (splitSize>0) this.splitSize = splitSize;
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
        Problem[] subProblems = buildSubproblems(problem);
        Node[] subSolutions = new Node[subProblems.length];
        Plan[] subplans = new Plan[subProblems.length];

        // params
        SearchStrategy.Name strategyName = SearchStrategy.Name.ASTAR;
        StateHeuristic.Name heuristic = StateHeuristic.Name.MAX;
        int timeout = 1000000;

        // Solving with A* and IMHeuristic
        for (int i = 0; i<subProblems.length; i++){
            if (i==0) {
                // solve sub problem 0
                StateSpaceSearch alg = StateSpaceSearch.getInstance(strategyName,heuristic,heuristicWeigth);
                subSolutions[i] = alg.searchSolutionNode(subProblems[i]);
                subplans[i] = alg.extractPlan(subSolutions[i],subProblems[i]);
            }
            else {
                // solve sub problem from prev sub solution founded
                subSolutions[i-1].setParent(null);
                StateSpaceSearch alg = new IMAStar(timeout,heuristic,heuristicWeigth,subSolutions[i-1]);
                subSolutions[i] = alg.searchSolutionNode(subProblems[i]);
                subplans[i] = alg.extractPlan(subSolutions[i],subProblems[i]);
            }
            System.out.println("\n===================================================================");
            System.out.printf("Plan %d: \n",i);
            System.out.println("------------------------------------------------------------------");
            if(subplans[i] != null)
                System.out.print(problem.toString(subplans[i]));
            else
                System.out.println("Plan empty");
            System.out.println("===================================================================");
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

    @SuppressWarnings("unchecked")
    private Problem[] buildSubproblems(Problem problem) {
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
            if (stringExpr.length >= 3 && stringExpr[0].equals("(ws-at-loc")) {
                //System.out.println(stringExpr[1]); // ws
                //System.out.println(stringExpr[2].substring(0, stringExpr[2].length() - 1)); // locX)
                int index = locations.indexOf(stringExpr[2].substring(0, stringExpr[2].length() - 1));
                if (index != -1) {
                    wsMap.put(stringExpr[1], index);
                    //System.out.println(wsMap.get(stringExpr[1]).toString());
                }
            }
        }

        //System.out.println(locations);

        List<Expression<String>>[] goals = new LinkedList[locations.size()];
        List<Expression<String>> inGoal = pp.getGoal().getChildren();
        // Categorize goals by workstation
        for (Expression<String> goal : inGoal) {
            String[] parts = goal.toString().split(" ");
            //System.out.println(parts[1]); loc
            //System.out.println(parts[2]); wsX)
            String wss = parts[2].substring(0, parts[2].length() - 1);
            int index = wsMap.get(wss);
            if(goals[index]==null)
                goals[index] = new LinkedList<>();
            goals[index].add(goal);
        }

        // foreach Expression<String> in goals.
        //  if it can be splitted in split=2 parts
        // add to goals new items builded as:
        //  goals[i] -> subgoals[i:..],  subgoals[i+?:...]
        // each component has less than split elements.
        // Add this subgoals to array of goals and remove the item splitted.
        // Split goals into sub-goals

        List<List<Expression<String>>> splitGoals = new LinkedList<>();
        int splitIndex = 0;
        for (int i = 0; i < goals.length; i++) {
            if (goals[i] != null) {
                for (int j = 0; j < goals[i].size(); j += this.splitSize) {
                    List<Expression<String>> subGoals = new LinkedList<>();
                    for (int k = 0; k < this.splitSize && j + k < goals[i].size(); k++)
                        subGoals.add(goals[i].get(j + k));
                    if(splitIndex==0 && subGoals.size()>1) {
                        LinkedList<Expression<String>> tmp = new LinkedList<>();
                        tmp.add(subGoals.remove(0));
                        splitGoals.add(tmp);
                        splitIndex++;
                    }
                    splitGoals.add(subGoals);
                    splitIndex++;
                }
            }
        }

        System.out.println("===================================================================");
        System.out.println("SubProblems instantiated with:");
        System.out.println("------------------------------------------------------------------\n");
        Problem[] ret = new DefaultProblem[splitIndex];
        for (int i = 0; i < ret.length; i++) {
            Expression<String> tmp = new Expression<>();
            tmp.setChildren(splitGoals.get(i));
            System.out.println("Goal" + i + ": " + tmp);
            pp.setGoal(tmp);
            ret[i] = new DefaultProblem(pp);
            ret[i].instantiate();
        }
        System.out.println("\n===================================================================");
        return ret;
    }

    @Override
    public boolean isSupported(Problem problem) { return true; }
}
