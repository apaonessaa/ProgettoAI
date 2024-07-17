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
    private final Map<String, List<Integer>> predicates;
    private final Map<Integer, List<String>> fluents;
    private Map<String, Set<String>> wsContentTypeRequested;
    private Map<String, String> filled;
    private Set<String> goalTypeRequested;

    public IMHeuristic(Problem problem) {
        super(problem);
        this.p = problem;
        this.predicates = extractPredicates();
        this.fluents = extractFluents();
    }

    @Override
    public double estimate(Node node, Condition goal) {
        return this.estimate((State) node, goal);
    }

    @Override
    public int estimate(State state, Condition goal) {
        super.setGoal(goal);
        this.expandRelaxedPlanningGraph(state);

        this.wsContentTypeRequested = new HashMap<>();
        this.goalTypeRequested = new HashSet<>();

        for (int i = goal.getPositiveFluents().nextSetBit(0); i >= 0; i = goal.getPositiveFluents().nextSetBit(i + 1)) {
            String type = this.fluents.get(i).get(1);
            String ws = this.fluents.get(i).get(2);
            wsContentTypeRequested.computeIfAbsent(ws, k -> new HashSet<>()).add(type);
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

        int estimated = !super.isGoalReachable() || existUnnecessaryContentAtWs(state)
            ? Integer.MAX_VALUE
            : onGoal(state, goal) - countContentInWs(state) - filledBoxAtAgentLocatedAt(state);

        return estimated;
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

    private int filledBoxAtAgentLocatedAt(State state) {
        Set<String> agents = new HashSet<>();
        for (int i : this.predicates.get("agent-at-loc")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                String agent = this.fluents.get(i).get(1);
                String loc = this.fluents.get(i).get(2);
                if (loc.equals("central_warehouse")) {
                    agents.add(agent);
                }
            }
        }

        Set<String> carriers = new HashSet<>();
        for (int i : this.predicates.get("carrier-at-agent")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                String carrier = this.fluents.get(i).get(1);
                String agent = this.fluents.get(i).get(2);
                if (agents.contains(agent)) {
                    carriers.add(carrier);
                }
            }
        }

        Set<String> places = new HashSet<>();
        for (int i : this.predicates.get("place-at-carrier")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                String place = this.fluents.get(i).get(1);
                String carrier = this.fluents.get(i).get(2);
                if (carriers.contains(carrier)) {
                    places.add(place);
                }
            }
        }

        int amount = 0;
        List<Integer> isTypeContentsPreds = this.predicates.get("is-type");
        for (int i : this.predicates.get("box-at-place")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                String box = this.fluents.get(i).get(1);
                String place = this.fluents.get(i).get(2);
                if (!filled.containsKey(box) && places.contains(place)) continue;
                String content = filled.get(box);
                for (int j : isTypeContentsPreds) {
                    BitVector tmp2 = new BitVector();
                    tmp2.set(j);
                    if (state.satisfy(new Condition(tmp2, new BitVector()))) {
                        String typedContent = this.fluents.get(j).get(1);
                        String type = this.fluents.get(j).get(2);
                        if (typedContent.equals(content) && goalTypeRequested.remove(type)) {
                            amount++;
                            break;
                        }
                    }
                }
                if (goalTypeRequested.isEmpty()) break;
            }
        }
        return amount;
    }

    private int countContentInWs(State state) {
        int count = 0;
        for (int i : this.predicates.get("content-type-at-ws")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (!state.satisfy(new Condition(tmp, new BitVector()))) {
                String type = this.fluents.get(i).get(1);
                String ws = this.fluents.get(i).get(2);
                if (!wsContentTypeRequested.containsKey(ws)) continue;
                if (wsContentTypeRequested.get(ws).contains(type)) count++;
            }
        }
        return count;
    }

    private boolean existUnnecessaryContentAtWs(State state) {
        List<Integer> isTypeContentsPreds = this.predicates.get("is-type");
        for (int i : this.predicates.get("box-at-ws")) {
            BitVector tmp = new BitVector();
            tmp.set(i);
            if (state.satisfy(new Condition(tmp, new BitVector()))) {
                String box = this.fluents.get(i).get(1);
                String ws = this.fluents.get(i).get(2);
                if (!wsContentTypeRequested.containsKey(ws)) continue;
                String contentInTheBox = this.filled.get(box);
                if (contentInTheBox == null) continue;
                for (int j : isTypeContentsPreds) {
                    BitVector tmp2 = new BitVector();
                    tmp2.set(j);
                    if (state.satisfy(new Condition(tmp2, new BitVector()))) {
                        String typedContent = this.fluents.get(j).get(1);
                        String type = this.fluents.get(j).get(2);
                        if (typedContent.equals(contentInTheBox) && !wsContentTypeRequested.get(ws).remove(type)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Map<String, List<Integer>> extractPredicates() {
        Map<String, List<Integer>> ret = new HashMap<>();
        int index = 0;
        for (Fluent f : p.getFluents()) {
            String key = new StringTokenizer(p.toString(f), "( )", false).nextToken();
            ret.computeIfAbsent(key, k -> new ArrayList<>()).add(index);
            index++;
        }
        return ret;
    }

    private Map<Integer, List<String>> extractFluents() {
        Map<Integer, List<String>> ret = new HashMap<>();
        int index = 0;
        for (Fluent f : p.getFluents()) {
            List<String> tmp = new ArrayList<>();
            StringTokenizer tokenizer = new StringTokenizer(p.toString(f), "( )", false);
            tokenizer.nextToken();
            while (tokenizer.hasMoreTokens()) {
                tmp.add(tokenizer.nextToken());
            }
            ret.put(index, tmp);
            index++;
        }
        return ret;
    }
}
