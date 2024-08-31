(define (problem instance1) (:domain industrial_manufacturing)
  (:objects
    ; define the world
    cw loc1 loc2 - location
    ws1 ws2 ws3 ws4 ws5 - workstation
    b1 b2 - box
    a1 - agent
    c1 - carrier
    p1 - place
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
    (empty_box b1)
    (empty_box b2)

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

    ; define agent located initially located at central_warehouse
    (agent_at_loc a1 cw)
    (carrier_at_agent c1 a1)
    (place_at_carrier p1 c1)
    (empty_place p1)
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
