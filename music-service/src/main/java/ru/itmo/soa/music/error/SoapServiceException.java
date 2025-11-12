package ru.itmo.soa.music.error;

import jakarta.xml.ws.WebFault;

@WebFault(name = "error", targetNamespace = "http://music.soa.itmo.ru/", faultBean = "ru.itmo.soa.music.error.ApiError")
public class SoapServiceException extends Exception {

    private final ApiError faultInfo;

    public SoapServiceException(ApiError faultInfo) {
        super(faultInfo != null ? faultInfo.getMessage() : null);
        this.faultInfo = faultInfo;
    }

    public SoapServiceException(String message, ApiError faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public SoapServiceException(String message, Throwable cause, ApiError faultInfo) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public ApiError getFaultInfo() {
        return faultInfo;
    }
}


