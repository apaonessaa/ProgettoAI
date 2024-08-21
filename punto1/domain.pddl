(define (domain industrial_manufacturing)
    (:requirements :strips :typing)
    (:types location box content agent workstation type carrier place - object)

    (:predicates
        (connected ?loc1 ?loc2 - location)
        (content_at_loc ?content - content ?loc - location)
        (box_at_loc ?box - box ?loc - location)
        (agent_at_loc ?agent - agent ?loc - location)
        (ws_at_loc ?ws - workstation ?loc - location)
        (is_type ?content - content ?t - type)
        (empty_box ?box - box)
        (filled_box ?box - box ?content - content)
        (box_at_ws ?box - box ?ws - workstation)
        (content_type_at_ws ?t - type ?ws - workstation)

        (box_at_place ?box - box ?place - place)
        (empty_place ?place - place)
        (place_at_carrier ?place - place ?carrier - carrier)
        (carrier_at_agent ?carrier - carrier ?agent - agent)
    )

    (:action pick_up
        :parameters (?loc - location ?box - box ?agent - agent ?carrier - carrier ?place - place)
        :precondition (and
            (agent_at_loc ?agent ?loc)
            (box_at_loc ?box ?loc)
            (carrier_at_agent ?carrier ?agent)
            (place_at_carrier ?place ?carrier)
            (empty_place ?place)
        )
        :effect (and
            (box_at_place ?box ?place)
            (not (empty_place ?place))
            (not (box_at_loc ?box ?loc))
        )
    )

    (:action move
        :parameters (?from ?to - location ?agent - agent)
        :precondition (and
            (connected ?from ?to)
            (agent_at_loc ?agent ?from)
        )
        :effect (and
            (not (agent_at_loc ?agent ?from))
            (agent_at_loc ?agent ?to)
        )
    )

    (:action deliver 
        :parameters ( 
            ?box - box  
            ?ws - workstation  
            ?location - location  
            ?agent - agent  
            ?carrier - carrier  
            ?place - place 
        ) 
        :precondition (and 
            (ws_at_loc ?ws ?location) 
            (agent_at_loc ?agent ?location) 
            (carrier_at_agent ?carrier ?agent) 
            (place_at_carrier ?place ?carrier) 
            (box_at_place ?box ?place) 
        ) 
        :effect (and 
            (not (box_at_place ?box ?place)) 
            (empty_place ?place) 
            (box_at_ws ?box ?ws) 
        ) 
    ) 

    (:action fill 
        :parameters ( 
            ?box - box  
            ?content - content 
            ?loc - location  
            ?agent - agent  
            ?carrier - carrier  
            ?place - place 
        ) 
        :precondition (and 
            (content_at_loc ?content ?loc) 
            (agent_at_loc ?agent ?loc) 
            (carrier_at_agent ?carrier ?agent) 
            (place_at_carrier ?place ?carrier) 
            (box_at_place ?box ?place) 
            (empty_box ?box) 
        ) 
        :effect (and 
            (not (empty_box ?box)) 
            (filled_box ?box ?content) 
            (not (content_at_loc ?content ?loc)) 
        ) 
    ) 

    (:action empty
        :parameters (?box - box ?content - content ?t - type ?ws - workstation ?agent - agent ?loc - location)
        :precondition (and
            (is_type ?content ?t)
            (box_at_ws ?box ?ws)
            (ws_at_loc ?ws ?loc)
            (filled_box ?box ?content)
            (agent_at_loc ?agent ?loc)
        )
        :effect (and
            (not (filled_box ?box ?content))
            (empty_box ?box)
            (box_at_loc ?box ?loc)
            (not (box_at_ws ?box ?ws))
            (content_type_at_ws ?t ?ws)
        )
    )
)
