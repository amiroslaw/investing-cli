package ovh.miroslaw.investing;

public class RetrievingDataException extends Exception {

    private final ErrorCode errorCode;

    public RetrievingDataException(String errorMsg, ErrorCode errorCode) {
        super(errorMsg);
        this.errorCode = errorCode;
    }

    public enum ErrorCode {
        SOFTWARE(1), USAGE(2);
        int code;

        ErrorCode(int code) {
            this.code = code;
        }
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
