package com.odissey.tour.model.entity.enumerator;

/**
 * Regole per cambi stato:
 * <OL>
 *     <LI>Lo status non può cambiare da WORK_IN_PROGRESS a SOLD_OUT, IN_PROGRESS, EXPIRED</LI>
 *     <LI>Da OPEN lo status può cambiare automaticamente in SOLD_OUT, IN_PROGRESS, EXPIRED, NOT_SOLD_OUT</LI>
 *     <LI>CANCELED può essere settato solo da ADMIN e non deve essere in status EXPIRED</LI>
 *     <LI>Lo status deve passare da OPEN a SOLD_OUT e poi a IN_PRGRESS ed infine a EXPIRED in maniera automatica</LI>
 * </OL>
 */
public enum TourStatus {

    WORK_IN_PROGRESS, // l'operatore lo sta creando/pensando, non è ancora in vendita
    OPEN, // il tour viene pubblicato; non può più essere modificato in quanto già vendibile
    SOLD_OUT, // il tour ha raggiunto il numero max di partecipanti
    IN_PROGRESS, // il tour è in corso di svolgimento
    EXPIRED, // il tour è finito e solo ora è votabile
    NOT_SOLD_OUT, // il tour non ha raggiunto il numero minimo di partecipanti
    CANCELED // il tour stato cancellato per cause di forza maggiore (es. motivi geopolitici)


}
