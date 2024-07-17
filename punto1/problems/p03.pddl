(define (problem p03)
    (:domain industrial_manufacturing)
    (:objects
        central_warehouse loc1 loc2 - location
        ws1 ws2 ws3 ws4 - workstation
        b1 b2 b3 - box
        a1 a2 - agent
        c1 c2 - carrier
        p11 p12 p21 - place
        valve1 valve2 bolt1 bolt2 bolt3 bolt4 tool1 tool2 hammer1 hammer2 - content
        valve bolt tool hammer - content-type
    )
    (:init
        (connected central_warehouse loc1)
        (connected central_warehouse loc2)
        (connected loc1 central_warehouse)
        (connected loc2 central_warehouse)

        (box-at-loc b1 central_warehouse)
        (box-at-loc b2 central_warehouse)
        (box-at-loc b3 central_warehouse)
        (empty-box b1)
        (empty-box b2)
        (empty-box b3)
        
        (is-type valve1 valve)
        (is-type valve2 valve)
        (is-type bolt1 bolt)
        (is-type bolt2 bolt)
        (is-type bolt3 bolt)
        (is-type bolt4 bolt)
        (is-type tool1 tool)
        (is-type tool2 tool)
        (is-type hammer1 hammer)
        (is-type hammer2 hammer)

        (content-at-loc valve1 central_warehouse)
        (content-at-loc valve2 central_warehouse)
        (content-at-loc bolt1 central_warehouse)
        (content-at-loc bolt2 central_warehouse)
        (content-at-loc bolt3 central_warehouse)
        (content-at-loc bolt4 central_warehouse)
        (content-at-loc tool1 central_warehouse)
        (content-at-loc tool2 central_warehouse)
        (content-at-loc hammer1 central_warehouse)
        (content-at-loc hammer2 central_warehouse)

        (ws-at-loc ws1 loc1)
        (ws-at-loc ws2 loc1)
        (ws-at-loc ws3 loc2)
        (ws-at-loc ws4 loc2)

        (agent-at-loc a1 central_warehouse)
        (carrier-at-agent c1 a1)
        (place-at-carrier p11 c1)
        (place-at-carrier p12 c1)
        (agent-at-loc a2 central_warehouse)
        (carrier-at-agent c2 a2)
        (place-at-carrier p21 c2)

        (empty-place p11)
        (empty-place p12)
        (empty-place p21)
    )
    (:goal
        (and
            (content-type-at-ws bolt ws1)
            (content-type-at-ws tool ws1)
            (content-type-at-ws hammer ws1)

            (content-type-at-ws valve ws2)
            (content-type-at-ws hammer ws2)
            (content-type-at-ws bolt ws2)
            (content-type-at-ws tool ws2)

            (content-type-at-ws valve ws3)
            (content-type-at-ws bolt ws3)

            (content-type-at-ws bolt ws4)
        )
    )
)
