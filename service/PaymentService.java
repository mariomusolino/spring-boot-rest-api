package com.odissey.tour.service;

import com.odissey.tour.exception.Exception400;
import com.odissey.tour.exception.Exception404;
import com.odissey.tour.exception.Exception409;
import com.odissey.tour.model.dto.request.PaymentRequest;
import com.odissey.tour.model.entity.Customer;
import com.odissey.tour.model.entity.Payment;
import com.odissey.tour.model.entity.Tour;
import com.odissey.tour.model.entity.Voucher;
import com.odissey.tour.model.entity.enumerator.PaymentType;
import com.odissey.tour.model.entity.enumerator.TourStatus;
import com.odissey.tour.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;
    private final VoucherRepository voucherRepository;


    @Transactional
    public String create(PaymentRequest req){

        Tour tour = tourRepository.findById(req.getTourId())
                .orElseThrow(()-> new Exception404("Tour non trovato con id "+req.getTourId()));
        if(!tour.getStatus().equals(TourStatus.OPEN))
            throw new Exception409("Il tour non è prenotabile e quindi non è pagabile");
        Customer customer = customerRepository.findById(req.getCustomerId())
                .orElseThrow(()-> new Exception404("Customer non trovato con id "+req.getCustomerId()));

        // boolean isBooked = verificare che il tour sia stato effettivamente prenotato dal customer,
        // ovvero che esista un record sulla tabella Booking
//        boolean isBooked = false;
//        Set<Tour> customerTours = customer.getTours();
//        for(Tour t : customerTours){
//            if(t.getId() == req.getTourId()){
//                isBooked = true;
//            }
//        }
        boolean isBooked = tour.getCustomers().contains(customer);
        if(!isBooked)
            throw new Exception409("Prenotazione al tour: " + tour.getName() + ", non effettuata dal customer: " + customer.getUser().getUsername() + ". Per poter effettuare il pagamento bisogna prima effettuare la prenotazione al tour.");

        PaymentType type = null;
        try{
            type = PaymentType.valueOf(req.getPaymentType().trim().toUpperCase());
        } catch (IllegalArgumentException e){
            throw new Exception400("Il tipo di pagamento non è tra quelli ammessi");
        }
        Voucher voucher = null;

        if(req.getVoucherId() != null && !type.equals(PaymentType.VOUCHER))
            throw new Exception409("Se selezioni un voucher da usare il tipo di pagamento deve essere 'voucher'");

        if(req.getVoucherId() != null && type.equals(PaymentType.VOUCHER)) {
        voucher = voucherRepository.findByIdAndCustomerIsAndUsedFalse(req.getVoucherId(), customer)
                .orElseThrow(() -> new Exception404("Voucher non trovato con id " + req.getVoucherId()));
        }
        if(voucher != null && !voucher.getEndValidity().isAfter(LocalDate.now()))
            throw new Exception409("Il voucher è scaduto!");

        if(req.getVoucherId() == null && type.equals(PaymentType.VOUCHER))
            throw new Exception400("Il tipo di pagamento prevede l'inserimento di un voucher valido");
        float amount = 0f;
        if(req.getAmount() == null && type.equals(PaymentType.VOUCHER))
            amount = voucher.getPrice();
        else if(req.getAmount() == null)
            throw new Exception400("L'importo del pagamento non può essere nullo");
        else if(req.getAmount() != null && type.equals(PaymentType.VOUCHER))
            throw new Exception400("Se usi un voucher non devi inserire l'importo.");
        else
            amount = req.getAmount();

        // verificare che il pagamento che stiamo effettuando (sommandosi agli altri già effettuati dal customer) non superi il valore del tour
        Float totalAmount = paymentRepository.sumPaymentByTourAndCustomer(tour.getId(), customer.getId());
        totalAmount = totalAmount == null ? 0f : totalAmount;
        if((amount + totalAmount) > tour.getPrice())
            throw new Exception409("Il versamento che vuoi effettuare supera il valore del tour di "+String.format("%.2f", (amount + totalAmount)-tour.getPrice()));

        Payment payment = new Payment(tour, customer, voucher,type, amount);
        paymentRepository.save(payment);
        if(voucher != null)
            voucher.setUsed(true);

        String currency = tour.getBranch().getAgency().getCountry().getCurrency();

        return (tour.getPrice()-(amount + totalAmount)) > 0 ?
                "Con questo versamento di "+amount+" "+currency+" ti mancano "+(tour.getPrice()-(amount + totalAmount))+" "+currency+" per saldare il prezzo del viaggio." :
                "Con questo versamento di "+amount+" "+currency+" hai saldato il viaggio!";
    }
}
