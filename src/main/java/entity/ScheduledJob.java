package entity;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Getter
@SuperBuilder(setterPrefix = "with", toBuilder = true)
public class ScheduledJob extends Job {

    @Builder.Default
    private Instant lastExecutionTimeStamp = LocalDateTime
            .now()
            .toInstant(ZoneOffset.UTC);

    private final Integer repetitionPeriod;

    public ScheduledJob(String jobType, Integer secondsToExecute, Integer repetitionPeriod) {
        super(jobType, secondsToExecute);
        this.repetitionPeriod = repetitionPeriod;
    }

    @Override
    public void run() {
        super.run();
        setState(State.IDLE);
        lastExecutionTimeStamp = LocalDateTime.now().toInstant(ZoneOffset.UTC);
    }

    @Override
    public String toString() {
        return super.toString() + " Repetition period:" + repetitionPeriod +
                " seconds" + " Last executed: " + lastExecutionTimeStamp;
    }
}
