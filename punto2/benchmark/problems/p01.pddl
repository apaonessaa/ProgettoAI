(define
    (problem p01)
    (:domain industrial_manufacturing)

    (:objects
        cw loc1 - location
        ws1 - workstation
        b1 - box
        a1 - agent
        c1 - carrier
        p1 - place
        valve1 bolt1 hammer1 - content
        valve bolt hammer - type
    )

    (:init
        (is_type valve1 valve)
        (is_type bolt1 bolt)
        (is_type hammer1 hammer)

        (box_at_loc b1 cw)
        (empty_box b1)

        (content_at_loc valve1 cw)
        (content_at_loc bolt1 cw)
        (content_at_loc hammer1 cw)

        (ws_at_loc ws1 loc1)

        (connected cw loc1)
        (connected loc1 cw)

        (agent_at_loc a1 cw)
        (carrier_at_agent c1 a1)
        (place_at_carrier p1 c1)
        (empty_place p1)
    )

    (:goal
        (and
            (content_type_at_ws bolt ws1)
            (content_type_at_ws valve ws1)
            (content_type_at_ws hammer ws1)
        )
    )
)
