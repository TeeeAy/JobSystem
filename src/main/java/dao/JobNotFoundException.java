package dao;

public class JobNotFoundException extends RuntimeException{

    public static String DEFAULT_NOT_FOUND_ERROR_MESSAGE = "Job not found";

    public JobNotFoundException() {
        super(DEFAULT_NOT_FOUND_ERROR_MESSAGE);
    }

    public JobNotFoundException(String message) {
        super(message);
    }
}
