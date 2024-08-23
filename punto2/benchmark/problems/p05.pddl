(define
    (problem p04)
    (:domain industrial_manufacturing)

    (:objects
        central_warehouse loc1 loc2 loc3 loc4 - location
        ws1 ws2 ws3 ws4 ws5 ws6 ws7 ws8 ws9 ws10 - workstation
        b1 b2 b3 b4 b5 - box
        a1 a2 a3 - agent
        c1 c2 c3 - carrier
        valve1 valve2 valve3 valve4 valve5 valve6 valve7 bolt1 bolt2 bolt3 bolt4 bolt5 tool1 tool2 tool3 tool4 tool5 tool6 - content
        valve bolt tool - content-type
    )

    (:init
        (is-type valve1 valve)
        (is-type valve2 valve)
        (is-type valve3 valve)
        (is-type valve4 valve)
        (is-type valve5 valve)
        (is-type valve6 valve)
        (is-type valve7 valve)

        (is-type bolt1 bolt)
        (is-type bolt2 bolt)
        (is-type bolt3 bolt)
        (is-type bolt4 bolt)
        (is-type bolt5 bolt)

        (is-type tool1 tool)
        (is-type tool2 tool)
        (is-type tool3 tool)
        (is-type tool4 tool)
        (is-type tool5 tool)
        (is-type tool6 tool)

        (at-content valve1 central_warehouse)
        (at-content valve2 central_warehouse)
        (at-content valve3 central_warehouse)
        (at-content valve4 central_warehouse)
        (at-content valve5 central_warehouse)
        (at-content valve6 central_warehouse)
        (at-content valve7 central_warehouse)

        (at-content bolt1 central_warehouse)
        (at-content bolt2 central_warehouse)
        (at-content bolt3 central_warehouse)
        (at-content bolt4 central_warehouse)
        (at-content bolt5 central_warehouse)

        (at-content tool1 central_warehouse)
        (at-content tool2 central_warehouse)
        (at-content tool3 central_warehouse)
        (at-content tool4 central_warehouse)
        (at-content tool5 central_warehouse)
        (at-content tool6 central_warehouse)

        (at-box b1 central_warehouse)
        (at-box b2 central_warehouse)
        (at-box b3 central_warehouse)
        (at-box b4 central_warehouse)
        (at-box b5 central_warehouse)
        (empty b1)
        (empty b2)
        (empty b3)
        (empty b4)
        (empty b5)

        (at-ws ws1 loc1)
        (at-ws ws2 loc1)
        (at-ws ws3 loc1)
        (at-ws ws4 loc2)
        (at-ws ws5 loc2)
        (at-ws ws6 loc3)
        (at-ws ws7 loc4)
        (at-ws ws8 loc4)
        (at-ws ws9 loc4)
        (at-ws ws10 loc4)

        (connected central_warehouse loc1)
        (connected central_warehouse loc2)
        (connected central_warehouse loc3)
        (connected central_warehouse loc4)
        (connected loc1 central_warehouse)
        (connected loc2 central_warehouse)
        (connected loc3 central_warehouse)
        (connected loc4 central_warehouse)

        (at-agent a1 central_warehouse)
        (carrier-at-agent a1 c1)
        (at-agent a2 central_warehouse)
        (carrier-at-agent a2 c2)
        (at-agent a3 central_warehouse)
        (carrier-at-agent a3 c3)

        (= (capacity c1) 5)
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
            (content-at-workstation ws2 bolt)
            (content-at-workstation ws2 tool)

            (content-at-workstation ws3 valve)
            (content-at-workstation ws3 bolt)
            (content-at-workstation ws3 tool)

            (content-at-workstation ws4 valve)
            (content-at-workstation ws4 bolt)

            (content-at-workstation ws5 valve)
            (content-at-workstation ws5 bolt)

            (content-at-workstation ws6 tool)

            (content-at-workstation ws7 valve)

            (content-at-workstation ws8 tool)

            (content-at-workstation ws9 valve)

            (content-at-workstation ws10 tool)

        )
    )
)
