package com.odissey.tour.service;

import com.odissey.tour.exception.*;
import com.odissey.tour.model.GenericMail;
import com.odissey.tour.model.dto.request.TourRequest;
import com.odissey.tour.model.dto.response.TourDetailResponse;
import com.odissey.tour.model.dto.response.TourResponse;
import com.odissey.tour.model.dto.response.TourResponsePaginated;
import com.odissey.tour.model.entity.*;
import com.odissey.tour.model.entity.enumerator.TourStatus;
import com.odissey.tour.model.entity.enumerator.VoucherType;
import com.odissey.tour.repository.*;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourService {

    private final TourRepository tourRepository;
    private final CountryRepository countryRepository;
    private final BranchRepository branchRepository;
    private final EmailService emailService;
    private final PaymentRepository paymentRepository;
    private final VoucherRepository voucherRepository;

    public TourDetailResponse create(TourRequest req){
        String name = req.getName().trim();
        String description = req.getDescription().trim();

        if(req.getEndDate().isBefore(req.getStartDate()))
            throw new Exception400("La data di fine tour non può essere antecedente a quella di inizio.");

        if(req.getMinPax() > req.getMaxPax())
            throw new Exception400("il numero minimo di partecipanti non può essere maggiore del numero massimo degli stessi.");

        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id "+req.getCountryId()));

        Branch branch = branchRepository.findByIdAndActiveTrue(req.getBranchId())
                .orElseThrow(()-> new Exception404("Filiale non trovata con id "+req.getBranchId()));

        Tour tour = new Tour(
                branch,
                country,
                name,
                description,
                req.getStartDate(),
                req.getEndDate(),
                req.getMinPax(),
                req.getMaxPax(),
                req.getPrice()
        );

        try {
            tourRepository.save(tour);
        } catch(Exception e){
            log.error(e.getMessage());
            throw new Exception400("Lo stesso tour per la stessa data è già stato inserito.");
        }
        return TourDetailResponse.fromEntityToDto(tour);

    }

    public TourResponsePaginated getAllTours(Integer branchId, int pageSize, int pageNumber, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        Page<TourResponse> page = tourRepository.getAllTours(branchId, pageable);
        return new TourResponsePaginated(pageNumber, pageSize, page.getTotalElements(), page.getTotalPages(), page.getContent());
    }


    public TourResponsePaginated getFilteredTours(Integer branchId, String status,
                                                  Short countryId, LocalDate startDate, LocalDate endDate,
                                                  float minPrice, Float maxPrice, Double avg,
                                                  String keyword, boolean isCaseSensitive, boolean isExactMatch,
                                                  int pageSize, int pageNumber, String sortBy, String direction) {
        List<TourResponse> list = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        try {
            // check tour status
            TourStatus tourStatus = status != null ? TourStatus.valueOf(status.toUpperCase().trim()) : null;
            // check date range
            if(endDate != null && startDate == null)
                throw new Exception400("Non è possibile valorizzare solo la data di fine periodo.");
            if(endDate != null && endDate.isBefore(startDate))
                throw new Exception400("La data di fine tour è antecedente a quella di partenza.");
            // check price range
            if(maxPrice != null && maxPrice < minPrice)
                throw new Exception400("il prezzo massimo è minore di quello minimo.");
            // check avg value
            if(avg != null && avg > 5d)
                throw new Exception400("Il valore della media non può essere maggiore di 5.00");

            Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            list = tourRepository.getFilteredTours(branchId, tourStatus, countryId, startDate, endDate, minPrice, maxPrice, avg, keyword != null ? '%'+keyword+'%' : keyword, sort);
            // check keyword
            if(keyword != null) {
                if (keyword.trim().length() < 3)
                    throw new Exception400("La parola da ricercare deve essere lunga almeno 3 caratteri");
                else {
                    if (!list.isEmpty()) {
                        Pattern pattern = null;
                        if (!isCaseSensitive && !isExactMatch)
                            pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
                        else if (!isCaseSensitive && isExactMatch)
                            pattern = Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE);
                        else if (isCaseSensitive && !isExactMatch)
                            pattern = Pattern.compile(keyword);
                        else
                            pattern = Pattern.compile("\\b" + keyword + "\\b");
                        Pattern finalPattern = pattern;
                        list = list.stream()
                                .filter(tour -> finalPattern.matcher(tour.getName().concat(" ").concat(tour.getDescription())).find())
                                .toList();
                    }
                }
            }
        } catch (IllegalArgumentException e){
            throw new Exception400("Stato del tour '"+status+"' non valido.");
        }
        Page<TourResponse> page = PaginationService.listToPage(list, pageable);
        return new TourResponsePaginated(pageNumber, pageSize, page.getTotalElements(), page.getTotalPages(), page.getContent());
    }


    public TourDetailResponse update(int id, TourRequest req){

        Tour tour = tourRepository.findById(id)
                .orElseThrow(()-> new Exception404("Tour non trovato con id "+id));

        if(!tour.getStatus().equals(TourStatus.WORK_IN_PROGRESS))
            throw new Exception422("Il tour non può più essere modificato.");

        String name = req.getName().trim();
        String description = req.getDescription().trim();

        if(req.getEndDate().isBefore(req.getStartDate()))
            throw new Exception400("La data di fine tour non può essere antecedente a quella di inizio.");

        if(req.getMinPax() > req.getMaxPax())
            throw new Exception400("il numero minimo di partecipanti non può essere maggiore del numero massimo degli stessi.");

        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id "+req.getCountryId()));

        Branch branch = branchRepository.findByIdAndActiveTrue(req.getBranchId())
                .orElseThrow(()-> new Exception404("Filiale non trovata con id "+req.getBranchId()));

        tour.setBranch(branch);
        tour.setCountry(country);
        tour.setName(name);
        tour.setDescription(description);
        tour.setStartDate(req.getStartDate());
        tour.setEndDate(req.getEndDate());
        tour.setMaxPax(req.getMaxPax());
        tour.setMinPax(req.getMinPax());
        tour.setPrice(req.getPrice());

        try {
            tourRepository.save(tour);
        } catch(Exception e){
            log.error(e.getMessage());
            throw new Exception400("Lo stesso tour per la stessa data è già stato inserito.");
        }
        return TourDetailResponse.fromEntityToDto(tour);

    }

    @Transactional
    public TourDetailResponse cancelTour(int id) {

        Tour tour = tourRepository.findById(id)
                .orElseThrow(()-> new Exception404("Tour non trovato con id "+id));
        Country country = countryRepository.findByIdAndActiveTrue(tour.getCountry().getId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id "+id));

        tour.setCountry(country);

        if(tour.getStatus().equals(TourStatus.EXPIRED))
            throw new Exception400("Il tour si è già concluso quindi non può essere cancellato.");

        if(!tour.getStatus().equals(TourStatus.WORK_IN_PROGRESS) || !tour.getStatus().equals(TourStatus.CANCELED)){
            // verificare se ci siano già partecipanti, avvisarli mia mail e rimborsarli
            Set<Customer> customers = tour.getCustomers();
            for(Customer customer : customers){
                try{
                    emailService.sendMail(sendNotification(customer.getUser(), tour.getName()));
                    // rimborso
                    Float amountRefund = paymentRepository.sumPaymentByTourAndCustomer(tour.getId(), customer.getId());
                    if(amountRefund != null){
                        Voucher voucher = new Voucher(customer, amountRefund, VoucherType.REFUND, tour.getBranch().getName());
                        voucherRepository.save(voucher);
                    }
                }catch (MessagingException ex){
                    log.error("Email non inviata a " + customer.getUser().getEmail());
                }
            }
        }

        tour.setStatus(TourStatus.CANCELED);
        tourRepository.save(tour);

        return TourDetailResponse.fromEntityToDto(tour);
    }



    public TourDetailResponse changeTourStatus(int id, String status) {
        try {
            TourStatus newStatus = TourStatus.valueOf(status);

            Tour tour = tourRepository.findById(id)
                    .orElseThrow(()-> new Exception404("Tour non trovato con id "+id));
            Country country = countryRepository.findByIdAndActiveTrue(tour.getCountry().getId())
                    .orElseThrow(()-> new Exception404("Nazione non trovata con id "+tour.getCountry().getId()));

            TourStatus oldStatus = tour.getStatus();

            // WORK_IN_PROGRESS
            if(newStatus.equals(TourStatus.WORK_IN_PROGRESS) && !oldStatus.equals(TourStatus.WORK_IN_PROGRESS))
                throw new Exception400("Il tour è già in vendita e quindi lo stato WORK_IN_PROGRESS non è utilizzabile.");
            if(newStatus.equals(TourStatus.WORK_IN_PROGRESS) && oldStatus.equals(TourStatus.WORK_IN_PROGRESS))
                throw new Exception400("Il tour è già in stato WORK_IN_PROGRESS.");

            // OPEN
           if(newStatus.equals(TourStatus.OPEN) && !oldStatus.equals(TourStatus.WORK_IN_PROGRESS))
                throw new Exception400("Il tour è già in vendita.");
           if(newStatus.equals(TourStatus.OPEN) && oldStatus.equals(TourStatus.WORK_IN_PROGRESS))
                throw new Exception400("Il tour è già in status OPEN.");

           // SOLD_OUT
           if(newStatus.equals(TourStatus.SOLD_OUT))
               throw new Exception400("Il tour può cambiare stato in SOLD_OUT solo in base alle prenotazioni."); // Cambio che trattiamo in BookingService quando facciamo una prenotazione

           // IN_PROGRESS
           if(newStatus.equals(TourStatus.IN_PROGRESS))
               throw new Exception400("Il tour può cambiare stato in IN_PROGRESS solo in base alla data di partenza."); // Cambio eseguito da schedulazione

            // EXPIRED
            if(newStatus.equals(TourStatus.EXPIRED))
                throw new Exception400("Il tour può cambiare stato in EXPIRED solo se la data attuale è successiva alla data di fine tour."); // Cambio eseguito da schedulazione

            // NOT_SOLD_OUT
            if(newStatus.equals(TourStatus.NOT_SOLD_OUT))
                throw new Exception400("Il tour può cambiare stato in NOT_SOLD_OUT solo se la data attuale coincide con la data di inizio tour e non si è raggiunto il numero minimo di partecipanti."); // Cambio eseguito da schedulazione

            // CANCELED
            if(newStatus.equals(TourStatus.CANCELED))
                throw new Exception401("Solo l'amministratore può cancellare il tour d'ufficio");


            tour.setStatus(newStatus);
            tour.setCountry(country);
            tourRepository.save(tour);
            return TourDetailResponse.fromEntityToDto(tour);

        } catch (IllegalArgumentException e){
            throw new Exception404("Il nuovo status non è tra quelli validi.");
        }
    }

    @Transactional
    public String earlyCancellation(int tourId, int customerId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(()-> new Exception404("Tour non trovato con id " + tourId));
        if((!tour.getStatus().equals(TourStatus.OPEN) && !tour.getStatus().equals(TourStatus.SOLD_OUT)) && tour.getStartDate().isAfter(LocalDate.now().plusDays(5L)))
            throw new Exception409("Non puoi disdire il tour.");
        Set<Customer> customers = tour.getCustomers();
        if(customers.stream().noneMatch(c -> c.getId() == customerId))
            throw new Exception409("Non puoi disdire un tour che non hai prenotato.");

        Customer customer = new Customer(customerId);
        tour.getCustomers().remove(customer);

        tour.setStatus(TourStatus.OPEN);

        Float amountToRefund = paymentRepository.sumPaymentByTourAndCustomer(tour.getId(), customerId);
        if(amountToRefund != null){
            Voucher voucher = new Voucher(customer, amountToRefund*0.6f, VoucherType.REFUND, tour.getBranch().getName());
            voucherRepository.save(voucher);
        }

        return "Disdetta del tour avvenuta con successo";
    }

    public static GenericMail sendNotification(User user, String tourName){
        GenericMail mail = new GenericMail();
        mail.setTo(user.getEmail());
        mail.setSubject("Tour Odissey: cancellazione tour '" + tourName + "'");
        mail.setBody("Gentile "+user.getFirstname()+" "+user.getLastname()+",\nci dispiace informarla che il tour è stato cancellato. seguiranno altre comunicazioni per il rimborso.");
        return mail;
    }
}
