(define (domain industrial_manufacturing_durative)
    (:requirements :strips :typing :numeric-fluents :durative-actions)

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
        (box-at-carrier ?carrier - carrier ?box - box)
        (carrier-at-agent ?agent - agent ?carrier - carrier)
        (is-type ?content - content ?t - content-type)
        (free-agent ?agent - agent)
    )

    (:functions
        (capacity ?carrier - carrier) - number
        (amount ?carrier - carrier) - number
        (pick-duration)
        (fill-duration)
        (move-duration)
        (deliver-duration)
        (empty-duration)
    )

    ;Si procede a definire la action relativa al ritiro di una box da una location.
    ;I parametri utili alla action in oggetto sono: la location, la scatola da ritirare e l'agente robotico.
    (:durative-action pick-up
        :parameters (?loc - location ?box - box ?agent - agent ?carrier - carrier)
        :duration (= ?duration (pick-duration))
        ;Le precondizioni che consentono l'esecuzione prevedono che:
        ;-l'agente robotico non sia occupato
        ;-l'agente robotico si trovi nella location inserita come parametro
        ;-la box si trovi nella location inserita come parametro
        :condition (and
            (at start (free-agent ?agent))
            (over all (carrier-at-agent ?agent ?carrier))
            (over all (at-agent ?agent ?loc))
            (at start (at-box ?box ?loc))
            (over all (< (amount ?carrier) (capacity ?carrier)))
        )
        ;Gli effetti dell'esecuzione portano ad uno stato in cui:
        ;-l'agente robotico non è più libero e porta la box inserita come parametro
        ;-la box non risulta più alla location
        :effect (and
            (at start (not(free-agent ?agent)))
            (at end (increase (amount ?carrier) 1))
            (at end (box-at-carrier ?carrier ?box))
            (at start (not (at-box ?box ?loc)))
            (at end (free-agent ?agent))
        )
    )

    ;Si procede a definire la action relativa allo spostamento di un agente da una location all'altra.
    ;I parametri utili alla action in oggetto sono: le location (from e to) e l'agente robotico coinvolto.
    (:durative-action move
        :parameters (?from ?to - location ?agent - agent)
        :duration (= ?duration (move-duration))
        ;Le precondizioni che consentono l'esecuzione prevedono che:
        ;-l'agente robotico si trovi nella location di partenza
        ;-le locations siano connesse
        :condition (and
            (at start (free-agent ?agent))
            (at start (at-agent ?agent ?from))
            (over all (connected ?from ?to))
        )
        ;Gli effetti dell'esecuzione portano ad uno stato in cui:
        ;-l'agente robotico non si trovi nella location di partenza
        ;-l'agente robotico si trovi nella location di arrivo
        :effect (and
            (at start (not(free-agent ?agent)))
            (at start (not (at-agent ?agent ?from)))
            (at end (at-agent ?agent ?to))
            (at end (free-agent ?agent))
        )
    )

    ;Si procede a definire la action relativa alla consegna di una box ad una workstation.
    ;I parametri utili alla action in oggetto sono: la box da consegnare, il contenuto della box, la workstation
    ;a cui consegnare la box, la location in cui si trova la workstation e l'agente robotico coinvolto.
    (:durative-action deliver
        :parameters (?box - box ?content - content ?t - content-type ?ws - workstation ?location - location ?agent - agent ?carrier - carrier)
        :duration (= ?duration (deliver-duration))
        ;Le precondizioni che consentono l'esecuzione prevedono che:
        ;-l'agente robotico si trovi nella stessa location della workstation a cui consegnare la box
        ;-l'agente robotico stia portando la box da consegnare
        ;-la box abbia un contenuto necessario alla workstation
        :condition (and
            (at start (free-agent ?agent))
            (over all (carrier-at-agent ?agent ?carrier))
            (over all (is-type ?content ?t))
            (over all (at-agent ?agent ?location))
            (over all (box-at-carrier ?carrier ?box))
            (over all (at-ws ?ws ?location))
            (over all (filled ?box ?content))
        )
        ;Gli effetti dell'esecuzione portano ad uno stato in cui:
        ;-l'agente robotico non porta più la box
        ;-la workstation non ha più bisogno del contenuto della box
        ;-la box si trova nella workstation a cui è stata consegnata
        :effect (and
            (at start (not(free-agent ?agent)))
            (at end (not (box-at-carrier ?carrier ?box)))
            (at end (decrease (amount ?carrier) 1))
            (at end (box-at-workstation ?ws ?box))
            (at end (free-agent ?agent))
        )
    )

    ;Si procede a definire la action relativa al riempimento di una box con un contenuto.
    ;I parametri utili alla action in oggetto sono: la box da riempire, il contenuto della box, la location
    ;in cui si trova il contenuto e l'agente robotico coinvolto.
    (:durative-action fill
        :parameters (?box - box ?content - content ?t - content-type ?loc - location ?agent - agent ?carrier - carrier)
        :duration (= ?duration (fill-duration))
        ;Le precondizioni che consentono l'esecuzione prevedono che:
        ;-la box sia vuota
        ;-l'agente robotico stia portando la box da riempire
        ;-il contenuto e l'agente robotico si trovino nella stessa location
        :condition (and
            (at start (free-agent ?agent))
            (over all (carrier-at-agent ?agent ?carrier))
            (over all (is-type ?content ?t))
            (over all (empty ?box))
            (over all (box-at-carrier ?carrier ?box))
            (at start (at-content ?content ?loc))
            (over all (at-agent ?agent ?loc))
        )
        ;Gli effetti dell'esecuzione portano ad uno stato in cui:
        ;-la box non è più vuota
        ;-la box è riempita con il contenuto
        ;-il contenuto non si trova più nella location
        :effect (and
            (at start (not(free-agent ?agent)))
            (at end (not (empty ?box)))
            (at end (filled ?box ?content))
            (at start (not (at-content ?content ?loc)))
            (at end (free-agent ?agent))
        )
    )

    ;Si procede a definire la action relativa allo svuotamento di una box.
    ;I parametri utili alla action in oggetto sono: la box da svuotare, il contenuto della box,
    ;la workstation che riceve il contenuto, l'agente robotico coinvolto e la location.
    (:durative-action empty
        :parameters (?box - box ?content - content ?t - content-type ?ws - workstation ?agent - agent ?loc - location)
        :duration (= ?duration (empty-duration))
        ;Le precondizioni che consentono l'esecuzione prevedono che:
        ;-l'agente non sia impegnato
        ;-la box sia riempita con il contenuto da consegnare
        ;-la box si trovi nella workstation che deve ricevere il contenuto
        ;-l'agente e la workstation si trovino nella stessa locazione
        :condition (and
            (at start (free-agent ?agent))
            (over all (is-type ?content ?t))
            (over all (filled ?box ?content))
            (at start (box-at-workstation ?ws ?box))
            (over all (at-agent ?agent ?loc))
            (over all (at-ws ?ws ?loc))
        )
        ;Gli effetti dell'esecuzione portano ad uno stato in cui:
        ;-la box non ha più contenuto
        ;-la box viene rilasciata nella locazione
        ;-la workstation ha il contenuto della box
        :effect (and
            (at start (not(free-agent ?agent)))
            (at end (not (filled ?box ?content)))
            (at end (empty ?box))
            (at end (at-box ?box ?loc))
            (at start (not (box-at-workstation ?ws ?box)))
            (at end (content-at-workstation ?ws ?t))
            (at end (free-agent ?agent))
        )
    )
)
