package entity;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder(setterPrefix = "with", toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class OneTimeJob extends Job {

    public OneTimeJob(String jobType, Runnable task) {
        super(jobType, task);
    }

    @Override
    public void run() {
        super.run();
        setState(State.FINISHED);
    }
}
