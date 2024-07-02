package ww.smartexpress.driver.data.model.api.response;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentBooking {
    private Long id;
    private int status;
    private String modifiedDate;
    private String createdDate;
    private ProfileResponse driver;
    private Customer customer;
    private ServiceResponse service;
    private Room room;
    private String code;
    private Integer state;
    private String pickupAddress;
    private double pickupLat;
    private double pickupLong;
    private String destinationAddress;
    private double destinationLat;
    private double destinationLong;
    private double distance;
    private double money;
    private double promotionMoney;
    private List<BookingDetails> bookingDetailsList;
    private String note;
    private String customerNote;
    private Rating rating;

    private Double codPrice;
    private String consigneeName;
    private String consigneePhone;
    private Boolean isCod;

    private String pickupImage;
    private String deliveryImage;
    private String senderName;
    private String senderPhone;
    private Integer paymentKind;
    private Integer ratioShare;
}
