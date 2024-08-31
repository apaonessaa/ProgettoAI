(define (problem p06)
    (:domain industrial_manufacturing)

    (:objects
        cw loc1 loc2 loc3 - location
        ws1 ws2 ws3 ws4 ws5 ws6 ws7 ws8 ws9 ws10 - workstation
        b1 b2 b3 b4 b5 b6 b7 b8 - box
        a1 a2 - agent
        c1 c2 - carrier
        p11 p12 p13 p14 p21 p22 p23 p24 - place
        valve1 valve2 valve3 valve4
        bolt1 bolt2 bolt3
        plier1 plier2 plier3 plier4 plier5
        wrench1 wrench2 wrench3
        hammer1 hammer2 hammer3 hammer4 hammer5
        screwdriver1 screwdriver2 screwdriver3 - content
        valve bolt plier wrench hammer screwdriver - type
    )

    (:init
        (is_type valve1 valve)
        (is_type valve2 valve)
        (is_type valve3 valve)
        (is_type valve4 valve)
        (is_type bolt1 bolt)
        (is_type bolt2 bolt)
        (is_type bolt3 bolt)
        (is_type plier1 plier)
        (is_type plier2 plier)
        (is_type plier3 plier)
        (is_type plier4 plier)
        (is_type plier5 plier)
        (is_type wrench1 wrench)
        (is_type wrench2 wrench)
        (is_type wrench3 wrench)
        (is_type hammer1 hammer)
        (is_type hammer2 hammer)
        (is_type hammer3 hammer)
        (is_type hammer4 hammer)
        (is_type hammer5 hammer)
        (is_type screwdriver1 screwdriver)
        (is_type screwdriver2 screwdriver)
        (is_type screwdriver3 screwdriver)

        (content_at_loc valve1 cw)
        (content_at_loc valve2 cw)
        (content_at_loc valve3 cw)
        (content_at_loc valve4 cw)
        (content_at_loc bolt1 cw)
        (content_at_loc bolt2 cw)
        (content_at_loc bolt3 cw)
        (content_at_loc plier1 cw)
        (content_at_loc plier2 cw)
        (content_at_loc plier3 cw)
        (content_at_loc plier4 cw)
        (content_at_loc plier5 cw)
        (content_at_loc wrench1 cw)
        (content_at_loc wrench2 cw)
        (content_at_loc wrench3 cw)
        (content_at_loc hammer1 cw)
        (content_at_loc hammer2 cw)
        (content_at_loc hammer3 cw)
        (content_at_loc hammer4 cw)
        (content_at_loc hammer5 cw)
        (content_at_loc screwdriver1 cw)
        (content_at_loc screwdriver2 cw)
        (content_at_loc screwdriver3 cw)

        (box_at_loc b1 cw)
        (box_at_loc b2 cw)
        (box_at_loc b3 cw)
        (box_at_loc b4 cw)
        (box_at_loc b5 cw)
        (box_at_loc b6 cw)
        (box_at_loc b7 cw)
        (box_at_loc b8 cw)
        (empty_box b1)
        (empty_box b2)
        (empty_box b3)
        (empty_box b4)
        (empty_box b5)
        (empty_box b6)
        (empty_box b7)
        (empty_box b8)

        (ws_at_loc ws1 loc1)
        (ws_at_loc ws2 loc1)
        (ws_at_loc ws3 loc1)
        (ws_at_loc ws4 loc1)
        (ws_at_loc ws5 loc2)
        (ws_at_loc ws6 loc2)
        (ws_at_loc ws7 loc2)
        (ws_at_loc ws8 loc3)
        (ws_at_loc ws9 loc3)
        (ws_at_loc ws10 loc3)

        (connected cw loc1)
        (connected cw loc2)
        (connected cw loc3)
        (connected loc1 cw)
        (connected loc2 cw)
        (connected loc3 cw)

        (agent_at_loc a1 cw)
        (carrier_at_agent c1 a1)
        (place_at_carrier p11 c1)
        (place_at_carrier p12 c1)
        (place_at_carrier p13 c1)
        (place_at_carrier p14 c1)
        (empty_place p11)
        (empty_place p12)
        (empty_place p13)
        (empty_place p14)
        (agent_at_loc a2 cw)
        (carrier_at_agent c2 a2)
        (place_at_carrier p21 c2)
        (place_at_carrier p22 c2)
        (place_at_carrier p23 c2)
        (place_at_carrier p24 c2)
        (empty_place p21)
        (empty_place p22)
        (empty_place p23)
        (empty_place p24)
    )

    (:goal
        (and
            (content_type_at_ws plier ws1)
            (content_type_at_ws hammer ws1)

            (content_type_at_ws plier ws2)
            (content_type_at_ws hammer ws2)

            (content_type_at_ws screwdriver ws3)
            (content_type_at_ws plier ws3)

            (content_type_at_ws hammer ws4)
            (content_type_at_ws wrench ws4)

            (content_type_at_ws valve ws5)
            (content_type_at_ws bolt ws5)

            (content_type_at_ws valve ws6)
            (content_type_at_ws wrench ws6)

            (content_type_at_ws valve ws7)
            (content_type_at_ws plier ws7)

            (content_type_at_ws bolt ws8)
            (content_type_at_ws hammer ws8)
            (content_type_at_ws screwdriver ws8)

            (content_type_at_ws wrench ws9)
            (content_type_at_ws hammer ws9)

            (content_type_at_ws valve ws10)
            (content_type_at_ws bolt ws10)
            (content_type_at_ws plier ws10)
            (content_type_at_ws screwdriver ws10)
        )
    )
)
