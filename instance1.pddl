(define
    (problem instance1)
    (:domain industrial_manufacturing)

    (:objects
        central_warehouse loc1 loc2 loc3 - location
        ws1 ws2 ws3 ws4 - workstation
        b1 b2 - box
        a1 - agent
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
        (at-content valve1 central_warehouse)
        (at-content valve2 central_warehouse)
        (at-content bolt1 central_warehouse)
        (at-content bolt2 central_warehouse)
        (at-content bolt3 central_warehouse)
        (at-content tool1 central_warehouse)
        (at-ws ws1 loc1)
        (at-ws ws2 loc2)
        (at-ws ws3 loc3)
        (at-ws ws4 loc3)
        (at-agent a1 central_warehouse)
        (connected central_warehouse loc1)
        (connected central_warehouse loc2)
        (connected central_warehouse loc3)
        (connected loc1 central_warehouse)
        (connected loc2 central_warehouse)
        (connected loc3 central_warehouse)
        (empty b1)
        (empty b2)
        (empty-hand a1)
    )

    (:goal
        (and
            (content-at-workstation ws1 bolt)
            (content-at-workstation ws2 valve)
            (content-at-workstation ws2 bolt)
            (content-at-workstation ws3 valve)
            (content-at-workstation ws3 bolt)
            (content-at-workstation ws3 tool)
        )
    ) 
)