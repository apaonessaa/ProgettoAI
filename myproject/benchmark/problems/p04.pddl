(define
    (problem p04)
    (:domain industrial_manufacturing)

    (:objects
        central_warehouse loc1 loc2 - location
        ws1 ws2 ws3 ws4 ws5 - workstation
        b1 b2 b3 - box
        a1 a2 a3 - agent
        c1 c2 c3 - carrier
        valve1 valve2 valve3 bolt1 bolt2 bolt3 bolt4 tool1 tool2 tool3 - content
        valve bolt tool - content-type
    )

    (:init
        (is-type valve1 valve)
        (is-type valve2 valve)
        (is-type valve3 valve)

        (is-type bolt1 bolt)
        (is-type bolt2 bolt)
        (is-type bolt3 bolt)
        (is-type bolt4 bolt)

        (is-type tool1 tool)
        (is-type tool2 tool)
        (is-type tool3 tool)

        (at-content valve1 central_warehouse)
        (at-content valve2 central_warehouse)
        (at-content valve3 central_warehouse)

        (at-content bolt1 central_warehouse)
        (at-content bolt2 central_warehouse)
        (at-content bolt3 central_warehouse)
        (at-content bolt4 central_warehouse)

        (at-content tool1 central_warehouse)
        (at-content tool2 central_warehouse)
        (at-content tool3 central_warehouse)


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

        (connected central_warehouse loc1)
        (connected central_warehouse loc2)
        (connected loc1 central_warehouse)
        (connected loc2 central_warehouse)

        (at-agent a1 central_warehouse)
        (carrier-at-agent a1 c1)
        (at-agent a2 central_warehouse)
        (carrier-at-agent a2 c2)
        (at-agent a3 central_warehouse)
        (carrier-at-agent a3 c3)

        (= (capacity c1) 3)
        (= (amount c1) 0)
        (= (capacity c2) 3)
        (= (amount c2) 0)
        (= (capacity c3) 3)
        (= (amount c3) 0)
    )

    (:goal
        (and
            (content-at-workstation ws1 valve)
            (content-at-workstation ws1 bolt)
            (content-at-workstation ws1 tool)

            (content-at-workstation ws2 valve)
            (content-at-workstation ws3 bolt)
            (content-at-workstation ws4 tool)

            (content-at-workstation ws5 valve)
            (content-at-workstation ws5 bolt)
            (content-at-workstation ws5 bolt)
            (content-at-workstation ws5 tool)
            (content-at-workstation ws5 tool)
        )
    )
)
