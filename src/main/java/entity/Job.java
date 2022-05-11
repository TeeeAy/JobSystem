package entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@SuperBuilder(setterPrefix = "with", toBuilder = true)
public abstract class Job implements Runnable {

    @Setter
    private String jobId;

    @Setter
    @Builder.Default
    private State state = State.CREATED;

    private final String jobType;

    private final Integer secondsToExecute;

    @Setter
    private Future<?> future;


    public Job(String jobType, Integer secondsToExecute) {
        this.jobType = jobType;
        this.secondsToExecute = secondsToExecute;
    }

    @SneakyThrows
    @Override
    public void run() {
        state = State.STARTED;
        log.info(this.toString());
        state = State.RUNNING;
        Thread.sleep(TimeUnit.SECONDS.toMillis(secondsToExecute));
        state = State.FINISHED;
        log.info(this.toString());
    }

    @Override
    public String toString(){
        return "Job:" + jobId + " Job Type: " + jobType + " State:" + state;
    }
}
