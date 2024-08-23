(define
    (problem p02)
    (:domain industrial_manufacturing)

    (:objects
        central_warehouse loc1 - location
        ws1 ws2 ws3 - workstation
        b1 - box
        a1 - agent
        c1 - carrier
        valve1 valve2 valve3 bolt1 tool1 - content
        valve bolt tool - content-type
    )

    (:init
        (is-type valve1 valve)
        (is-type valve2 valve)
        (is-type valve3 valve)
        (is-type bolt1 bolt)
        (is-type tool1 tool)

        (at-content valve1 central_warehouse)
        (at-content valve2 central_warehouse)
        (at-content valve3 central_warehouse)
        (at-content bolt1 central_warehouse)
        (at-content tool1 central_warehouse)

        (at-box b1 central_warehouse)
        (empty b1)

        (at-ws ws1 loc1)
        (at-ws ws2 loc1)
        (at-ws ws3 loc1)

        (connected central_warehouse loc1)
        (connected loc1 central_warehouse)

        (at-agent a1 central_warehouse)
        (carrier-at-agent a1 c1)

        (= (capacity c1) 1)
        (= (amount c1) 0)
    )

    (:goal
        (and
            (content-at-workstation ws1 valve)
            (content-at-workstation ws1 bolt)
            (content-at-workstation ws2 valve)
            (content-at-workstation ws3 valve)
            (content-at-workstation ws3 tool)
        )
    )
)
