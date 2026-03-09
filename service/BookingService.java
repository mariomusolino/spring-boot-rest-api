package com.odissey.tour.service;

import com.odissey.tour.exception.Exception404;
import com.odissey.tour.exception.Exception409;
import com.odissey.tour.model.entity.Customer;
import com.odissey.tour.model.entity.Tour;
import com.odissey.tour.model.entity.enumerator.TourStatus;
import com.odissey.tour.model.entity.User;
import com.odissey.tour.repository.CustomerRepository;
import com.odissey.tour.repository.TourRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public String bookingTour(int tourId, UserDetails userDetails){

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(()-> new Exception404("Il tour non trovato"));

        if(!tour.getStatus().equals(TourStatus.OPEN))
            throw new Exception404("Il tour non è prenotabile.");

        User user = (User) userDetails;
        Customer customer = customerRepository.findById(user.getId())
                .orElseThrow(() -> new Exception404("Cliente non trovato"));

        // verificare che il tour che il customer vuole prenotare non si sovrapponga con
        // eventuali altri tour che ha prenotato neanche per un giorno
        // e che non stia prenotando un viaggio che ha già prenotato
        Set<Tour> customerTours = customer.getTours();
        for(Tour t : customerTours) {
            if(t.getStartDate().isBefore(tour.getEndDate()) &&
                    t.getEndDate().isAfter(tour.getStartDate()) &&
                    t.getStatus().equals(TourStatus.OPEN) &&
                    !t.getId().equals(tour.getId())
            )
                throw new Exception409("Il tour che vuoi prenotare si sovrappone ad un altro tour che hai gia prenotato: "+t.getName()+" ("+t.getStartDate()+" - "+t.getEndDate()+")");
            if(t.getId().equals(tour.getId()))
                throw new Exception409("Hai già prenotato questo viaggio");
        }

        if(tour.getMaxPax() > tour.getCustomers().size())
            tour.addCustomer(customer);

        if(tour.getMaxPax() == tour.getCustomers().size())
            tour.setStatus(TourStatus.SOLD_OUT);

        tourRepository.save(tour);
        return "Complimenti "+customer.getUser().getFirstname()+" "+customer.getUser().getLastname()+", hai prenotato il tour "+tour.getName();
    }
}
