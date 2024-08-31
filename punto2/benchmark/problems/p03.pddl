(define
    (problem p02)
    (:domain industrial_manufacturing)
    (:objects
        cw loc1 - location
        ws1 ws2 ws3 ws4 ws5 ws6 - workstation
        b1 b2 b3 b4 b5 b6 - box
        a1 a2 - agent
        c1 c2 - carrier
        p11 p12 p21 p22 - place
        valve1 valve2 valve3 valve4 valve5 valve6
        bolt1 bolt2 bolt3
        plier1 plier2 plier3 plier4 plier5 - content
        valve bolt plier - type
    )

    (:init
        (is_type valve1 valve)
        (is_type valve2 valve)
        (is_type valve3 valve)
        (is_type valve4 valve)
        (is_type valve5 valve)
        (is_type valve6 valve)
        (is_type bolt1 bolt)
        (is_type bolt2 bolt)
        (is_type bolt3 bolt)
        (is_type plier1 plier)
        (is_type plier2 plier)
        (is_type plier3 plier)
        (is_type plier4 plier)
        (is_type plier5 plier)

        (box_at_loc b1 cw)
        (box_at_loc b2 cw)
        (box_at_loc b3 cw)
        (box_at_loc b4 cw)
        (box_at_loc b5 cw)
        (box_at_loc b6 cw)
        (empty_box b1)
        (empty_box b2)
        (empty_box b3)
        (empty_box b4)
        (empty_box b5)
        (empty_box b6)

        (content_at_loc valve1 cw)
        (content_at_loc valve2 cw)
        (content_at_loc valve3 cw)
        (content_at_loc valve4 cw)
        (content_at_loc valve5 cw)
        (content_at_loc valve6 cw)
        (content_at_loc bolt1 cw)
        (content_at_loc bolt2 cw)
        (content_at_loc bolt3 cw)
        (content_at_loc plier1 cw)
        (content_at_loc plier2 cw)
        (content_at_loc plier3 cw)
        (content_at_loc plier4 cw)
        (content_at_loc plier5 cw)

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
        (place_at_carrier p11 c1)
        (place_at_carrier p21 c1)
        (empty_place p11)
        (empty_place p21)
        (agent_at_loc a2 cw)
        (carrier_at_agent c2 a2)
        (place_at_carrier p21 c2)
        (place_at_carrier p22 c2)
        (empty_place p21)
        (empty_place p22)
    )

    (:goal
        (and
            (content_type_at_ws valve ws1)
            (content_type_at_ws bolt ws1)
            (content_type_at_ws plier ws1)

            (content_type_at_ws valve ws2)
            (content_type_at_ws bolt ws2)
            (content_type_at_ws plier ws2)

            (content_type_at_ws valve ws3)
            (content_type_at_ws plier ws3)

            (content_type_at_ws valve ws4)
            (content_type_at_ws bolt ws4)
            (content_type_at_ws plier ws4)

            (content_type_at_ws valve ws5)
            (content_type_at_ws plier ws5)

            (content_type_at_ws valve ws6)
        )
    )
)
