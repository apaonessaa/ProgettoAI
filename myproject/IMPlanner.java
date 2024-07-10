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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class IMPlanner extends AbstractPlanner {
    private double heuristic_weigth = 1.0;
    private int split = 2;

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
        //array di utilità
        Problem[] subProblems = split(problem,split);
        Node[] subSolutions = new Node[subProblems.length];
        Plan[] subplans = new Plan[subProblems.length];

        //parametri algoritmi
        SearchStrategy.Name strategyName = SearchStrategy.Name.ASTAR;
        StateHeuristic.Name heuristic = StateHeuristic.Name.IM_HEURISTIC;
        int timeout = 1000000;

        //inizialmente usiamo A*
        StateSpaceSearch alg0 = StateSpaceSearch.getInstance(strategyName, heuristic, heuristic_weigth);
        subSolutions[0] = alg0.searchSolutionNode(subProblems[0]);
        subplans[0] = alg0.extractPlan(subSolutions[0],subProblems[0]);
        System.out.println("piano 0: ");
        if(subplans[0] != null)
            printPlan(subplans[0],problem);
        else System.out.println("piano nullo");
        System.out.println("fine piano");
        //risolviamo i sottoproblemi rimanenti usando ModifiedAStar
        for(int i = 1; i<subProblems.length; i++){
            subSolutions[i-1].setParent(null);
            StateSpaceSearch alg = new IMAStar(timeout,heuristic,heuristic_weigth,subSolutions[i-1]);
            subSolutions[i] = alg.searchSolutionNode(subProblems[i]);
            subplans[i] = alg.extractPlan(subSolutions[i],subProblems[i]);
            System.out.printf("piano %d: \n",i);
            if(subplans[i] != null)
                printPlan(subplans[i],problem);
            else System.out.println("piano nullo");
            System.out.println("fine piano");
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

    private void printPlan(Plan p, Problem problem){
        System.out.println(
            new StringBuilder().append(problem.toString(p)).toString()
        );
    }

    @SuppressWarnings("unchecked")
    private Problem[] split(Problem problem, int split){
        List<Expression<String>>[] goals = new LinkedList[split];
        DefaultParsedProblem pp = problem.getParsedProblem();
        System.out.println(pp.getConstants());
        List<Expression<String>> inGoal = pp.getGoal().getChildren();
        int bias = inGoal.size()/split;
        int j = 0;
        Iterator<Expression<String>> it = inGoal.iterator();
        for(int i = 0; i<goals.length; i++){
            goals[i] = new LinkedList<>();
            int k = 0;
            for(;k<bias; k++) {
                goals[i].add(it.next());
                it.remove();
            }
            j+=k;
        }
        while(inGoal.size()>0)
            for(int k = 0; k<goals.length; k++) {
                goals[k].add(inGoal.remove(0));
                if(inGoal.isEmpty())break;
            }
        Problem[] ret = new DefaultProblem[split];
        for(int i = 0; i<ret.length; i++){
            Expression<String> tmp = new Expression<>();
            tmp.setChildren(goals[i]);
            System.out.println("Goal"+i+": "+tmp);
            pp.setGoal(tmp);
            ret[i] = new DefaultProblem(pp);
            ret[i].instantiate();
        }
        return ret;
    }

    @Override
    public boolean isSupported(Problem problem) {
        return false;
    }
}
