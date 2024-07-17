package fr.uga.pddl4j.project;

import fr.uga.pddl4j.heuristics.state.StateHeuristic;
import fr.uga.pddl4j.planners.statespace.search.AbstractStateSpaceSearch;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.planners.statespace.search.NodeComparator;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;
import org.openjdk.jol.info.GraphLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public class IMAStar extends AbstractStateSpaceSearch {
    private Node initial;

    public IMAStar() {
        super();
    }

    public IMAStar(int timeout, StateHeuristic.Name heuristic, double weight, Node initial) {
        super(timeout, heuristic, weight);
        this.initial = initial;
    }

    public Node search(final Problem codedProblem) {
        Objects.requireNonNull(codedProblem);
        final long begin = System.currentTimeMillis();
        final StateHeuristic heuristic = StateHeuristic.getInstance(this.getHeuristic(), codedProblem);
        // Get the initial state from the planning problem
        initial.setParent(null);
        final State init = initial;
        // Initialize the closed list of nodes (store the nodes explored)
        final Map<State, Node> closeSet = new HashMap<>();
        final Map<State, Node> openSet = new HashMap<>();
        // Initialize the opened list (store the pending node)
        final double currWeight = getWeight();
        // The list stores the node ordered according to the A* (getFValue = g + h) function
        final PriorityQueue<Node> open = new PriorityQueue<>(1000, new NodeComparator(currWeight));
        // Creates the root node of the tree search
        final Node root = new Node(init, null, -1, 0, heuristic.estimate(init, codedProblem.getGoal()));
        // Adds the root to the list of pending nodes
        open.add(root);
        openSet.put(init, root);

        this.resetNodesStatistics();
        Node solution = null;
        final long timeout = this.getTimeout() * 1000;
        long time = 0;
        // Start of the search
        while (!open.isEmpty() && solution == null && time < timeout) {
            // Pop the first node in the pending list open
            final Node current = open.poll();
            openSet.remove(current);
            closeSet.put(current, current);
            // If the goal is satisfy in the current node then extract the search and return it
            if (current.satisfy(codedProblem.getGoal())) {
                solution = current;
            } else {
                // Try to apply the operators of the problem to this node
                int index = 0;
                for (Action op : codedProblem.getActions()) {

                    // Test if a specified operator is applicable in the current state
                    if (op.isApplicable(current)) {
                        //System.out.println("IS APPLICABLE");
                        Node state = new Node(current);
                        this.setCreatedNodes(this.getCreatedNodes() + 1);

                        // Apply the effect of the applicable operator
                        // Test if the condition of the effect is satisfied in the current state
                        // Apply the effect to the successor node
                        op.getConditionalEffects().stream().filter(ce -> current.satisfy(ce.getCondition()))
                            .forEach(ce -> state.apply(ce.getEffect()));
                        final double g = current.getCost() + op.getCost().getValue();
                        Node result = openSet.get(state);
                        if (result == null) {
                            result = closeSet.get(state);
                            if (result != null) {
                                if (g < result.getCost()) {
                                    result.setCost(g);
                                    result.setParent(current);
                                    result.setAction(index);
                                    result.setDepth(current.getDepth() + 1);
                                    open.add(result);
                                    openSet.put(result, result);
                                    closeSet.remove(result);
                                }
                            } else {
                                state.setCost(g);
                                state.setParent(current);
                                state.setAction(index);
                                state.setHeuristic(heuristic.estimate(state, codedProblem.getGoal()));
                                state.setDepth(current.getDepth() + 1);
                                open.add(state);
                                openSet.put(state, state);
                            }
                        } else if (g < result.getCost()) {
                            result.setCost(g);
                            result.setParent(current);
                            result.setAction(index);
                            result.setDepth(current.getDepth() + 1);
                        }
                    }
                    index++;
                }
            }
            // Compute the searching time
            time = System.currentTimeMillis() - begin;
        }

        this.setExploredNodes(closeSet.size());
        this.setPendingNodes(openSet.size());
        this.setMemoryUsed(GraphLayout.parseInstance(closeSet).totalSize()
            + GraphLayout.parseInstance(openSet).totalSize());
        this.setSearchingTime(time);

        // return the search computed or null if no search was found
        return solution;
    }
}
