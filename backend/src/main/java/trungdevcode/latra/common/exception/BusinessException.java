// backend/src/main/java/trungdevcode/latra/common/exception/BusinessException.java
package trungdevcode.latra.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int status;

    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
    }

    // Default status 400 (Bad Request)
    public BusinessException(String message) {
        super(message);
        this.status = 400;
    }
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getCode();
    }
    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(errorCode.getMessage() + ": " + detailMessage);
        this.status = errorCode.getCode();
    }
}