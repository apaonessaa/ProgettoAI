
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
    private final Problem p;
    private final HashMap<Integer, List<String>> fluents;

    private HashMap<String, String> typeOf;

    // carrier -> places
    private HashMap<String, List<String>> placeOnCarrier;
    // carrier -> agent
    private HashMap<String, String> carrierOnAgent;

    private HashMap<String, List<String>> wsRequested;

    private List<String> typeRequested;

    private boolean debug = false;

    public IMHeuristic(Problem problem) {
        super(problem);
        this.p = problem;
        // carrier -> places
        this.placeOnCarrier = new HashMap<>();
        // carrier -> agent
        this.carrierOnAgent = new HashMap<>();

        // extraction the fluents and initializing of support struct
        this.fluents = new HashMap<>();
        this.typeOf = new HashMap<>();
        int index = 0;
        String place, carrier, agent;
        for (Fluent f : p.getFluents()) {
            StringTokenizer st = new StringTokenizer(p.toString(f), "( )", false);
            this.fluents.computeIfAbsent(index, k -> new ArrayList<>());
            while (st.hasMoreTokens())
                this.fluents.get(index).add(st.nextToken());
            // collect
            switch (this.fluents.get(index).get(0)) {
                case "is_type":
                    // is_type ?content - content ?t - type
                    this.typeOf.put(this.fluents.get(index).get(1), this.fluents.get(index).get(2));
                    break;
                case "place_at_carrier":
                    // place_at_carrier ?place - place ?carrier - carrier
                    place=this.fluents.get(index).get(1);
                    carrier=this.fluents.get(index).get(2);
                    placeOnCarrier.computeIfAbsent(carrier, k -> new ArrayList<>());
                    placeOnCarrier.get(carrier).add(place);
                    break;
                case "carrier_at_agent":
                    // carrier_at_agent ?carrier - carrier ?agent - agent
                    carrier=this.fluents.get(index).get(1);
                    agent=this.fluents.get(index).get(2);
                    carrierOnAgent.put(carrier, agent);
                    break;
            }
            index++;
        }

        // Struct that contains the type requested by ws
        this.typeRequested = new LinkedList<>();
        this.wsRequested = new HashMap<>();
        String ws, type;
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
            System.out.println(this.fluents);
            System.out.println(this.typeOf);
            System.out.println(this.typeRequested);
            System.out.println(this.wsRequested);
            //System.out.println(this.placeOnCarrier);
            //System.out.println(this.carrierOnAgent);
        }
    }

    @Override
    public double estimate(Node node, Condition goal) {
        return this.estimate((State)node, goal);
    }

    @Override
    public int estimate(State state, Condition goal) {
        super.setGoal(goal);

        if (debug)
            System.out.println("-----------------------------------------");

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
        int evalState = 0;

        // the type that are requested and not satisfied in current state
        List<String> tmpTypeRequested = this.typeRequested;
        HashMap<String, List<String>> tmpWsRequested = this.wsRequested;
        List<String> tmp;

        //HashMap<String, String> boxAtLoc = new HashMap<>();
        //HashMap<String, String> boxAtWs = new HashMap<>();
        HashMap<String, List<String>> boxAtAgent = new HashMap<>(); // agent -> list of box (box at place[empty or filled])
        //HashMap<String, String> agentAtLoc = new HashMap<>();
        HashMap<String, String> boxContentType = new HashMap<>();

        String predicate;
        String loc, ws, box, content, agent, carrier, place, type;
        BitVector ppk = new BitVector(state);
        for (int p = ppk.nextSetBit(0); p >= 0; p = ppk.nextSetBit(p + 1)) {
            //if(debug) System.out.println(this.fluents.get(p));

            predicate = this.fluents.get(p).get(0);

            switch (predicate) {
                // 1. Count the goal not already satisfied in the current state
                case "content_type_at_ws":
                    // content_type_at_ws ?t - type ?ws - workstation
                    type = this.fluents.get(p).get(1);
                    ws = this.fluents.get(p).get(2);
                    evalState += goalSatisfied;
                    tmp = tmpWsRequested.getOrDefault(ws, new LinkedList<>());
                    if (!tmp.isEmpty()) {
                        tmpWsRequested.get(ws).remove(type); // remove satisfied goal
                        tmpTypeRequested.remove(type);
                    }
                    break;

                // 2. (Penalty) count the number of request not satisfied (relaxed problem :- count the number of content located at cw)
                case "content_at_loc":
                    // content_at_loc ?content - content ?loc - location
                    content = this.fluents.get(p).get(1);
                    loc = this.fluents.get(p).get(2);
                    evalState += loc.startsWith("cw") ? (this.typeRequested.contains(this.typeOf.get(content)) ? penaltyContentAtCw : -1): 0;
                    break;

                // 3. (Penalty) box that contains a type not requested by any ws (relaxed problem :- no constraints on where is located the box)
                case "filled_box":
                    // filled_box ?box - box ?content - content
                    // 2.1. box is filled with a type necessary
                    box = this.fluents.get(p).get(1);
                    content = this.fluents.get(p).get(2);
                    type = this.typeOf.getOrDefault(content, "empty");
                    evalState += this.typeRequested.contains(type) ? bonusTypeNecessary : malusNoTypeNecessary;
                    if (!type.equals("empty")) boxContentType.put(box, this.typeOf.get(content));
                    break;

                /**
                 * Box distance to goal : box at location, box at agent, box at ws.
                 * */
                // 2. (Penalty) empty box not located at cw
                case "box_at_loc":
                    // box_at_loc ?box - box ?loc - location
                    box = this.fluents.get(p).get(1);
                    loc = this.fluents.get(p).get(2);
                    evalState+= loc.startsWith("loc") ? penaltyBoxAtLoc : 0;
                    //boxAtLoc.put(box, loc);
                    break;

                case "box_at_place":
                    // box_at_place ?box - box ?place - place
                    // for each carrier, find the place of box_at_place
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

                case "agent_at_loc":
                    // agent_at_loc ?agent - agent ?loc - location
                    agent = this.fluents.get(p).get(1);
                    loc = this.fluents.get(p).get(2);
                    evalState+=loc.startsWith("loc") ? +5 : 0;
                    //agentAtLoc.put(agent, loc);
                    break;

                case "box_at_ws":
                    // box_at_ws ?box - box ?ws - workstation
                    box = this.fluents.get(p).get(1);
                    ws = this.fluents.get(p).get(2);
                    evalState += this.wsRequested.containsKey(ws) ? 0 : penaltyBoxAtWs;
                    //boxAtWs.put(box, ws);
                    break;

                default:
                    break;
            }

            if (tmpTypeRequested.isEmpty()) return -500;
        }

        if (evalState==0) evalState=100;

        if (debug) {
            //System.out.println(boxAtLoc);
            System.out.println(boxAtAgent);
            //System.out.println(agentAtLoc);
            //System.out.println(boxAtWs);
            System.out.println(boxContentType);
            System.out.println(tmpWsRequested);
            System.out.println(tmpTypeRequested);
        }

        /**
         * Estimated cost from next states available from action perform on current state
         * */
        Effect effects;
        BitVector positiveFluents, negativeFluents;
        String toLoc, fromLoc;
        int moveBonus = -1;
        int movePenalty = +1;
        int moveCost;
        boolean lastOne = tmpTypeRequested.size()==1;
        // pick_up, move, deliver, fill, empty
        int[] moves = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
        int index = -1;
        for (Action op : this.getActions()) {
            if (op.isApplicable(state)) {
                effects = op.getConditionalEffects().get(0).getEffect();
                positiveFluents = effects.getPositiveFluents();
                negativeFluents = effects.getNegativeFluents();
                moveCost = Integer.MAX_VALUE;
                index = -1;
                switch (op.getName()) {
                    case "pick_up":
                        /**
                         *  >> pick_up
                         *      [box_at_place, b1, p1]
                         *      ---
                         *      [empty_place, p1]
                         *      [box_at_loc, b1, loc1]
                         */
                        moveCost = tmpTypeRequested.size()*moveBonus;
                        index=0;
                        //if (debug) System.out.println(op.getName()+">>"+distance);
                        break;
                    case "move":
                        /**
                         *   >> move
                         *      [agent_at_loc, a1, cw]
                         *      ---
                         *      [agent_at_loc, a1, loc1]
                         * */
                        agent = this.fluents.get(positiveFluents.nextSetBit(0)).get(1);
                        toLoc = this.fluents.get(positiveFluents.nextSetBit(0)).get(2);
                        fromLoc = this.fluents.get(negativeFluents.nextSetBit(0)).get(2);

                        // carrier on agent
                        carrier = "";
                        for (Map.Entry<String, String> entry : this.carrierOnAgent.entrySet()) {
                            carrier = entry.getKey();
                            if (entry.getValue().equals(agent)) {
                                // carrier founded
                                break;
                            }
                        }
                        // num place on carrier
                        int totalCapacity = this.placeOnCarrier.get(carrier).size();
                        // check all box at carrier (boxAtAgent)
                        List<String> boxes = boxAtAgent.getOrDefault(agent, new LinkedList<>());
                        if (boxes.isEmpty()) {
                            moveCost = totalCapacity*movePenalty;
                        } else {
                            int necessary = 0, unnecessary = 0;
                            for (String theBox : boxes) {
                                type = this.typeOf.getOrDefault(theBox, "empty");
                                if (!type.equals("empty")){
                                    if (tmpTypeRequested.contains(type)) necessary++;
                                    else unnecessary++;
                                }
                            }
                            moveCost = toLoc.startsWith("loc") ?
                                (necessary>0 ? (necessary-unnecessary)*moveBonus : totalCapacity*movePenalty) :
                                (necessary>0 ? totalCapacity*moveBonus : (totalCapacity-unnecessary)*movePenalty);
                        }
                        index=1;
                        break;
                    case "deliver":
                        /**
                         *  >> deliver
                         *      [empty_place, p1]
                         *      [box_at_ws, b1, ws1]
                         *      ---
                         *      [box_at_place, b1, p1]
                         */
                        for (int p = positiveFluents.nextSetBit(0); p >= 0; p = positiveFluents.nextSetBit(p + 1)) {
                            predicate = this.fluents.get(p).get(0);
                            if (predicate.equals("box_at_ws")) {
                                box = this.fluents.get(p).get(1);
                                ws = this.fluents.get(p).get(2);
                                type = boxContentType.getOrDefault(box, "empty");
                                if (type.equals("empty"))
                                    moveCost = movePenalty;
                                    //if (debug) System.out.println(op.getName()+">>"+box+",empty,Malus");
                                else {
                                    tmp = tmpWsRequested.getOrDefault(ws, new LinkedList<>());
                                    if (tmp.isEmpty())
                                        moveCost = movePenalty;
                                    else
                                        moveCost = tmp.contains(type) ? (lastOne ? 10*moveBonus : moveBonus) : movePenalty;
                                }
                                    //if (debug) System.out.println(op.getName()+">>"+box+","+type+","+(tmpTypeRequested.contains(type) ? "BBonus" : "MMalus"));
                                break;
                            }
                        }
                        index=2;
                        break;
                    case "fill":
                        /**
                         * >> fill
                         *      [filled_box, b1, valve1]
                         *      ---
                         *      [content_at_loc, valve1, cw]
                         *      [empty_box, b1]
                         * */
                        box = this.fluents.get(positiveFluents.nextSetBit(0)).get(1);
                        content = this.fluents.get(positiveFluents.nextSetBit(0)).get(2);
                        type = this.typeOf.getOrDefault(content, "empty");
                        if (type.equals("empty"))
                            moveCost = movePenalty;
                        else
                            moveCost = tmpTypeRequested.contains(type) ? (lastOne ? 10*moveBonus : moveBonus) : movePenalty;
                            //if (debug) System.out.println(op.getName()+">>"+content+","+type+","+(tmpTypeRequested.contains(type) ? "Bonus" : "Malus"));
                        index=3;
                        break;
                    case "empty":
                        /**
                         * >> empty
                         *      [box_at_loc, b1, loc1]
                         *      [empty_box, b1]
                         *      [content_type_at_ws, valve, ws1]
                         *      ---
                         *      [box_at_ws, b1, ws1]
                         *      [filled_box, b1, valve1]
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
                                    moveCost = tmp.contains(type) ? (lastOne ? 10*moveBonus : moveBonus) : movePenalty;
                                break;
                            }
                        }
                        index=4;
                        break;
                    default:
                        break;
                }
                if (index!=-1 && moveCost < moves[index]) moves[index] = moveCost;
            }
        }

        if(debug) {
            System.out.println("<"+evalState+">");
            // pick_up, move, deliver, fill, empty
            System.out.println("Best pick up:"+moves[0]);
            System.out.println("Best move:"+moves[1]);
            System.out.println("Best deliver:"+moves[2]);
            System.out.println("Best fill:"+moves[3]);
            System.out.println("Best empty:"+moves[4]);
            System.out.println("-----------------------------------------");
        }
        return evalState
            + (moves[0]<Integer.MAX_VALUE ? 2*moves[0] : 0)
            + (moves[1]<Integer.MAX_VALUE ? 3*moves[1] : 0)
            + (moves[2]<Integer.MAX_VALUE ? 6*moves[2] : 0)
            + (moves[3]<Integer.MAX_VALUE ? 4*moves[3] : 0)
            + (moves[4]<Integer.MAX_VALUE ? 9*moves[4] : 0);
    }
}
