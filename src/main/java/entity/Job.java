package entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@EqualsAndHashCode
public abstract class Job implements Runnable {

    @Setter
    private String jobId;

    @Setter
    @Builder.Default
    private State state = State.CREATED;

    private final String jobType;

    private final Runnable task;

    @Setter
    private Future<?> future;


    public Job(String jobType, Runnable task) {
        this.jobType = jobType;
        this.task = task;
    }

    @SneakyThrows
    @Override
    public void run() {
        state = State.STARTED;
        log.info(this.toString());
        state = State.RUNNING;
        task.run();
        state = State.FINISHED;
        log.info(this.toString());
    }

    @Override
    public String toString(){
        return "Job:" + jobId + " Job Type: " + jobType + " State:" + state;
    }
}
