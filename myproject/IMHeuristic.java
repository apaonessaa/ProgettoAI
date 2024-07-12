package fr.uga.pddl4j.myproject;

import fr.uga.pddl4j.heuristics.state.RelaxedGraphHeuristic;
import fr.uga.pddl4j.parser.Expression;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.*;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.util.BitVector;

import java.util.*;

public class IMHeuristic extends RelaxedGraphHeuristic {
    private Problem p;
    private final HashMap<String,List<Integer>> predicates;
    private final HashMap<Integer, List<String>> fluents;
    public IMHeuristic(Problem problem) {
        super(problem);
        this.p = problem;
        this.predicates = extractPredicates();
        this.fluents = extractFluents();
        //System.out.println(this.predicates.values());
        //System.out.println(this.fluents.values());
    }

    @Override
    public double estimate(Node node, Condition goal) {
        return this.estimate((State)node,goal);
    }

    @Override
    public int estimate(State state, Condition goal) {
        // Sets the goal state for the heuristic.
        super.setGoal(goal);
        // Expands the relaxed planning graph from the current state.
        this.expandRelaxedPlanningGraph(state);

        return
            onGoal(state, goal)
            + countUnnecessaryContent(state)
            //+ filling(state)
            - counter(state, "box-at-workstation", true);
            // TODO add constraints

    }

    /**
     * Total capacity agent's carrier - num of boxes in all carrier
     */
    private int filling(State state) {
        // for each box in carrier
        // check if it is filled then add += -> currentAmount
        // Contents box at agent
        // (box-at-carrier ?carrier - carrier ?box - box) == (filled ?box ?content)
        int currentAmount = 0;
        for (int j : this.predicates.get("filled")) {
            BitVector filledTmp = new BitVector();
            filledTmp.set(j);
            // If the state satisfies the condition filled(box, content)
            if (!state.satisfy(new Condition(filledTmp, new BitVector())))
                currentAmount++;
        }

        // for each agent or carrier, get carrier capacity
        // sum carrier capacity -> totalCapacity
        // Extract ws from init and build map {ws -> location index}
        //int totalCapacity = 8;

        // carrier capacity - box filled in the carrier = totalCapacity - currentAmount
        return - currentAmount;
    }


    /**
     * Counts the predicates in the goal that are satisfied in the current state.
     * */
    private int onGoal(State state, Condition goal) {
        BitVector positiveFluents = goal.getPositiveFluents();
        int count = 0;
        for(int i = positiveFluents.nextSetBit(0); i>=0; i = positiveFluents.nextSetBit(i+1)){
            BitVector tmp = new BitVector();
            tmp.set(i);
            if(state.satisfy(new Condition(tmp,new BitVector())))count++;
        }
        BitVector negativeFluents = goal.getNegativeFluents();
        for(int i = negativeFluents.nextSetBit(0); i>=0; i = negativeFluents.nextSetBit(i+1)){
            BitVector tmp = new BitVector();
            tmp.set(i);
            if(state.satisfy(new Condition(tmp,new BitVector())))count++;
        }
        return goal.cardinality() - count;
    }

    /**
     * Counts the predicates that are satisfied(or not satisfied) in the current state.
     * */
    private int counter(State state, String predName, boolean toSatisfied) {
        int count = 0;
        for (int i : this.predicates.get(predName)) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            Condition condition = new Condition(tmp, new BitVector());
            if (toSatisfied)
                if (state.satisfy(condition)) count++;
            else
                if (!state.satisfy(condition)) count++;
        }
        return count;
    }

    /**
     *  Counter of content that is not necessary for current loc
     *  Goal : content-at-ws
     *
     *  Current state : for each box-at-workstation satisfied predicates
     *                      contents := (filled pred satisfied for box)
     *                      for each content in contents verify
     *                          +1 goal NOT is-type content same content-at-workstation content.
     *
     * */
    private int countUnnecessaryContent(State state) {
        int count = 0;
        // Map box -> [content]
        Map<String, List<String>> filled = new HashMap<>();
        // Contents of the box
        // (filled ?box - box ?content - content)
        List<Integer> filledPreds = this.predicates.get("filled");
        // Content Type
        // (is-type ?content - content ?t - content-type)
        List<Integer> isTypeContentsPreds = this.predicates.get("is-type");
        for (int j : filledPreds) {
            BitVector filledTmp = new BitVector();
            filledTmp.set(j);
            // If the state satisfies the condition filled(box, content)
            if (state.satisfy(new Condition(filledTmp, new BitVector()))) {
                // Extract the content from the predicate
                // (filled ?box - box ?content - content)
                String box = this.fluents.get(j).get(1);
                String content = this.fluents.get(j).get(2);

                // Ensure the box is in the map
                filled.computeIfAbsent(box, k -> new ArrayList<>());
                filled.get(box).add(content);
            }
        }

        // Iterate through predicates related to box-at-workstation
        List<Integer> boxAtWsPreds = this.predicates.get("box-at-workstation");
        for (int i : boxAtWsPreds) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            // If the state satisfies the condition box-at-workstation
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                // Extract the box and workstation indices from the predicate
                // (box-at-workstation ?ws - workstation ?box - box)
                String ws = this.fluents.get(i).get(1);
                String box = this.fluents.get(i).get(2);
                // Get content type of the box
                List<String> contentInTheBox = filled.get(box);
                if(contentInTheBox==null)
                    continue;
                // Check if this content is necessary at this workstation
                // (content-at-workstation ?ws - workstation ?t - content-type)
                List<Integer> keys = this.predicates.get("content-at-workstation");
                boolean isNecessary = false;
                for (int k : keys) {
                    // (content-at-workstation ?ws - workstation ?t - content-type)
                    String goalWs = this.fluents.get(k).get(1);
                    String goalType = this.fluents.get(k).get(2);
                    if (!ws.equals(goalWs))
                        continue;
                    // Ensure the content is of the type required at the workstation
                    for (String theContent : contentInTheBox) {
                        for (int l : isTypeContentsPreds) {
                            String typeContent = this.fluents.get(l).get(1);
                            String type = this.fluents.get(l).get(2);
                            if (typeContent.equals(theContent) && type.equals(goalType)) {
                                isNecessary = true;
                                break;
                            }
                        }
                        if (isNecessary)
                            break;
                    }
                    if (isNecessary)
                        break;
                }
                // if exists a content-type not necessary, increment the counter
                if (!isNecessary)
                    count++;
            }
        }
        return count;
    }

    /**
     *   (:predicates
     *     (at-box ?box - box ?loc - location)
     *     (at-agent ?agent - agent ?loc - location)
     *     (at-ws ?ws - workstation ?loc - location)
     *     (at-content ?content - content ?loc - location)
     *     (connected ?loc1 ?loc2 - location)
     *     (empty ?box - box)
     *     (filled ?box - box ?content - content)
     *     (box-at-workstation ?ws - workstation ?box - box)
     *     (content-at-workstation ?ws - workstation ?t - content-type)
     *     (box-at-carrier ?carrier - carrier ?box - box)
     *     (carrier-at-agent ?agent - agent ?carrier - carrier)
     *     (is-type ?content - content ?t - content-type)
     *   )
     *
     *   predicates(at-box) -> 0
     *   predicates(at-ws) -> 2
     *
     *   fluents(0)[at-box] -> ["at-box", "?box", "?loc"]
     *      fluents(0).get(1) -> "?box"
     * */


    /**
     *  Extract and map predicate names to their indices in the fluents list.
     * */
    private HashMap<String, List<Integer>> extractPredicates(){
        HashMap<String,List<Integer>> ret = new HashMap<>();
        int index = 0;
        for(Fluent f: p.getFluents()) {
            String key = new StringTokenizer(p.toString(f),"( )",false).nextToken();
            if(ret.get(key) == null)
                ret.put(key, new ArrayList<>());
            ret.get(key).add(index);
            index++;
        }
        return ret;
    }

    /**
     *  Extract and map fluents indices to their respective components.
     * */
    private HashMap<Integer, List<String>> extractFluents(){
        HashMap<Integer,List<String>> ret = new HashMap<>();
        int index = 0;
        for(Fluent f: p.getFluents()) {
            StringTokenizer st = new StringTokenizer(p.toString(f),"( )",false);
            if(ret.get(index)==null)
                ret.put(index,new ArrayList<>());
            while(st.hasMoreTokens())
                ret.get(index).add(st.nextToken());
            index++;
        }
        return ret;
    }
}
