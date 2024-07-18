(define (problem instance1) (:domain industrial_manufacturing)  
  (:objects  
    ; define the world 
    central_warehouse loc1 loc2 - location  
    ws1 ws2 ws3 ws4 ws5 - workstation  
    b1 b2 - box  
    a1 - agent  
    c1 - carrier 
    p1 - place 
    valve1 valve2 bolt1 bolt2 hammer1 hammer2 - content  
    valve bolt hammer - content-type  
  )  
  (:init  
    ; define the type of content 
    (is-type valve1 valve)  
    (is-type valve2 valve)  
    (is-type bolt1 bolt)  
    (is-type bolt2 bolt)  
    (is-type hammer1 hammer)  
    (is-type hammer2 hammer)  
 
    ; define the empty box located at central_warehouse 
    (box-at-loc b1 central_warehouse)  
    (box-at-loc b2 central_warehouse)  
    (empty-box b1)  
    (empty-box b2)  
             
    ; define content located at central_warehouse 
    (content-at-loc valve1 central_warehouse)  
    (content-at-loc valve2 central_warehouse)  
    (content-at-loc bolt1 central_warehouse)  
    (content-at-loc bolt2 central_warehouse)  
    (content-at-loc hammer1 central_warehouse)  
    (content-at-loc hammer2 central_warehouse)  
 
    ; define workstation located at central_warehouse 
    (ws-at-loc ws1 loc1)  
    (ws-at-loc ws2 loc1)  
    (ws-at-loc ws3 loc2)  
    (ws-at-loc ws4 loc2)  
    (ws-at-loc ws5 loc2) 
 
    ; define workstation located at central_warehouse 
    (connected central_warehouse loc1)  
    (connected central_warehouse loc2)  
    (connected loc1 central_warehouse)  
    (connected loc2 central_warehouse)  
 
    ; define agent located initially located at central_warehouse 
    (agent-at-loc a1 central_warehouse)  
    (carrier-at-agent c1 a1) 
    (place-at-carrier p1 c1) 
    (empty-place p1) 
  )  
  (:goal  
    ; define goals to satisfied  
    (and  
      (content-type-at-ws bolt ws1)  
      (content-type-at-ws valve ws1)  
      (content-type-at-ws bolt ws2)  
      (content-type-at-ws hammer ws3)  
      (content-type-at-ws valve ws3)  
      (content-type-at-ws hammer ws4)  
    )  
   )  
  ) 
 