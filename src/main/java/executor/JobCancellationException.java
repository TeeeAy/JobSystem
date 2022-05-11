package executor;

public class JobCancellationException extends RuntimeException {

    public static String DEFAULT_CANCELLATION_ERROR_MESSAGE = "You can't cancel exception which has either been " +
            "already finished or haven't been started yet";

    public JobCancellationException() {
        super(DEFAULT_CANCELLATION_ERROR_MESSAGE);
    }

    public JobCancellationException(String message) {
        super(message);
    }
}
