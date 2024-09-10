(define (domain industrial_manufacturing_durative)
    (:requirements :strips :typing :fluents :durative-actions)
    (:types
        location box content agent workstation type carrier place
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
        (box_at_place ?box - box ?place - place)
        (content_type_at_ws ?t - type ?ws - workstation)
        (empty_place ?place - place)
        (place_at_carrier ?place - place ?carrier - carrier)
        (carrier_at_agent ?carrier - carrier ?agent - agent)
        (free_agent ?agent - agent)
    )

    (:functions
        (pick_duration)
        (fill_duration)
        (move_duration)
        (deliver_duration)
        (empty_duration)
    )

    (:durative-action pick_up
        :parameters (?loc - location ?box - box ?agent - agent ?carrier - carrier ?place - place)
        :duration (= ?duration (pick_duration))
        :condition (and
            (at start (free_agent ?agent))
            (over all (agent_at_loc ?agent ?loc))
            (at start (box_at_loc ?box ?loc))
            (over all (carrier_at_agent ?carrier ?agent))
            (over all (place_at_carrier ?place ?carrier))
            (over all (empty_place ?place))
        )
        :effect (and
            (at start (not(free_agent ?agent)))
            (at end (box_at_place ?box ?place))
            (at end (not (empty_place ?place)))
            (at start (not (box_at_loc ?box ?loc)))
            (at end (free_agent ?agent))
        )
    )

    (:durative-action move
        :parameters (?from ?to - location ?agent - agent)
        :duration (= ?duration (move_duration))
        :condition (and
            (at start (free_agent ?agent))
            (over all (connected ?from ?to))
            (at start (agent_at_loc ?agent ?from))
        )
        :effect (and
            (at start (not(free_agent ?agent)))
            (at start (not (agent_at_loc ?agent ?from)))
            (at end (agent_at_loc ?agent ?to))
            (at end (free_agent ?agent))
        )
    )

    (:durative-action deliver
        :parameters (?box - box ?ws - workstation ?location - location ?agent - agent ?carrier - carrier ?place - place)
        :duration (= ?duration (deliver_duration))
        :condition (and
            (at start (free_agent ?agent))
            (over all (ws_at_loc ?ws ?location))
            (over all (agent_at_loc ?agent ?location))
            (over all (carrier_at_agent ?carrier ?agent))
            (over all (place_at_carrier ?place ?carrier))
            (over all (box_at_place ?box ?place))
        )
        :effect (and
            (at start (not(free_agent ?agent)))
            (at end (not (box_at_place ?box ?place)))
            (at end (empty_place ?place))
            (at end (box_at_ws ?box ?ws))
            (at end (free_agent ?agent))
        )
    )

    (:durative-action fill
        :parameters (?box - box ?content - content ?loc - location ?agent - agent ?carrier - carrier ?place - place)
        :duration (= ?duration (fill_duration))
        :condition (and
            (at start (free_agent ?agent))
            (at start (content_at_loc ?content ?loc))
            (over all (agent_at_loc ?agent ?loc))
            (over all (carrier_at_agent ?carrier ?agent))
            (over all (place_at_carrier ?place ?carrier))
            (over all (box_at_place ?box ?place))
            (over all (empty_box ?box))
        )
        :effect (and
            (at start (not(free_agent ?agent)))
            (at end (not (empty_box ?box)))
            (at end (filled_box ?box ?content))
            (at start (not (content_at_loc ?content ?loc)))
            (at end (free_agent ?agent))
        )
    )

    (:durative-action empty
        :parameters (?box - box ?content - content ?t - type ?ws - workstation ?agent - agent ?loc - location)
        :duration (= ?duration (empty_duration))
        :condition (and
            (at start (free_agent ?agent))
            (over all (is_type ?content ?t))
            (at start (box_at_ws ?box ?ws))
            (over all (ws_at_loc ?ws ?loc))
            (over all (filled_box ?box ?content))
            (over all (agent_at_loc ?agent ?loc))
        )
        :effect (and
            (at start (not(free_agent ?agent)))
            (at end (not (filled_box ?box ?content)))
            (at end (empty_box ?box))
            (at end (box_at_loc ?box ?loc))
            (at start (not (box_at_ws ?box ?ws)))
            (at end (content_type_at_ws ?t ?ws))
            (at end (free_agent ?agent))
        )
    )
)
