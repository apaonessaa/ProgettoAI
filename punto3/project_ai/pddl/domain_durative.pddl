(define (domain industrial_manufacturing_durative)
    (:requirements :strips :typing :fluents :durative-actions)
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
        (free-agent ?agent - agent)
    )

    (:functions
        (pick-duration)
        (fill-duration)
        (move-duration)
        (deliver-duration)
        (empty-duration)
    )

    (:durative-action pick-up
        :parameters (?loc - location ?box - box ?agent - agent ?carrier - carrier ?place - place)
        :duration (= ?duration (pick-duration))
        :condition (and
            (at start (free-agent ?agent))
            (over all (agent-at-loc ?agent ?loc))
            (at start (box-at-loc ?box ?loc))
            (over all (carrier-at-agent ?carrier ?agent))
            (over all (place-at-carrier ?place ?carrier))
            (over all (empty-place ?place))
        )
        :effect (and
            (at start (not(free-agent ?agent)))
            (at end (box-at-place ?box ?place))
            (at end (not (empty-place ?place)))
            (at start (not (box-at-loc ?box ?loc)))
            (at end (free-agent ?agent))
        )
    )

    (:durative-action move
        :parameters (?from ?to - location ?agent - agent)
        :duration (= ?duration (move-duration))
        :condition (and
            (at start (free-agent ?agent))
            (over all (connected ?from ?to))
            (at start (agent-at-loc ?agent ?from))
        )
        :effect (and
            (at start (not(free-agent ?agent)))
            (at start (not (agent-at-loc ?agent ?from)))
            (at end (agent-at-loc ?agent ?to))
            (at end (free-agent ?agent))
        )
    )

    (:durative-action deliver
        :parameters (?box - box ?ws - workstation ?location - location ?agent - agent ?carrier - carrier ?place - place)
        :duration (= ?duration (deliver-duration))
        :condition (and
            (at start (free-agent ?agent))
            (over all (ws-at-loc ?ws ?location))
            (over all (agent-at-loc ?agent ?location))
            (over all (carrier-at-agent ?carrier ?agent))
            (over all (place-at-carrier ?place ?carrier))
            (over all (box-at-place ?box ?place))
        )
        :effect (and
            (at start (not(free-agent ?agent)))
            (at end (not (box-at-place ?box ?place)))
            (at end (empty-place ?place))
            (at end (box-at-ws ?box ?ws))
            (at end (free-agent ?agent))
        )
    )

    (:durative-action fill
        :parameters (?box - box ?content - content ?loc - location ?agent - agent ?carrier - carrier ?place - place)
        :duration (= ?duration (fill-duration))
        :condition (and
            (at start (free-agent ?agent))
            (at start (content-at-loc ?content ?loc))
            (over all (agent-at-loc ?agent ?loc))
            (over all (carrier-at-agent ?carrier ?agent))
            (over all (place-at-carrier ?place ?carrier))
            (over all (box-at-place ?box ?place))
            (over all (empty-box ?box))
        )
        :effect (and
            (at start (not(free-agent ?agent)))
            (at end (not (empty-box ?box)))
            (at end (filled-box ?box ?content))
            (at start (not (content-at-loc ?content ?loc)))
            (at end (free-agent ?agent))
        )
    )

    (:durative-action empty
        :parameters (?box - box ?content - content ?t - content-type ?ws - workstation ?agent - agent ?loc - location)
        :duration (= ?duration (empty-duration))
        :condition (and
            (at start (free-agent ?agent))
            (over all (is-type ?content ?t))
            (at start (box-at-ws ?box ?ws))
            (over all (ws-at-loc ?ws ?loc))
            (over all (filled-box ?box ?content))
            (over all (agent-at-loc ?agent ?loc))
        )
        :effect (and
            (at start (not(free-agent ?agent)))
            (at end (not (filled-box ?box ?content)))
            (at end (empty-box ?box))
            (at end (box-at-loc ?box ?loc))
            (at start (not (box-at-ws ?box ?ws)))
            (at end (content-type-at-ws ?t ?ws))
            (at end (free-agent ?agent))
        )
    )
)