(define (domain industrial_manufacturing)
    (:requirements :strips :typing)
    (:types
        location box content agent workstation content-type carrier place - object
    )

    (:predicates
        (connected ?loc1 ?loc2 - location)
        (box-at-loc ?box - box ?loc - location)
        (agent-at-loc ?agent - agent ?loc - location)
        (ws-at-loc ?ws - workstation ?loc - location)
        (content-at-loc ?content - content ?loc - location)
        (is-type ?content - content ?t - content-type)
        (empty-box ?box - box)
        (filled-box ?box - box ?content - content)
        (box-at-ws ?box - box ?ws - workstation)
        (box-at-place ?box - box ?place - place)
        (content-type-at-ws ?t - content-type ?ws - workstation)
        (empty-place ?place - place)
        (place-at-carrier ?place - place ?carrier - carrier)
        (carrier-at-agent ?carrier - carrier ?agent - agent)
    )

    (:action pick-up
        :parameters (?loc - location ?box - box ?agent - agent ?carrier - carrier ?place - place)
        :precondition (and
            (agent-at-loc ?agent ?loc)
            (box-at-loc ?box ?loc)
            (carrier-at-agent ?carrier ?agent)
            (place-at-carrier ?place ?carrier)
            (empty-place ?place)
        )
        :effect (and
            (box-at-place ?box ?place)
            (not (empty-place ?place))
            (not (box-at-loc ?box ?loc))
        )
    )

    (:action move
        :parameters (?from ?to - location ?agent - agent)
        :precondition (and
            (connected ?from ?to)
            (agent-at-loc ?agent ?from)
        )
        :effect (and
            (not (agent-at-loc ?agent ?from))
            (agent-at-loc ?agent ?to)
        )
    )

    (:action deliver
        :parameters (?box - box ?content - content ?t - content-type ?ws - workstation ?location - location ?agent - agent ?carrier - carrier ?place - place)
        :precondition (and
            (is-type ?content ?t)
            (ws-at-loc ?ws ?location)
            (agent-at-loc ?agent ?location)
            (carrier-at-agent ?carrier ?agent)
            (place-at-carrier ?place ?carrier)
            (box-at-place ?box ?place)
            (filled-box ?box ?content)
        )
        :effect (and
            (not (box-at-place ?box ?place))
            (empty-place ?place)
            (box-at-ws ?box ?ws)
        )
    )

    (:action fill
        :parameters (?box - box ?content - content ?t - content-type ?loc - location ?agent - agent ?carrier - carrier ?place - place)
        :precondition (and
            (is-type ?content ?t)
            (content-at-loc ?content ?loc)
            (agent-at-loc ?agent ?loc)
            (carrier-at-agent ?carrier ?agent)
            (place-at-carrier ?place ?carrier)
            (box-at-place ?box ?place)
            (empty-box ?box)
        )
        :effect (and
            (not (empty-box ?box))
            (filled-box ?box ?content)
            (not (content-at-loc ?content ?loc))
        )
    )

    (:action empty
        :parameters (?box - box ?content - content ?t - content-type ?ws - workstation ?agent - agent ?loc - location)
        :precondition (and
            (is-type ?content ?t)
            (box-at-ws ?box ?ws)
            (ws-at-loc ?ws ?loc)
            (filled-box ?box ?content)
            (agent-at-loc ?agent ?loc)
        )
        :effect (and
            (not (filled-box ?box ?content))
            (empty-box ?box)
            (box-at-loc ?box ?loc)
            (not (box-at-ws ?box ?ws))
            (content-type-at-ws ?t ?ws)
        )
    )
)
