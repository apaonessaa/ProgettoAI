(define
    (problem p03)
    (:domain industrial_manufacturing)

    (:objects
        central_warehouse loc1 loc2 - location
        ws1 ws2 ws3 ws4 ws5 ws6 - workstation
        b1 b2 b3 - box
        a1 - agent
        c1 - carrier
        valve1 valve2 bolt1 bolt2 tool1 tool2 - content
        valve bolt tool - content-type
    )

    (:init
        (is-type valve1 valve)
        (is-type valve2 valve)
        (is-type bolt1 bolt)
        (is-type bolt2 bolt)
        (is-type tool1 tool)
        (is-type tool2 tool)

        (at-content valve1 central_warehouse)
        (at-content valve2 central_warehouse)
        (at-content bolt1 central_warehouse)
        (at-content bolt2 central_warehouse)
        (at-content tool1 central_warehouse)
        (at-content tool2 central_warehouse)

        (at-box b1 central_warehouse)
        (at-box b2 central_warehouse)
        (at-box b3 central_warehouse)
        (empty b1)
        (empty b2)
        (empty b3)

        (at-ws ws1 loc1)
        (at-ws ws2 loc1)
        (at-ws ws3 loc1)
        (at-ws ws4 loc2)
        (at-ws ws5 loc2)
        (at-ws ws6 loc2)

        (connected central_warehouse loc1)
        (connected central_warehouse loc2)
        (connected loc1 central_warehouse)
        (connected loc2 central_warehouse)

        (at-agent a1 central_warehouse)
        (carrier-at-agent a1 c1)

        (= (capacity c1) 2)
        (= (amount c1) 0)
    )

    (:goal
        (and
            (content-at-workstation ws1 valve)
            (content-at-workstation ws2 bolt)
            (content-at-workstation ws3 tool)
            (content-at-workstation ws4 valve)
            (content-at-workstation ws5 bolt)
            (content-at-workstation ws6 tool)
        )
    )
)
