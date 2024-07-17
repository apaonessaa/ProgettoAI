(define
    (problem p04)
    (:domain industrial_manufacturing)

    (:objects
        central_warehouse loc1 loc2 loc3 - location
        ws1 ws2 ws3 ws4 ws5 - workstation
        b1 b2 b3 b4 b5 b6 b7 - box
        a1 a2 - agent
        c1 c2 - carrier
        p11 p12 p21 p22 - place
        valve1 valve2
        bolt1 bolt2 bolt3
        hammer1 hammer2 hammer3
        screwdriver1 screwdriver2 screwdriver3 - content
        valve bolt hammer screwdriver - content-type
    )

    (:init
        (is-type valve1 valve)
        (is-type valve2 valve)
        (is-type bolt1 bolt)
        (is-type bolt2 bolt)
        (is-type bolt3 bolt)
        (is-type hammer1 hammer)
        (is-type hammer2 hammer)
        (is-type hammer3 hammer)
        (is-type screwdriver1 screwdriver)
        (is-type screwdriver2 screwdriver)
        (is-type screwdriver3 screwdriver)

        (content-at-loc valve1 central_warehouse)
        (content-at-loc valve2 central_warehouse)
        (content-at-loc bolt1 central_warehouse)
        (content-at-loc bolt2 central_warehouse)
        (content-at-loc bolt3 central_warehouse)
        (content-at-loc hammer1 central_warehouse)
        (content-at-loc hammer2 central_warehouse)
        (content-at-loc hammer3 central_warehouse)
        (content-at-loc screwdriver1 central_warehouse)
        (content-at-loc screwdriver2 central_warehouse)
        (content-at-loc screwdriver3 central_warehouse)

        (box-at-loc b1 central_warehouse)
        (box-at-loc b2 central_warehouse)
        (box-at-loc b3 central_warehouse)
        (box-at-loc b4 central_warehouse)
        (box-at-loc b5 central_warehouse)
        (box-at-loc b6 central_warehouse)
        (box-at-loc b7 central_warehouse)
        (empty-box b1)
        (empty-box b2)
        (empty-box b3)
        (empty-box b4)
        (empty-box b5)
        (empty-box b6)
        (empty-box b7)

        (ws-at-loc ws1 loc1)
        (ws-at-loc ws2 loc1)
        (ws-at-loc ws3 loc2)
        (ws-at-loc ws4 loc3)
        (ws-at-loc ws5 loc3)

        (connected central_warehouse loc1)
        (connected central_warehouse loc2)
        (connected central_warehouse loc3)
        (connected loc1 central_warehouse)
        (connected loc2 central_warehouse)
        (connected loc3 central_warehouse)

        (agent-at-loc a1 central_warehouse)
        (carrier-at-agent c1 a1)
        (place-at-carrier p11 c1)
        (place-at-carrier p12 c1)
        (empty-place p11)
        (empty-place p12)
        (agent-at-loc a2 central_warehouse)
        (carrier-at-agent c2 a2)
        (place-at-carrier p21 c2)
        (place-at-carrier p22 c2)
        (empty-place p21)
        (empty-place p22)
    )

    (:goal
        (and
            (content-type-at-ws hammer ws1)
            (content-type-at-ws screwdriver ws1)
            (content-type-at-ws valve ws1)
            (content-type-at-ws bolt ws1)
            (content-type-at-ws hammer ws2)
            (content-type-at-ws bolt ws2)
            (content-type-at-ws screwdriver ws2)
            (content-type-at-ws hammer ws3)
            (content-type-at-ws screwdriver ws3)
            (content-type-at-ws valve ws3)
            (content-type-at-ws bolt ws3)
        )
    )
)
