(define
    (problem instance22)
    (:domain industrial_manufacturing)

    (:objects
        central_warehouse loc1 loc2 loc3 - location
        ws1 ws2 ws3 ws4 - workstation
        b1 b2 b3 b4 - box
        a1 a2 a3 a4 - agent
        c1 c2 c3 c4 - carrier
        valve1 valve2 bolt1 bolt2 bolt3 tool1 - content
        valve bolt tool - content-type
    )

    (:init
        (is-type valve1 valve)
        (is-type valve2 valve)
        (is-type bolt1 bolt)
        (is-type bolt2 bolt)
        (is-type bolt3 bolt)
        (is-type tool1 tool)

        (at-box b1 central_warehouse)
        (at-box b2 central_warehouse)
        (at-box b3 central_warehouse)
        (at-box b4 central_warehouse)

        (at-content valve1 central_warehouse)
        (at-content valve2 central_warehouse)
        (at-content bolt1 central_warehouse)
        (at-content bolt2 central_warehouse)
        (at-content bolt3 central_warehouse)
        (at-content tool1 central_warehouse)

        (at-ws ws1 loc1)
        (at-ws ws2 loc1)
        (at-ws ws3 loc1)
        (at-ws ws4 loc1)

        (at-agent a1 central_warehouse)
        (at-agent a2 central_warehouse)
        (at-agent a3 central_warehouse)
        (at-agent a4 central_warehouse)

        (connected central_warehouse loc1)
        (connected central_warehouse loc2)
        (connected central_warehouse loc3)
        (connected loc1 central_warehouse)
        (connected loc2 central_warehouse)
        (connected loc3 central_warehouse)

        (empty b1)
        (empty b2)
        (empty b3)
        (empty b4)

        (carrier-at-agent a1 c1)
        (carrier-at-agent a2 c2)
        (carrier-at-agent a3 c3)
        (carrier-at-agent a4 c4)

        (= (capacity c1) 3)
        (= (capacity c2) 2)
        (= (capacity c3) 1)
        (= (capacity c4) 1)

        (= (amount c1) 0)
        (= (amount c2) 0)
        (= (amount c3) 0)
        (= (amount c4) 0)
    )

    (:goal
        (and
            (content-at-workstation ws1 bolt)

            (content-at-workstation ws2 valve)
            (content-at-workstation ws2 bolt)

            (content-at-workstation ws3 valve)
            (content-at-workstation ws3 bolt)

            (content-at-workstation ws4 tool)
        )
    )
)
