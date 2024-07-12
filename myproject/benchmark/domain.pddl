(define (domain industrial_manufacturing)
    (:requirements :strips :typing)
    (:types
        location box content agent workstation
    )

    (:predicates
        (at-box ?box - box ?loc - location)
        (at-agent ?agent - agent ?loc - location)
        (at-ws ?ws - workstation ?loc - location)
        (at-content ?content - content ?loc - location)
        (connected ?loc1 ?loc2 - location)
        (empty ?box - box)
        (filled ?box - box ?content - content)
        (box-at-workstation ?ws - workstation ?box - box)
        (content-at-workstation ?ws - workstation ?content - content)
        (workstation-needs ?ws - workstation ?content - content)
        (box-at-agent ?agent - agent ?box - box)
        (empty-hand ?agent - agent)
    )

    (:action pick-up
        :parameters (?loc - location ?box - box ?agent - agent)
        :precondition (and
            (empty-hand ?agent)
            (at-agent ?agent ?loc)
            (at-box ?box ?loc)
        )
        :effect (and
            (not (empty-hand ?agent))
            (box-at-agent ?agent ?box)
            (not (at-box ?box ?loc))
        )
    )

    (:action move
        :parameters (?from ?to - location ?agent - agent)
        :precondition (and
            (at-agent ?agent ?from)
            (connected ?from ?to)
        )
        :effect (and
            (not (at-agent ?agent ?from))
            (at-agent ?agent ?to)
        )
    )

    (:action deliver
        :parameters (?box - box ?content - content ?ws - workstation ?location - location ?agent - agent)
        :precondition (and
            (at-agent ?agent ?location)
            (box-at-agent ?agent ?box)
            (at-ws ?ws ?location)
            (filled ?box ?content)
            (workstation-needs ?ws ?content)
        )
        :effect (and
            (not (box-at-agent ?agent ?box))
            (empty-hand ?agent)
            (box-at-workstation ?ws ?box)
            (not (workstation-needs ?ws ?content))
        )
    )

    (:action fill
        :parameters (?box - box ?content - content ?loc - location ?agent - agent)
        :precondition (and
            (empty ?box)
            (box-at-agent ?agent ?box)
            (at-content ?content ?loc)
            (at-agent ?agent ?loc)
        )
        :effect (and
            (not (empty ?box))
            (filled ?box ?content)
            (not (at-content ?content ?loc))
        )
    )

    (:action empty
        :parameters (?box - box ?content - content ?ws - workstation ?agent - agent ?loc - location)
        :precondition (and
            (empty-hand ?agent)
            (filled ?box ?content)
            (box-at-workstation ?ws ?box)
            (at-agent ?agent ?loc)
            (at-ws ?ws ?loc)
        )
        :effect (and
            (not (filled ?box ?content))
            (empty ?box)
            (at-box ?box ?loc)
            (not (box-at-workstation ?ws ?box))
            (content-at-workstation ?ws ?content)
        )
    )
)
