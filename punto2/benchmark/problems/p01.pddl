(define
    (problem p01)
    (:domain industrial_manufacturing)

    (:objects
        central_warehouse loc1 - location
        ws1 ws2 ws3 - workstation
        b1 - box
        a1 - agent
        c1 - carrier
        valve1 bolt1 tool1 - content
        valve bolt tool - content-type
    )

    (:init
        (is-type valve1 valve)
        (is-type bolt1 bolt)
        (is-type tool1 tool)

        (at-box b1 central_warehouse)
        (empty b1)

        (at-content valve1 central_warehouse)
        (at-content bolt1 central_warehouse)
        (at-content tool1 central_warehouse)

        (at-ws ws1 loc1)
        (at-ws ws2 loc1)
        (at-ws ws3 loc1)

        (at-agent a1 central_warehouse)
        (carrier-at-agent a1 c1)

        (connected central_warehouse loc1)
        (connected loc1 central_warehouse)

        (= (capacity c1) 1)
        (= (amount c1) 0)
    )

    (:goal
        (and
            (content-at-workstation ws1 bolt)
            (content-at-workstation ws2 valve)
            (content-at-workstation ws3 tool)
        )
    )
)
