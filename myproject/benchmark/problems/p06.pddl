(define
    (problem p06)
    (:domain industrial_manufacturing)

    (:objects
        central_warehouse loc2 - location
        ws1 - workstation
        b1 b2 b3 b4 b5 b6 b7 b8 - box
        a1 - agent
        c1 - carrier
        valve1 bolt1 tool1 nail1 screw1 screwdriver1 board1 hammer1 - content
        valve bolt tool nail screw screwdriver board hammer - content-type
    )

    (:init
        (is-type valve1 valve)
        (is-type bolt1 bolt)
        (is-type tool1 tool)
        (is-type nail1 nail)
        (is-type screw1 screw)
        (is-type screwdriver1 screwdriver)
        (is-type board1 board)
        (is-type hammer1 hammer)
        (at-box b1 central_warehouse)
        (at-box b2 central_warehouse)
        (at-box b3 central_warehouse)
        (at-box b4 central_warehouse)
        (at-box b5 central_warehouse)
        (at-box b6 central_warehouse)
        (at-box b7 central_warehouse)
        (at-box b8 central_warehouse)
        (at-content valve1 central_warehouse)
        (at-content bolt1 central_warehouse)
        (at-content tool1 central_warehouse)
        (at-content nail1 central_warehouse)
        (at-content screw1 central_warehouse)
        (at-content screwdriver1 central_warehouse)
        (at-content board1 central_warehouse)
        (at-content hammer1 central_warehouse)
        (at-ws ws1 loc2)
        (at-agent a1 central_warehouse)
        (connected central_warehouse loc2)
        (connected loc2 central_warehouse)
        (empty b1)
        (empty b2)
        (empty b3)
        (empty b4)
        (empty b5)
        (empty b6)
        (empty b7)
        (empty b8)
        (carrier-at-agent a1 c1)
        (= (capacity c1) 4)
        (= (amount c1) 0)
    )

    (:goal
        (and
            (content-at-workstation ws1 valve)
            (content-at-workstation ws1 bolt)
            (content-at-workstation ws1 tool)
            (content-at-workstation ws1 nail)
            (content-at-workstation ws1 screw)
            (content-at-workstation ws1 screwdriver)
            (content-at-workstation ws1 board)
            (content-at-workstation ws1 hammer)
        )
    )
)
