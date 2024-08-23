(define (problem instance2) (:domain industrial_manufacturing)
  (:objects
    ; define the world
    cw loc1 loc2 - location
    ws1 ws2 ws3 ws4 ws5 - workstation
    b1 b2 b3 b4 - box
    a1 a2 - agent
    c1 c2 - carrier
    p11 p12 p21 p22 - place
    valve1 valve2 bolt1 bolt2 hammer1 hammer2 - content
    valve bolt hammer - type
  )
  (:init
    ; define the type of content
    (is_type valve1 valve)
    (is_type valve2 valve)
    (is_type bolt1 bolt)
    (is_type bolt2 bolt)
    (is_type hammer1 hammer)
    (is_type hammer2 hammer)

    ; define the empty box located at central_warehouse
    (box_at_loc b1 cw)
    (box_at_loc b2 cw)
    (box_at_loc b3 cw)
    (box_at_loc b4 cw)
    (empty_box b1)
    (empty_box b2)
    (empty_box b3)
    (empty_box b4)

    ; define content located at central_warehouse
    (content_at_loc valve1 cw)
    (content_at_loc valve2 cw)
    (content_at_loc bolt1 cw)
    (content_at_loc bolt2 cw)
    (content_at_loc hammer1 cw)
    (content_at_loc hammer2 cw)

    ; define workstation located at central_warehouse
    (ws_at_loc ws1 loc1)
    (ws_at_loc ws2 loc1)
    (ws_at_loc ws3 loc2)
    (ws_at_loc ws4 loc2)
    (ws_at_loc ws5 loc2)

    ; define workstation located at central_warehouse
    (connected cw loc1)
    (connected cw loc2)
    (connected loc1 cw)
    (connected loc2 cw)

    ; define agents located initially located at central_warehouse
    (agent_at_loc a1 cw)
    (carrier_at_agent c1 a1)
    (place_at_carrier p11 c1)
    (place_at_carrier p12 c1)
    (empty_place p11)
    (empty_place p12)
    (agent_at_loc a2 cw)
    (carrier_at_agent c2 a2)
    (place_at_carrier p21 c2)
    (place_at_carrier p22 c2)
    (empty_place p21)
    (empty_place p22)
  )
  (:goal
    ; define goals to satisfied
    (and
      (content_type_at_ws bolt ws1)
      (content_type_at_ws valve ws1)
      (content_type_at_ws bolt ws2)
      (content_type_at_ws hammer ws3)
      (content_type_at_ws valve ws3)
      (content_type_at_ws hammer ws4)
    )
   )
  )
