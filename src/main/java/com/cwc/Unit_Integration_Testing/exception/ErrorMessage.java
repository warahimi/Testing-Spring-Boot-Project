package com.cwc.Unit_Integration_Testing.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorMessage {
    private String timeStamp;
    private String error;
    private String message;
    private String status;
}
