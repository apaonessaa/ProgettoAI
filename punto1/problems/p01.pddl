(define (problem p01)
    (:domain industrial_manufacturing)
    (:objects
        central_warehouse loc1 - location
        ws1 ws2 ws3 - workstation
        b1 - box
        a1 - agent
        c1 - carrier
        p1 - place
        valve1 valve2 bolt1 tool1 - content
        valve bolt tool - content-type
    )
    (:init
        (connected central_warehouse loc1)
        (connected loc1 central_warehouse)
        
        (is-type valve1 valve)
        (is-type valve2 valve)
        (is-type bolt1 bolt)
        (is-type tool1 tool)

        (box-at-loc b1 central_warehouse)
        (empty-box b1)

        (content-at-loc valve1 central_warehouse)
        (content-at-loc valve2 central_warehouse)
        (content-at-loc bolt1 central_warehouse)
        (content-at-loc tool1 central_warehouse)

        (ws-at-loc ws1 loc1)
        (ws-at-loc ws2 loc1)
        (ws-at-loc ws3 loc1)

        (agent-at-loc a1 central_warehouse)
        (carrier-at-agent c1 a1)
        (place-at-carrier p1 c1)
        (empty-place p1)
    )
    (:goal
        (and
            (content-type-at-ws bolt ws1)
            (content-type-at-ws valve ws1)
            (content-type-at-ws valve ws2)
            (content-type-at-ws tool ws3)
        )
    )
)
