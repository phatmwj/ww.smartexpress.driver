package ww.smartexpress.driver.data.model.api.response;

import lombok.Data;

@Data
public class DriverServiceResponse {

    private Long id;

    private int ratioShare;

    private int state;

    private  int status;

    private ProfileResponse driver;

    private ServiceResponse service;
}
