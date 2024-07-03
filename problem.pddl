(define (problem test)
    (:domain industrial_manufacturing)

    (:objects
        loc1 loc2 - location
        ws1 - workstation
        b1 - box
        a1 - agent
        valve bolt tool - content
    )
    
    (:init
        (at-agent a1 loc2)
        (at-box b1 loc1)
        (at-ws ws1 loc2)
        (at-content valve loc1)
        (connected loc1 loc2)
        (connected loc2 loc1)
        (workstation-needs ws1 valve)
        (empty-hand a1)
        (empty b1)
    )

    (:goal
        (content-at-workstation ws1 valve)
    )
)