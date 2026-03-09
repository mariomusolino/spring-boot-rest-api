package com.odissey.tour.model.dto.response;

import com.odissey.tour.model.entity.Voucher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VoucherReceiptResponse {

    private String emittedBy;
    private String code;
    private int customerId;
    private String customerFullName;
    private float price;

    public static VoucherReceiptResponse fromEntityToDto(Voucher voucher){
        return new VoucherReceiptResponse(
                voucher.getEmittedBy(),
                voucher.getCode(),
                voucher.getCustomer().getId(),
                voucher.getCustomer().getUser().getFirstname().concat(" ").concat( voucher.getCustomer().getUser().getLastname()),
                voucher.getPrice()
        );
    }
}
