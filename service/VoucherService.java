package com.odissey.tour.service;

import com.odissey.tour.exception.Exception400;
import com.odissey.tour.exception.Exception404;
import com.odissey.tour.model.dto.request.VoucherRequest;
import com.odissey.tour.model.dto.response.VoucherReceiptResponse;
import com.odissey.tour.model.entity.Customer;
import com.odissey.tour.model.entity.Voucher;
import com.odissey.tour.model.entity.enumerator.VoucherType;
import com.odissey.tour.repository.CustomerRepository;
import com.odissey.tour.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherService {

    private final CustomerRepository customerRepository;
    private final VoucherRepository voucherRepository;

    public VoucherReceiptResponse create(VoucherRequest req){
        String type = req.getType().trim().toUpperCase();
        if(!type.equals(VoucherType.GIFT.name()) && !type.equals(VoucherType.REFUND.name()))
            throw new Exception400("La tipologia di voucher non è valida");

        Customer customer = customerRepository.findById(req.getCustomerId())
                .orElseThrow(()-> new Exception404("Customer non trovato con id "+req.getCustomerId()));
        Voucher voucher = new Voucher(customer, req.getPrice(), VoucherType.valueOf(type), req.getEmittedBy());
        voucherRepository.save(voucher);
        return VoucherReceiptResponse.fromEntityToDto(voucher);

    }
}
