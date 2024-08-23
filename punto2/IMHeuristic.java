package fr.uga.pddl4j.myproject;

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
    private final HashMap<Integer, List<String>> fluents;

    private HashMap<String, String> typeOf;

    private final List<String> typeRequested;

    private boolean debug = false;

    public IMHeuristic(Problem problem) {
        super(problem);
        this.p = problem;
        // extraction the fluents and initializing of support struct
        this.fluents = new HashMap<>();
        this.typeOf = new HashMap<>();
        int index = 0;
        for (Fluent f : p.getFluents()) {
            StringTokenizer st = new StringTokenizer(p.toString(f), "( )", false);
            this.fluents.computeIfAbsent(index, k -> new ArrayList<>());
            while (st.hasMoreTokens())
                this.fluents.get(index).add(st.nextToken());
            if (this.fluents.get(index).get(0).equals("is_type"))
                this.typeOf.put(this.fluents.get(index).get(1), this.fluents.get(index).get(2));
            index++;
        }

        // Struct that contains the type requested by ws
        this.typeRequested = new LinkedList<>();
        BitVector gpk = new BitVector(problem.getGoal());
        for (int p = gpk.nextSetBit(0); p >= 0; p = gpk.nextSetBit(p + 1)) {
            // content_type_at_ws ?t - type ?ws - workstation
            this.typeRequested.add(this.fluents.get(p).get(1));
        }

        if(debug) {
            System.out.println(this.fluents);
            System.out.println(this.typeOf);
            System.out.println(this.typeRequested);
        }
    }

    @Override
    public double estimate(Node node, Condition goal) {
        return this.estimate((State)node, goal);
    }

    @Override
    public int estimate(State state, Condition goal) {
        super.setGoal(goal);
        int goalSatisfied = 0;
        int penaltyBoxAtLoc = 0;
        int countEmptyPlace = 0;
        int countContentAtCw = 0;
        int bonusTypeNecessary = 0;
        if (debug)
            System.out.println("-----------------------------------------");

        // tmp type requested struct
        List<String> tmpTypeRequested =this.typeRequested;

        BitVector ppk = new BitVector(state);
        for (int p = ppk.nextSetBit(0); p >= 0; p = ppk.nextSetBit(p + 1)) {
            if(debug)
                System.out.println(this.fluents.get(p));

            // 1. Count the goal not already satisfied in the current state
            if (this.fluents.get(p).get(0).equals("content_type_at_ws")) {
                // content_type_at_ws ?t - type ?ws - workstation
                goalSatisfied++;
            }

            // 2. (Penalty) empty box not located at cw
            if (this.fluents.get(p).get(0).equals("box_at_loc") && this.fluents.get(p).get(2).startsWith("loc")) {
                // box_at_loc ?box - box ?loc - location
                penaltyBoxAtLoc++;
            }

            // 3. (Bonus) maximize capacity of carriers (relaxed problem :- total_capacity - # not empty_place = empty_place)
            if (this.fluents.get(p).get(0).equals("empty_place")) {
                // box_at_loc ?box - box ?loc - location
                countEmptyPlace++;
            }

            // 4. (Penalty) count the number of request not satisfied (relaxed problem :- count the number of content located at cw)
            if (this.fluents.get(p).get(0).equals("content_at_loc") && this.fluents.get(p).get(2).startsWith("cw")) {
                // content_at_loc ?content - content ?loc - location
                countContentAtCw++;
            }

            // 5. (Penalty) box that contains a type not requested by any ws (relaxed problem :- no constraints on where is located the box)
            if (!tmpTypeRequested.isEmpty() && this.fluents.get(p).get(0).equals("filled_box")) {
                // filled_box ?box - box ?content - content
                // 2.1. box is filled with a type necessary
                if (this.typeOf.containsKey(this.fluents.get(p).get(2))) {
                    if(tmpTypeRequested.remove(this.typeOf.get(this.fluents.get(p).get(2)))) {
                        bonusTypeNecessary+=5;
                        //System.out.println(tmpTypeRequested);
                    }
                    else {
                        //countContentAtCw+=2;
                        penaltyBoxAtLoc+=10;
                    }
                }
            }

            if (tmpTypeRequested.isEmpty()) return -100;
        }
        int estimated = - goalSatisfied - bonusTypeNecessary + penaltyBoxAtLoc + countEmptyPlace + countContentAtCw;

        if(debug) {
            System.out.println(estimated);
            System.out.println("-----------------------------------------");
        }
        return estimated;
    }
}
