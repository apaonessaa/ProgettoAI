(define (domain industrial_manufacturing_no_carrier)
    (:requirements :strips :typing)
    (:types 
        location box content agent workstation type - object
    )

    (:predicates
        (connected ?loc1 ?loc2 - location)
        (box_at_loc ?box - box ?loc - location)
        (agent_at_loc ?agent - agent ?loc - location)
        (ws_at_loc ?ws - workstation ?loc - location)
        (content_at_loc ?content - content ?loc - location)
        (is_type ?content - content ?t - type)
        (empty_box ?box - box)
        (filled_box ?box - box ?content - content)
        (box_at_ws ?box - box ?ws - workstation)
        (box_at_agent ?box - box ?agent - agent)
        (content_type_at_ws ?t - type ?ws - workstation)
        (empty_agent ?agent - agent)
    )

    (:action pick_up
        :parameters (?loc - location ?box - box ?agent - agent)
        :precondition (and
            (empty_agent ?agent)
            (agent_at_loc ?agent ?loc)
            (box_at_loc ?box ?loc)
        )
        :effect (and
            (not (empty_agent ?agent))
            (box_at_agent ?box ?agent)
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
        ) 
        :precondition (and 
            (ws_at_loc ?ws ?location) 
            (agent_at_loc ?agent ?location) 
            (box_at_agent ?box ?agent)
        ) 
        :effect (and 
            (not (box_at_agent ?box ?agent)) 
            (empty_agent ?agent)
            (box_at_ws ?box ?ws) 
        ) 
    ) 

    (:action fill 
        :parameters ( 
            ?box - box  
            ?content - content 
            ?loc - location  
            ?agent - agent  
        ) 
        :precondition (and 
            (content_at_loc ?content ?loc) 
            (agent_at_loc ?agent ?loc) 
            (box_at_agent ?box ?agent) 
            (empty_box ?box) 
        ) 
        :effect (and 
            (not (empty_box ?box)) 
            (filled_box ?box ?content) 
            (not (content_at_loc ?content ?loc)) 
        ) 
    ) 

    (:action empty
        :parameters (
            ?box - box 
            ?content - content 
            ?t - type 
            ?ws - workstation 
            ?agent - agent 
            ?loc - location
        )
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
