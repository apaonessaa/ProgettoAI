(define (domain industrial_manufacturing2)
    (:requirements :strips :typing :numeric-fluents)
    
    (:types
        location box content agent workstation content-type carrier - object
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
        (content-at-workstation ?ws - workstation ?t - content-type)
        ;(workstation-needs ?ws - workstation ?content - content)
        (box-at-carrier ?carrier - carrier ?box - box)
        (carrier-at-agent ?agent - agent ?carrier - carrier)
        (is-type ?content - content ?t - content-type)
    )

    (:functions
        (capacity ?carrier - carrier)- number
        (amount ?carrier - carrier) - number
    )

    ;Si procede a definire la action relativa al ritiro di una box da una location.
    ;I parametri utili alla action in oggetto sono: la location, la scatola da ritirare e l'agente robotico.
    (:action pick-up
        :parameters (?loc - location ?box - box ?agent - agent ?carrier - carrier)
        ;Le precondizioni che consentono l'esecuzione prevedono che:
        ;-l'agente robotico non sia occupato
        ;-l'agente robotico si trovi nella location inserita come parametro
        ;-la box si trovi nella location inserita come parametro
        :precondition (and
            (carrier-at-agent ?agent ?carrier)
            (< (amount ?carrier) (capacity ?carrier))
            (at-agent ?agent ?loc)
            (at-box ?box ?loc)
        )
        ;Gli effetti dell'esecuzione portano ad uno stato in cui:
        ;-l'agente robotico non è più libero e porta la box inserita come parametro
        ;-la box non risulta più alla location
        :effect (and
            (increase (amount ?carrier) 1)
            (box-at-carrier ?carrier ?box)
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
            (not (at-agent ?agent ?from))
            (at-agent ?agent ?to)
        )
    )

    ;Si procede a definire la action relativa alla consegna di una box ad una workstation.
    ;I parametri utili alla action in oggetto sono: la box da consegnare, il contenuto della box, la workstation
    ;a cui consegnare la box, la location in cui si trova la workstation e l'agente robotico coinvolto.
    (:action deliver
        :parameters (?box - box ?content - content ?t - content-type ?ws - workstation ?location - location ?agent - agent ?carrier - carrier)
        ;Le precondizioni che consentono l'esecuzione prevedono che:
        ;-l'agente robotico si trovi nella stessa location della workstation a cui consegnare la box
        ;-l'agente robotico stia portando la box da consegnare
        ;-la box abbia un contenuto necessario alla workstation
        :precondition (and
            (carrier-at-agent ?agent ?carrier)
            (is-type ?content ?t)
            (at-agent ?agent ?location)
            (box-at-carrier ?carrier ?box)
            (at-ws ?ws ?location)
            (filled ?box ?content)
            ;(workstation-needs ?ws ?content)
        )
        ;Gli effetti dell'esecuzione portano ad uno stato in cui:
        ;-l'agente robotico non porta più la box
        ;-la workstation non ha più bisogno del contenuto della box
        ;-la box si trova nella workstation a cui è stata consegnata
        :effect (and 
            (not (box-at-carrier ?carrier ?box))
            (decrease (amount ?carrier) 1)
            (box-at-workstation ?ws ?box)
            ;(not (workstation-needs ?ws ?content))
        )
    )

    ;Si procede a definire la action relativa al riempimento di una box con un contenuto.
    ;I parametri utili alla action in oggetto sono: la box da riempire, il contenuto della box, la location
    ;in cui si trova il contenuto e l'agente robotico coinvolto.
    (:action fill
        :parameters (?box - box ?content - content ?t - content-type ?loc - location ?agent - agent ?carrier - carrier)
        ;Le precondizioni che consentono l'esecuzione prevedono che:
        ;-la box sia vuota
        ;-l'agente robotico stia portando la box da riempire
        ;-il contenuto e l'agente robotico si trovino nella stessa location
        :precondition (and
            (carrier-at-agent ?agent ?carrier)
            (is-type ?content ?t)
            (empty ?box)
            (box-at-carrier ?carrier ?box)
            (at-content ?content ?loc)
            (at-agent ?agent ?loc)
        )
        ;Gli effetti dell'esecuzione portano ad uno stato in cui:
        ;-la box non è più vuota
        ;-la box è riempita con il contenuto
        ;-il contenuto non si trova più nella location
        :effect (and
            (not (empty ?box))
            (filled ?box ?content)
            (not (at-content ?content ?loc))
        )
    )

    ;Si procede a definire la action relativa allo svuotamento di una box.
    ;I parametri utili alla action in oggetto sono: la box da svuotare, il contenuto della box,
    ;la workstation che riceve il contenuto, l'agente robotico coinvolto e la location.
    (:action empty 
        :parameters (?box - box ?content - content ?t - content-type ?ws - workstation ?agent - agent ?loc - location)
        ;Le precondizioni che consentono l'esecuzione prevedono che:
        ;-l'agente non sia impegnato
        ;-la box sia riempita con il contenuto da consegnare
        ;-la box si trovi nella workstation che deve ricevere il contenuto
        ;-l'agente e la workstation si trovino nella stessa locazione
        :precondition (and
            (is-type ?content ?t)
            (filled ?box ?content)
            (box-at-workstation ?ws ?box)
            (at-agent ?agent ?loc)
            (at-ws ?ws ?loc)
        )
        ;Gli effetti dell'esecuzione portano ad uno stato in cui:
        ;-la box non ha più contenuto
        ;-la box viene rilasciata nella locazione
        ;-la workstation ha il contenuto della box
        :effect (and 
            (not (filled ?box ?content))
            (empty ?box)
            (at-box ?box ?loc)
            (content-at-workstation ?ws ?t)
        )
    )

  

)