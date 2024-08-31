(define
    (problem p02)
    (:domain industrial_manufacturing)
    (:objects
        cw loc1 - location
        ws1 ws2 ws3 ws4 ws5 ws6 - workstation
        b1 b2 b3 b4 - box
        a1 - agent
        c1 - carrier
        p1 p2 p3 p4 - place
        valve1 valve2 valve3 valve4 valve5 valve6 - content
        valve - type
    )

    (:init
        (is_type valve1 valve)
        (is_type valve2 valve)
        (is_type valve3 valve)
        (is_type valve4 valve)
        (is_type valve5 valve)
        (is_type valve6 valve)

        (box_at_loc b1 cw)
        (box_at_loc b2 cw)
        (box_at_loc b3 cw)
        (box_at_loc b4 cw)
        (empty_box b1)
        (empty_box b2)
        (empty_box b3)
        (empty_box b4)

        (content_at_loc valve1 cw)
        (content_at_loc valve2 cw)
        (content_at_loc valve3 cw)
        (content_at_loc valve4 cw)
        (content_at_loc valve5 cw)
        (content_at_loc valve6 cw)

        (ws_at_loc ws1 loc1)
        (ws_at_loc ws2 loc1)
        (ws_at_loc ws3 loc1)
        (ws_at_loc ws4 loc1)
        (ws_at_loc ws5 loc1)
        (ws_at_loc ws6 loc1)

        (connected cw loc1)
        (connected loc1 cw)

        (agent_at_loc a1 cw)
        (carrier_at_agent c1 a1)
        (place_at_carrier p1 c1)
        (place_at_carrier p2 c1)
        (place_at_carrier p3 c1)
        (place_at_carrier p4 c1)
        (empty_place p1)
        (empty_place p2)
        (empty_place p3)
        (empty_place p4)
    )

    (:goal
        (and
            (content_type_at_ws valve ws1)
            (content_type_at_ws valve ws2)
            (content_type_at_ws valve ws3)
            (content_type_at_ws valve ws4)
            (content_type_at_ws valve ws5)
            (content_type_at_ws valve ws6)
        )
    )
)
