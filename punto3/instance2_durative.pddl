(define
    (problem instance2)
    (:domain industrial_manufacturing_durative)

    (:objects
        central_warehouse loc1 loc2 loc3 - location
        ws1 ws2 ws3 ws4 - workstation
        b1 b2 b3 b4 b5 - box
        a1 a2 - agent
        c1 c2 - carrier
        p11 p12 p13 p21 p22 p23 - place
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
        (box-at-loc b1 central_warehouse)
        (box-at-loc b2 central_warehouse)
        (box-at-loc b3 central_warehouse)
        (box-at-loc b4 central_warehouse)
        (box-at-loc b5 central_warehouse)
        (content-at-loc valve1 central_warehouse)
        (content-at-loc valve2 central_warehouse)
        (content-at-loc bolt1 central_warehouse)
        (content-at-loc bolt2 central_warehouse)
        (content-at-loc bolt3 central_warehouse)
        (content-at-loc tool1 central_warehouse)
        (ws-at-loc ws1 loc1)
        (ws-at-loc ws2 loc2)
        (ws-at-loc ws3 loc3)
        (ws-at-loc ws4 loc3)
        (agent-at-loc a1 central_warehouse)
        (agent-at-loc a2 central_warehouse)
        (free-agent a1)
        (free-agent a2)
        (connected central_warehouse loc1)
        (connected central_warehouse loc2)
        (connected central_warehouse loc3)
        (connected loc1 central_warehouse)
        (connected loc2 central_warehouse)
        (connected loc3 central_warehouse)
        (empty-box b1)
        (empty-box b2)
        (carrier-at-agent c1 a1)
        (carrier-at-agent c2 a2)
        (place-at-carrier p11 c1)
        (place-at-carrier p12 c1)
        (place-at-carrier p13 c1)
        (place-at-carrier p21 c2)
        (place-at-carrier p22 c2)
        (place-at-carrier p23 c2)
        (empty-place p11)
        (empty-place p12)
        (empty-place p13)
        (empty-place p21)
        (empty-place p22)
        (empty-place p23)
        (= (pick-duration) 2)
        (= (fill-duration) 3)
        (= (move-duration) 5)
        (= (deliver-duration) 2)
        (= (empty-duration) 3)
    )

    (:goal
        (and
            (content-type-at-ws bolt ws1)
            (content-type-at-ws valve ws2)
            (content-type-at-ws bolt ws2)
            (content-type-at-ws valve ws3)
            (content-type-at-ws bolt ws3)
            (content-type-at-ws tool ws4)
        )
    )
)
