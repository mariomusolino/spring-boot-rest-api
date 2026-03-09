package com.odissey.tour.service;

import com.odissey.tour.exception.Exception500;
import com.odissey.tour.model.GenericMail;
import com.odissey.tour.model.entity.Customer;
import com.odissey.tour.model.entity.Tour;
import com.odissey.tour.model.entity.User;
import com.odissey.tour.model.entity.Voucher;
import com.odissey.tour.model.entity.enumerator.TourStatus;
import com.odissey.tour.model.entity.enumerator.VoucherType;
import com.odissey.tour.repository.PaymentRepository;
import com.odissey.tour.repository.TourRepository;
import com.odissey.tour.repository.VoucherRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulingService {

    private final TourRepository tourRepository;
    private final PaymentRepository paymentRepository;
    private final VoucherRepository voucherRepository;
    private final EmailService emailService;

    // I METODI SOTTOPOSTI A SCHEDULAZIONE DEVONO SEMPRE ESSERE VOID

    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html#parse(java.lang.String)
    //@Scheduled(fixedRate = 10000000) // tempo espresso in millisecondi
    //@Scheduled(cron = "*/10 * * * * *") // ogni 10 secondi
    //@Scheduled(cron = "@daily") // ogni giorno a mezzanotte
    @Scheduled(cron = "0 0 9-12 * * *") // ogni giorno alle 9, 10, 11 e 12
    @Transactional
    public void changeTourStatus(){
        log.info(">> Sto cercando i tour a cui cambiare stato");
        Set<TourStatus> statuses = new HashSet<>();
        statuses.add(TourStatus.CANCELED);
        statuses.add(TourStatus.EXPIRED);
        statuses.add(TourStatus.WORK_IN_PROGRESS);
        List<Tour> validTours = tourRepository.findValidTours(statuses);
        List<Tour> tours = new ArrayList<>();

        for(Tour t : validTours){
            LocalDate now = LocalDate.now();
            TourStatus status = t.getStatus();

            if( (status.equals(TourStatus.OPEN) || status.equals(TourStatus.SOLD_OUT)) && Objects.equals(t.getStartDate(), now)) {
                if (t.getCustomers().size() >= t.getMinPax() )
                    t.setStatus(TourStatus.IN_PROGRESS);
                else {
                    t.setStatus(TourStatus.NOT_SOLD_OUT);
                    refund(t, t.getCustomers());
                }
                tours.add(t);
            }
            if( status.equals(TourStatus.IN_PROGRESS) && t.getEndDate().plusDays(1L) == now) {
                t.setStatus(TourStatus.EXPIRED);
                tours.add(t);
            }
        }
        tourRepository.saveAll(tours);
    }

    private void refund(Tour tour, Set<Customer> customers){
        List<Voucher> vouchers = new ArrayList<>();
        for(Customer c : customers){
            try {
                emailService.sendMail(sendNotificationForNotSoldOut(c, tour));
            } catch (MessagingException e){
                throw new Exception500("Si è verificato un errore duranto l'invio dell'email");
            }
            Float amountToRefund = paymentRepository.sumPaymentByTourAndCustomer(tour.getId(), c.getId());
            if(amountToRefund != null) {
                Voucher voucher = new Voucher(c, amountToRefund, VoucherType.REFUND, tour.getBranch().getName());
                vouchers.add(voucher);
            }
        }
        voucherRepository.saveAll(vouchers);
    }

    public static GenericMail sendNotificationForNotSoldOut(Customer customer, Tour tour){
        GenericMail mail = new GenericMail();
        mail.setTo(customer.getUser().getEmail());
        mail.setSubject("Tour Odissey: cancellazione tour '"+tour.getName()+"'");
        mail.setBody("Gentile "+customer.getUser().getFirstname()+" "+customer.getUser().getLastname()+",\nci dispiace informarLa che il tour in oggetto è stato cancellato in quanto non è stato raggiunto il numero minimo di partecipanti.\nLe è stato assegnato un voucher di rimborso (qualora abbia effettuato dei pagamenti per il tour in oggetto) spendibile presso " +tour.getBranch().getName());
        return mail;
    }
}
