package fr.uga.pddl4j.myproject;

import fr.uga.pddl4j.heuristics.state.RelaxedGraphHeuristic;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.*;
import fr.uga.pddl4j.problem.numeric.NumericFluent;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.util.BitVector;

import java.util.*;

public class IMHeuristic extends RelaxedGraphHeuristic {
    private final Problem p;
    private final HashMap<String,List<Integer>> predicates;
    private final HashMap<Integer, List<String>> fluents;
    private HashMap<String, List<String>> wsContentTypeRequested;
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

        // <workstation, types[]> to satisfied in current problem.
        this.wsContentTypeRequested= new HashMap<>();
        for(int i = goal.getPositiveFluents().nextSetBit(0); i>=0; i = goal.getPositiveFluents().nextSetBit(i+1)){
            BitVector tmp = new BitVector();
            tmp.set(i);
            if(state.satisfy(new Condition(tmp,new BitVector()))){
                // (content-at-workstation ?ws - workstation ?t - content-type)
                String ws = this.fluents.get(i).get(1);
                String type = this.fluents.get(i).get(2);
                wsContentTypeRequested.computeIfAbsent(ws, k -> new ArrayList<>());
                wsContentTypeRequested.get(ws).add(type);
            }
        }
        return
            onGoal(state, goal)
            + countUnnecessaryContent(state)                                // scope: box, avoid configuration where there are some unnecessary content for a loc/ws
            - countContentInWs(state)                                       // scope: goal, more sophisticated than "counter(state, "content-at-workstation")"
            - counter(state, "box-at-workstation")                // scope: deliver
            + countContentAt(state, "central_warehouse");      // scope: minimize number of move, fill before a move if it is possible
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
    private int counter(State state, String predName) {
        int count = 0;
        for (int i : this.predicates.get(predName)) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            Condition condition = new Condition(tmp, new BitVector());
            if (state.satisfy(condition))
                count++;
        }
        return count;
    }

    /**
     * Counts the content at ws, the satisfied the constraints
     * */
    private int countContentInWs(State state) {
        int count = 0;
        // (content-at-workstation ?ws - workstation ?t - content-type)
        for (int i : this.predicates.get("content-at-workstation")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            Condition condition = new Condition(tmp, new BitVector());
            if (state.satisfy(condition)) {
                // Extract the content-type and ws
                // (content-at-workstation ?ws - workstation ?t - content-type)
                String ws = this.fluents.get(i).get(1);
                String type = this.fluents.get(i).get(2);
                // Check if it is a target workstation
                if (!wsContentTypeRequested.containsKey(ws))
                    continue;
                if(wsContentTypeRequested.get(ws).contains(type))
                    count++;
            }
            count++;
        }
        return count;
    }

    /**
     *  Counter of content that is not necessary for current loc/ws
     *  Goal : content-at-ws
     *
     *  Current state : for each box-at-workstation satisfied predicates
     *                      contents := (filled pred satisfied for box)
     *                      for each content in contents verify
     *                          +1 goal NOT is-type content same content-at-workstation content.
     * */
    private int countUnnecessaryContent(State state) {
        int count = 0;

        // Step 1: Populate the filled map with boxes and their contents
        // Map box -> [content]
        Map<String, List<String>> filled = new HashMap<>();

        // Contents of the box
        // (filled ?box - box ?content - content)
        for (int j : this.predicates.get("filled")) {
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

        // Step 2: Check the content at each workstation
        // Content Type
        // (is-type ?content - content ?t - content-type)
        List<Integer> isTypeContentsPreds = this.predicates.get("is-type");
        for (int i : this.predicates.get("box-at-workstation")) {
            // (box-at-workstation ?ws - workstation ?box - box)
            BitVector tmp = new BitVector();
            tmp.set(i);
            // If the state satisfies the condition box-at-workstation
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                // Extract the box and workstation indices from the predicate
                // (box-at-workstation ?ws - workstation ?box - box)
                String ws = this.fluents.get(i).get(1);
                String box = this.fluents.get(i).get(2);

                // Check if it is a target workstation
                if (!wsContentTypeRequested.containsKey(ws))
                    continue;

                // Get content type of the box
                List<String> contentInTheBox = filled.get(box);
                if (contentInTheBox == null)
                    continue;

                // Check if this content is necessary at this workstation
                // (content-at-workstation ?ws - workstation ?t - content-type)
                // current goal
                boolean isNecessary = false;
                for (String theContent : contentInTheBox) {
                    for (int l : isTypeContentsPreds) {
                        String typedContent = this.fluents.get(l).get(1);
                        String type = this.fluents.get(l).get(2);
                        if (typedContent.equals(theContent) && wsContentTypeRequested.get(ws).contains(type)) {
                            isNecessary = true;
                            break;
                        }
                    }
                    if (isNecessary)
                        break;
                }
                // If there exists a content-type not necessary, increment the counter
                if (!isNecessary)
                    count++;
            }
        }
        return count;
    }

    /**
     * Counts the content located at :targetLocation
     * */
    private int countContentAt(State state, String targetLocation) {
        int count=0;
        List<Integer> contentAtCentralWarehouse = this.predicates.get("at-content");
        // content at loc (at-content ?content - content ?loc - location)
        for (int j : contentAtCentralWarehouse) {
            BitVector tmp = new BitVector();
            tmp.set(j);
            // If the state satisfies the condition at-content(content, loc)
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                // Extract the location from the predicate
                // (at-content ?content - content ?loc - location)
                //String content = this.fluents.get(j).get(1);
                String loc = this.fluents.get(j).get(2);

                // Ensure the loc is targetLocation
                if (loc.equals(targetLocation))
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
        // Numeric fluents
        for(NumericFluent f: ((FinalizedProblem)p).getNumericFluents()) {
            String key = new StringTokenizer(((FinalizedProblem)p).toString(f),"( )",false).nextToken();
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
        // Numeric fluents
        for(NumericFluent f: ((FinalizedProblem)p).getNumericFluents()) {
            StringTokenizer st = new StringTokenizer(((FinalizedProblem)p).toString(f),"( )",false);
            if(ret.get(index)==null)
                ret.put(index,new ArrayList<>());
            while(st.hasMoreTokens())
                ret.get(index).add(st.nextToken());
            index++;
        }
        return ret;
    }
}
