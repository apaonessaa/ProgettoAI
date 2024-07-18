package fr.uga.pddl4j.project;

import fr.uga.pddl4j.heuristics.state.RelaxedGraphHeuristic;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.Fluent;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.util.BitVector;

import java.util.*;

public class IMHeuristic extends RelaxedGraphHeuristic {
    private final Problem p;
    private final HashMap<String, List<Integer>> predicates;
    private final HashMap<Integer, List<String>> fluents;
    private HashMap<String, List<String>> wsContentTypeRequested;
    private HashMap<String, String> filled;
    private List<String> goalTypeRequested;

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
        return this.estimate((State)node, goal);
    }

    @Override
    public int estimate(State state, Condition goal) {
        super.setGoal(goal);
        this.expandRelaxedPlanningGraph(state);

        this.wsContentTypeRequested = new HashMap<>();
        this.goalTypeRequested = new LinkedList<>();
        //System.out.println(this.fluents.get(goal.getPositiveFluents().nextSetBit(0)).get(1));
        for (int i = goal.getPositiveFluents().nextSetBit(0); i >= 0; i = goal.getPositiveFluents().nextSetBit(i + 1)) {
            //BitVector tmp = new BitVector();
            //tmp.set(i);
            String type = this.fluents.get(i).get(1);
            String ws = this.fluents.get(i).get(2);
            //System.out.println(type+","+ws);
            wsContentTypeRequested.computeIfAbsent(ws, k -> new ArrayList<>());
            wsContentTypeRequested.get(ws).add(type);
            goalTypeRequested.add(type);
        }

        this.filled = new HashMap<>();
        for (int j : this.predicates.get("filled-box")) {
            BitVector filledTmp = new BitVector();
            filledTmp.set(j);
            if (state.satisfy(new Condition(filledTmp, new BitVector()))) {
                String box = this.fluents.get(j).get(1);
                String content = this.fluents.get(j).get(2);
                filled.put(box, content);
            }
        }
        //System.out.println(wsContentTypeRequested.keySet());
        //System.out.println(goalTypeRequested);
        //System.out.println(filled);
        int estimated =
            !super.isGoalReachable() || existUnnecessaryContentAtWs(state) ? Integer.MAX_VALUE :
                onGoal(state, goal)
                + countContentLocatedAtCW(state)
                + counter(state, "empty-box")
                - counter(state, "content-type-at-ws")
                - filledBoxAtAgentLocatedAtCW(state);
        //System.out.println(estimated);
        return estimated;
    }

    /**
     * Counts the predicates that are satisfied in the current state.
     * */
    private int counter(State state, String predName) {
        int count = 0;
        for (int i : this.predicates.get(predName)) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) count++;
        }
        return count;
    }

    private int onGoal(State state, Condition goal) {
        BitVector positiveFluents = goal.getPositiveFluents();
        int count = 0;
        for (int i = positiveFluents.nextSetBit(0); i >= 0; i = positiveFluents.nextSetBit(i + 1)) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) count++;
        }
        BitVector negativeFluents = goal.getNegativeFluents();
        for (int i = negativeFluents.nextSetBit(0); i >= 0; i = negativeFluents.nextSetBit(i + 1)) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) count++;
        }
        return goal.cardinality() - count;
    }

    private int filledBoxAtAgentLocatedAtCW(State state) {
        List<String> agents = new LinkedList<>();
        for (int i : this.predicates.get("agent-at-loc")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                String agent = this.fluents.get(i).get(1);
                String loc = this.fluents.get(i).get(2);
                if (loc.equals("central_warehouse"))
                    agents.add(agent);
            }
        }
        List<String> carriers = new LinkedList<>();
        for (int i : this.predicates.get("carrier-at-agent")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                String carrier = this.fluents.get(i).get(1);
                String agent = this.fluents.get(i).get(2);
                if (agents.contains(agent))
                    carriers.add(carrier);
            }
        }
        //int fullCapacity=0;
        List<String> places = new LinkedList<>();
        for (int i : this.predicates.get("place-at-carrier")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                String place = this.fluents.get(i).get(1);
                String carrier = this.fluents.get(i).get(2);
                if (carriers.contains(carrier)) {
                    //fullCapacity++;
                    places.add(place);
                }
            }
        }
        //System.out.println("Agent at central-warehouse full capacity: "+fullCapacity);
        int amount=0;
        List<Integer> isTypeContentsPreds = this.predicates.get("is-type");
        for (int i : this.predicates.get("box-at-place")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                String box = this.fluents.get(i).get(1);
                String place = this.fluents.get(i).get(2);
                if (!filled.containsKey(box) && places.contains(place))
                    continue;
                String content = filled.get(box);
                //System.out.println(box+","+content);
                for (int j : isTypeContentsPreds) {
                    BitVector tmp2 = new BitVector();
                    tmp2.set(j);
                    if (state.satisfy(new Condition(tmp2, new BitVector()))) {
                        String typedContent = this.fluents.get(j).get(1);
                        String type = this.fluents.get(j).get(2);
                        //System.out.println(box+","+typedContent+","+content+","+type);
                        if (typedContent.equals(content) && goalTypeRequested.remove(type)) {
                            amount++;
                            //System.out.println(amount);
                            break;
                        }
                    }
                }
                if (goalTypeRequested.isEmpty())
                    break;
            }
        }
        //System.out.println("Number of box at central-warehouse on agent : "+amount);
        return amount;
    }

    /**
     * Counts the content at ws, the satisfied the constraints
     * */
    private int countContentLocatedAtCW(State state) {
        int count = 0;
        for (int i : this.predicates.get("content-type-at-ws")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (!state.satisfy(new Condition(tmp, new BitVector()))) {
                String type = this.fluents.get(i).get(1);
                String ws = this.fluents.get(i).get(2);
                if (!wsContentTypeRequested.containsKey(ws))
                    continue;
                if (wsContentTypeRequested.get(ws).contains(type))
                    count++;
            }
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
    private boolean existUnnecessaryContentAtWs(State state) {
        // Step 2: Check the content at each workstation
        // Content Type
        // (is-type ?content - content ?t - content-type)
        List<Integer> isTypeContentsPreds = this.predicates.get("is-type");
        for (int i : this.predicates.get("box-at-ws")) {
            // (box-at-ws ?box - box ?ws - workstation)
            BitVector tmp = new BitVector();
            tmp.set(i);
            // If the state satisfies the condition box-at-workstation
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                // Extract the box and workstation indices from the predicate
                // (box-at-ws ?box - box ?ws - workstation)
                String box = this.fluents.get(i).get(1);
                String ws = this.fluents.get(i).get(2);

                // Check if it is a target workstation
                if (!wsContentTypeRequested.containsKey(ws))
                    continue;

                // Get content type of the box
                String contentInTheBox = this.filled.get(box);
                if (contentInTheBox == null)
                    continue;

                // Check if this content is necessary at this workstation
                // (content-type-at-ws ?t - content-type ?ws - workstation)
                // current goal
                for (int j : isTypeContentsPreds) {
                    // (box-at-place ?box - box ?place - place)
                    BitVector tmp2 = new BitVector();
                    tmp2.set(j);
                    // If the state satisfies the condition is-type(content, type)
                    if (state.satisfy(new Condition(tmp2, new BitVector()))) {
                        String typedContent = this.fluents.get(j).get(1);
                        String type = this.fluents.get(j).get(2);
                        if (typedContent.equals(contentInTheBox) && !wsContentTypeRequested.get(ws).remove(type)) {
                            // exist a content un necessary for this ws
                            return true;
                        }
                    }
                }
            }
        }
        //System.out.println("Number of unnecessary content :"+count);
        return false;
    }

    private HashMap<String, List<Integer>> extractPredicates() {
        HashMap<String, List<Integer>> ret = new HashMap<>();
        int index = 0;
        for (Fluent f : p.getFluents()) {
            String key = new StringTokenizer(p.toString(f), "( )", false).nextToken();
            if (ret.get(key) == null)
                ret.put(key, new ArrayList<>());
            ret.get(key).add(index);
            index++;
        }
        return ret;
    }

    private HashMap<Integer, List<String>> extractFluents() {
        HashMap<Integer, List<String>> ret = new HashMap<>();
        int index = 0;
        for (Fluent f : p.getFluents()) {
            StringTokenizer st = new StringTokenizer(p.toString(f), "( )", false);
            if (ret.get(index) == null)
                ret.put(index, new ArrayList<>());
            while (st.hasMoreTokens())
                ret.get(index).add(st.nextToken());
            index++;
        }
        return ret;
    }
}
