
package fr.uga.pddl4j.myproject;

import fr.uga.pddl4j.heuristics.state.RelaxedGraphHeuristic;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.Fluent;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.problem.operator.Effect;
import fr.uga.pddl4j.util.BitVector;

import java.util.*;

public class IMHeuristic extends RelaxedGraphHeuristic {
    private final HashMap<Integer, List<String>> fluents;
    private final HashMap<String, List<String>> placeOnCarrier;
    private final HashMap<String, String> carrierOnAgent;
    private final HashMap<String, List<String>> wsRequested;
    private final HashMap<String, String> typeOf;
    private final List<String> typeRequested;
    private final String NO_TYPE = "unknown";
    private final String LOCATION = "loc";
    private final String CENTRAL_WAREHOUSE = "cw";
    private boolean debug = false;
    public void setDebugMode(boolean debug) { this.debug = debug; }

    public IMHeuristic(Problem problem) {
        super(problem);
        // carrier -> places
        this.placeOnCarrier = new HashMap<>();
        // carrier -> agent
        this.carrierOnAgent = new HashMap<>();

        // Extraction the fluents and initializing of support struct
        this.fluents = new HashMap<>();
        this.typeOf = new HashMap<>();
        int index = 0;
        String predicate, content, type, place, carrier, agent;
        for (Fluent f : problem.getFluents()) {
            StringTokenizer st = new StringTokenizer(problem.toString(f), "( )", false);
            this.fluents.computeIfAbsent(index, k -> new ArrayList<>());
            while (st.hasMoreTokens())
                this.fluents.get(index).add(st.nextToken());
            // Collect default predicate
            predicate = this.fluents.get(index).get(0);
            switch (predicate) {
                case "is_type":
                    // is_type ?content - content ?t - type
                    content=this.fluents.get(index).get(1);
                    type=this.fluents.get(index).get(2);
                    this.typeOf.put(content, type);
                    break;
                case "place_at_carrier":
                    // place_at_carrier ?place - place ?carrier - carrier
                    place=this.fluents.get(index).get(1);
                    carrier=this.fluents.get(index).get(2);
                    this.placeOnCarrier.computeIfAbsent(carrier, k -> new ArrayList<>());
                    this.placeOnCarrier.get(carrier).add(place);
                    break;
                case "carrier_at_agent":
                    // carrier_at_agent ?carrier - carrier ?agent - agent
                    carrier=this.fluents.get(index).get(1);
                    agent=this.fluents.get(index).get(2);
                    this.carrierOnAgent.put(carrier, agent);
                    break;
            }
            index++;
        }

        // Structs that contain the type requested by ws
        this.typeRequested = new LinkedList<>();
        this.wsRequested = new HashMap<>();
        String ws;
        BitVector gpk = new BitVector(problem.getGoal());
        for (int p = gpk.nextSetBit(0); p >= 0; p = gpk.nextSetBit(p + 1)) {
            // content_type_at_ws ?t - type ?ws - workstation
            type = this.fluents.get(p).get(1);
            ws = this.fluents.get(p).get(2);
            this.typeRequested.add(type);
            this.wsRequested.computeIfAbsent(ws, k->new LinkedList<>());
            this.wsRequested.get(ws).add(type);
        }

        if(debug) {
            System.out.println("Fluents: "+this.fluents);
            System.out.println("Content type: "+this.typeOf);
            System.out.println("Type requested: "+this.typeRequested);
            System.out.println("Ws requested: "+this.wsRequested);
        }
    }

    @Override
    public double estimate(Node node, Condition goal) {
        return this.estimate((State)node, goal);
    }

    @Override
    public int estimate(State state, Condition goal) {
        super.setGoal(goal);

        if (debug) System.out.println("-----------------------------------------");
        /**
         * Estimated cost from current state
         * */
        int goalSatisfied = -10;
        int penaltyBoxAtLoc = +5;
        int penaltyContentAtCw = +1;
        int bonusTypeNecessary = -5;
        int malusNoTypeNecessary = +10;
        int bonusBoxAtPlace = -1;
        int penaltyBoxAtWs = +2;
        int penaltyAgentAtLoc = +10;
        int evalState = 0;

        // Define the type and ws that are requested and not already satisfied in current state
        List<String> tmpTypeRequested = this.typeRequested;
        HashMap<String, List<String>> tmpWsRequested = this.wsRequested;

        // Define for each agent a list of box (box at place[empty or filled])
        HashMap<String, List<String>> boxAtAgent = new HashMap<>();
        // Define for each box the content type which it is filled
        HashMap<String, String> boxContentType = new HashMap<>();

        List<String> tmp;
        String predicate, loc, ws, box, content, agent, carrier, place, type;
        BitVector ppk = new BitVector(state);
        for (int p = ppk.nextSetBit(0); p >= 0; p = ppk.nextSetBit(p + 1)) {
            predicate = this.fluents.get(p).get(0);
            switch (predicate) {
                // 1. Count the goal already satisfied in the current state
                case "content_type_at_ws":
                    // content_type_at_ws ?t - type ?ws - workstation
                    type = this.fluents.get(p).get(1);
                    ws = this.fluents.get(p).get(2);
                    evalState += goalSatisfied;

                    // Update ws request and type requested
                    tmp = tmpWsRequested.getOrDefault(ws, new LinkedList<>());
                    if (!tmp.isEmpty()) {
                        tmpWsRequested.get(ws).remove(type); // remove satisfied goal
                        tmpTypeRequested.remove(type);
                    }
                    break;

                // 2. (Penalty) the content at CENTRAL_WAREHOUSE that is necessary to satisfed the goal (relaxed problem :- count the number of content located at cw)
                case "content_at_loc":
                    // content_at_loc ?content - content ?loc - location
                    content = this.fluents.get(p).get(1);
                    loc = this.fluents.get(p).get(2);
                    evalState += loc.startsWith(CENTRAL_WAREHOUSE) ? (this.typeRequested.contains(this.typeOf.get(content)) ? penaltyContentAtCw : -1): 0;
                    break;

                // 3. (Penalty) the box that contains a type not requested by any ws, otherwise (Bonus) (relaxed problem :- no constraints on where is located the box)
                case "filled_box":
                    // filled_box ?box - box ?content - content
                    box = this.fluents.get(p).get(1);
                    content = this.fluents.get(p).get(2);
                    type = this.typeOf.getOrDefault(content, NO_TYPE);
                    evalState += this.typeRequested.contains(type) ? bonusTypeNecessary : malusNoTypeNecessary;
                    if (!type.equals(NO_TYPE)) boxContentType.put(box, this.typeOf.get(content));
                    break;

                // 4. (Penalty) the empty box that are not allocated at CENTRAL_WAREHOUSE
                case "box_at_loc":
                    // box_at_loc ?box - box ?loc - location
                    //box = this.fluents.get(p).get(1);
                    loc = this.fluents.get(p).get(2);
                    evalState += loc.startsWith(LOCATION) ? penaltyBoxAtLoc : 0;
                    break;

                // 5. (Bonus) the box on carrier
                case "box_at_place":
                    // box_at_place ?box - box ?place - place
                    // for each carrier, find the place of box_at_place and collect in the struct
                    box = this.fluents.get(p).get(1);
                    place = this.fluents.get(p).get(2);
                    for (Map.Entry<String, List<String>> entry : this.placeOnCarrier.entrySet()) {
                        carrier = entry.getKey();
                        if (entry.getValue().contains(place)) {
                            agent = this.carrierOnAgent.get(carrier);
                            boxAtAgent.computeIfAbsent(agent, k -> new ArrayList<>());
                            boxAtAgent.get(agent).add(box);
                            break;
                        }
                    }
                    evalState += bonusBoxAtPlace;
                    break;

                // 6. (Penalty) the agent that is no located at CENTRAL_WAREHOUSE
                case "agent_at_loc":
                    // agent_at_loc ?agent - agent ?loc - location
                    //agent = this.fluents.get(p).get(1);
                    loc = this.fluents.get(p).get(2);
                    evalState += loc.startsWith(LOCATION) ? penaltyAgentAtLoc : 0;
                    break;

                // 7. (Penalty) the box that is located at ws
                case "box_at_ws":
                    // box_at_ws ?box - box ?ws - workstation
                    //box = this.fluents.get(p).get(1);
                    ws = this.fluents.get(p).get(2);
                    evalState += this.wsRequested.containsKey(ws) ? 0 : penaltyBoxAtWs;
                    break;

                default:
                    // other predicates, MAX penalty + 1
                    evalState+=11;
                    break;
            }

            // All the request are satisfied
            if (tmpTypeRequested.isEmpty()) return 0;
        }

        if (debug) {
            System.out.println("Box at Agent: "+boxAtAgent);
            System.out.println("Box & content type: "+boxContentType);
            System.out.println("Ws to satisfy: "+tmpWsRequested);
            System.out.println("Type requested: "+tmpTypeRequested);
        }

        /**
         * For each available action for the current state, assign a value
         * */
        Effect effects;
        BitVector positiveFluents; //, negativeFluents;
        int[] actions = new int[]{
            Integer.MAX_VALUE, // 0 - pick_up
            Integer.MAX_VALUE, // 1 - move
            Integer.MAX_VALUE, // 2 - deliver
            Integer.MAX_VALUE, // 3 - fill
            Integer.MAX_VALUE  // 4 - empty
        };
        String toLoc; //, fromLoc;
        int moveBonus = -1, movePenalty = +1, moveCost, index;
        boolean lastOne = tmpTypeRequested.size()==1;
        for (Action op : this.getActions()) {
            if (op.isApplicable(state)) {
                effects = op.getConditionalEffects().get(0).getEffect();
                positiveFluents = effects.getPositiveFluents();
                //negativeFluents = effects.getNegativeFluents();
                moveCost = Integer.MAX_VALUE;
                index = -1;
                switch (op.getName()) {
                    case "pick_up":
                        /**
                         *      Positive fluents:
                         *          [box_at_place, box, place]
                         *      --------------------------------
                         *      Negative fluents:
                         *          [empty_place, place]
                         *          [box_at_loc, box, loc]
                         */
                        System.out.println(tmpWsRequested.values().size());
                        moveCost = tmpTypeRequested.size()*moveBonus;
                        index=0;
                        break;
                    case "move":
                        /**
                         *      Positive fluents:
                         *          [agent_at_loc, agent, loc]
                         *      --------------------------------
                         *      Negative fluents:
                         *          [agent_at_loc, agent, loc]
                         * */
                        agent = this.fluents.get(positiveFluents.nextSetBit(0)).get(1);
                        toLoc = this.fluents.get(positiveFluents.nextSetBit(0)).get(2);
                        //fromLoc = this.fluents.get(negativeFluents.nextSetBit(0)).get(2);
                        // Get the carrier on agent
                        carrier = "";
                        for (Map.Entry<String, String> entry : this.carrierOnAgent.entrySet()) {
                            carrier = entry.getKey();
                            if (entry.getValue().equals(agent)) {
                                // carrier founded
                                break;
                            }
                        }
                        // Get the number of places on carrier
                        int totalCapacity = this.placeOnCarrier.get(carrier).size();
                        // Check all boxes on carrier (boxAtAgent)
                        List<String> boxes = boxAtAgent.getOrDefault(agent, new LinkedList<>());
                        if (boxes.isEmpty()) {
                            moveCost = totalCapacity*movePenalty;
                        } else {
                            int necessary = 0, unnecessary = 0;
                            for (String theBox : boxes) {
                                type = this.typeOf.getOrDefault(theBox, NO_TYPE);
                                if (!type.equals(NO_TYPE)){
                                    if (tmpTypeRequested.contains(type))
                                        necessary++;
                                    else
                                        unnecessary++;
                                }
                            }
                            moveCost = toLoc.startsWith(LOCATION) ?
                                (necessary>0 ? (necessary-unnecessary)*moveBonus : totalCapacity*movePenalty) :
                                (necessary>0 ? totalCapacity*moveBonus : (totalCapacity-unnecessary)*movePenalty);
                        }
                        index=1;
                        break;
                    case "deliver":
                        /**
                         *      Positive fluents:
                         *          [empty_place, place]
                         *          [box_at_ws, box, ws]
                         *      --------------------------------
                         *      Negative fluents:
                         *          [box_at_place, box, place]
                         */
                        for (int p = positiveFluents.nextSetBit(0); p >= 0; p = positiveFluents.nextSetBit(p + 1)) {
                            predicate = this.fluents.get(p).get(0);
                            if (predicate.equals("box_at_ws")) {
                                box = this.fluents.get(p).get(1);
                                ws = this.fluents.get(p).get(2);
                                type = boxContentType.getOrDefault(box, NO_TYPE);
                                if (type.equals(NO_TYPE))
                                    moveCost = movePenalty;
                                else {
                                    tmp = tmpWsRequested.getOrDefault(ws, new LinkedList<>());
                                    if (tmp.isEmpty())
                                        moveCost = movePenalty;
                                    else
                                        moveCost = tmp.contains(type) ? (lastOne ? 10*moveBonus : moveBonus) : movePenalty;
                                }
                                break;
                            }
                        }
                        index=2;
                        break;
                    case "fill":
                        /**
                         *      Positive fluents:
                         *          [filled_box, box, content]
                         *      --------------------------------
                         *      Negative fluents:
                         *          [content_at_loc, content, loc]
                         *          [empty_box, box]
                         * */
                        //box = this.fluents.get(positiveFluents.nextSetBit(0)).get(1);
                        content = this.fluents.get(positiveFluents.nextSetBit(0)).get(2);
                        type = this.typeOf.getOrDefault(content, NO_TYPE);
                        if (type.equals(NO_TYPE))
                            moveCost = movePenalty;
                        else
                            moveCost = tmpTypeRequested.contains(type) ? (lastOne ? 10*moveBonus : moveBonus) : movePenalty;
                        index=3;
                        break;
                    case "empty":
                        /**
                         *      Positive fluents:
                         *          [box_at_loc, box, loc]
                         *          [empty_box, box]
                         *          [content_type_at_ws, type, ws]
                         *      --------------------------------
                         *      Negative fluents:
                         *          [box_at_ws, box, ws]
                         *          [filled_box, box, content]
                         */
                        for (int p = positiveFluents.nextSetBit(0); p >= 0; p = positiveFluents.nextSetBit(p + 1)) {
                            predicate = this.fluents.get(p).get(0);
                            if (predicate.equals("content_type_at_ws")) {
                                type = this.fluents.get(p).get(1);
                                ws = this.fluents.get(p).get(2);
                                tmp = tmpWsRequested.getOrDefault(ws, new LinkedList<>());
                                if (tmp.isEmpty())
                                    moveCost = movePenalty;
                                else
                                    moveCost = tmp.contains(type) ? (lastOne ? 10 * moveBonus : moveBonus) : movePenalty;
                                break;
                            }
                        }
                        index=4;
                        break;
                    default:
                        break;
                }
                if (index!=-1 && moveCost < actions[index]) actions[index] = moveCost;
            }
        }

        int estimated = evalState
            + (actions[0]<Integer.MAX_VALUE ? 2*actions[0] : 0)
            + (actions[1]<Integer.MAX_VALUE ? 3*actions[1] : 0)
            + (actions[2]<Integer.MAX_VALUE ? 6*actions[2] : 0)
            + (actions[3]<Integer.MAX_VALUE ? 4*actions[3] : 0)
            + (actions[4]<Integer.MAX_VALUE ? 9*actions[4] : 0);

        if(debug) {
            System.out.println("<"+evalState+">");
            if (actions[0]<Integer.MAX_VALUE) System.out.println("<Pick up:"+actions[0]+">");
            if (actions[1]<Integer.MAX_VALUE) System.out.println("<Move:"+actions[1]+">");
            if (actions[2]<Integer.MAX_VALUE) System.out.println("<Deliver:"+actions[2]+">");
            if (actions[3]<Integer.MAX_VALUE) System.out.println("<Fill:"+actions[3]+">");
            if (actions[4]<Integer.MAX_VALUE) System.out.println("<Empty:"+actions[4]+">");
            System.out.println("<"+estimated+">");
            System.out.println("-----------------------------------------");
        }
        return estimated;
    }
}
