package ww.smartexpress.driver.data.model.api.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;
    private ProfileResponse driver;
    private String code;
    private String status;
    private String createdDate;
    private Customer customer;
    private ServiceResponse service;
    private int state;
    private String destinationAddress;
    private String pickupAddress;
    private Double money;
    private Double promotionMoney;
    private List<BookingDetails> bookingDetails;
    private int ratioShare;
    private Rating rating;
    private Double distance;

    private Double codPrice;
    private String consigneeName;
    private String consigneePhone;
    private Boolean isCod;

    private String pickupImage;
    private String deliveryImage;
    private String senderName;
    private String senderPhone;
}
