(define (domain industrial_manufacturing)
    (:requirements :strips :typing)
    
    (:types
        location box content agent workstation
    )

    (:predicates
        (at-box ?box - box ?loc - location)
        (at-agent ?agent - agent ?loc - location)
        (at-ws ?ws - workstation ?loc - location)
        (at-content ?content - content ?loc - location)
        (connected ?loc1 ?loc2 - location)
        (empty ?box - box)
        (filled ?box - box ?content - content)
        (box-at-workstation ?ws - workstation ?box - box)
        (workstation-needs ?ws - workstation ?content - content)
        (box-at-agent ?agent - agent ?box - box)
        (empty-hand ?agent - agent)
    )
    ;Il box può essere collegato ad una location, ad un agente o ad una workstation (a sua volta collegati ad una location)

    ;Si procede a definire la action relativa al ritiro di una box da una location.
    ;I parametri utili alla action in oggetto sono: la location, la scatola da ritirare e l'agente robotico.
    (:action pick-up
        :parameters (?loc - location ?box - box ?agent - agent)
        ;Le precondizioni che consentono l'esecuzione prevedono che:
        ;-l'agente robotico non sia occupato
        ;-l'agente robotico si trovi nella location inserita come parametro
        ;-la box si trovi nella location inserita come parametro
        :precondition (and
            (empty-hand ?agent)
            (at-agent ?agent ?loc)
            (at-box ?box ?loc)
        )
        ;Gli effetti dell'esecuzione portano ad uno stato in cui:
        ;-l'agente robotico non è più libero e porta la box inserita come parametro
        ;-la box non risulta più alla location
        :effect (and
            (not (empty-hand ?agent))
            (box-at-agent ?agent ?box)
            (not (at-box ?box ?loc))
        )
    )

    ;Si procede a definire la action relativa allo spostamento di un agente da una location all'altra.
    ;I parametri utili alla action in oggetto sono: le location (from e to) e l'agente robotico coinvolto.
    (:action move
        :parameters (?from ?to - location ?agent - agent)
        ;Le precondizioni che consentono l'esecuzione prevedono che:
        ;-l'agente robotico si trovi nella location di partenza
        ;-le locations siano connesse
        :precondition (and
            (at-agent ?agent ?from)
            (connected ?from ?to)
        )
        ;Gli effetti dell'esecuzione portano ad uno stato in cui:
        ;-l'agente robotico non si trovi nella location di partenza
        ;-l'agente robotico si trovi nella location di arrivo
        :effect (and
            (not (at-agent ?from))
            (at-agent ?to)
        )
    )

    (:action fill
        :parameters (?box - box ?content - content ?loc - location ?agent - agent)
        :precondition (and 
            (not (empty-hand ?agent))
            
            (at-box ?box ?loc)
            (at-agent ?agent ?loc)
        )
    
    )

    (:action deliver
        :parameters (?box - box ?content - content ?ws - workstation ?location - location ?agent - agent)
        :precondition (and 
            (at-agent ?agent ?location)
            (box-at-agent ?agent ?box)
            (at-ws ?ws ?location)
            (filled ?box ?content)
            (workstation-needs ?ws ?content)
        )
        :effect (and 
            (not (box-at-agent ?agent ?box))
            (not (workstation-needs ?ws ?content))
            (box-at-workstation ?ws ?box)
            (at-box ?box ?location)
        )
    )


    (:action empty 
        :parameters (?box - box ?content - content ?ws - workstation ?agent - agent ?loc - location)
        :precondition (and 
            (filled ?box ?content)
            (box-at-agent ?box ?agent)
            (at-agent ?agent ?location)
            (at-ws ?ws ?loc)
        )
        :effect (and 
            (at-work)
        )
    
    
    )

  

)