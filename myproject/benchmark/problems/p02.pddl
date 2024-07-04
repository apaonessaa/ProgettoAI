(define
    (problem p02)
    (:domain industrial_manufacturing)

    (:objects
        loc1 loc2 - location
        ws1 ws2 - workstation
        b1 - box
        a1 a2 - agent
        valve bolt tool - content
    )
    (:init
        (at-agent a1 loc1)
        (at-agent a2 loc2)
        (at-box b1 loc1)
        (at-ws ws1 loc1)
        (at-ws ws2 loc2)
        (at-content valve loc1)
        (at-content bolt loc2)
        (at-content tool loc2)
        (connected loc1 loc2)
        (connected loc2 loc1)
        (workstation-needs ws1 valve)
        (workstation-needs ws1 bolt)
        (empty-hand a1)
        (empty-hand a2)
        (empty b1)
    )

    (:goal
        (and
            (content-at-workstation ws1 valve)
            (content-at-workstation ws1 bolt)
            (at-agent a2 loc2)
            (content-at-workstation ws1 tool)
        )
    )
)
