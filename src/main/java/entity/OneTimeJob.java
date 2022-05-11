package entity;

import lombok.experimental.SuperBuilder;

@SuperBuilder(setterPrefix = "with", toBuilder = true)
public class OneTimeJob extends Job {

    public OneTimeJob(String jobType, Integer secondsToExecute) {
        super(jobType, secondsToExecute);
    }

    @Override
    public void run() {
        super.run();
        setState(State.FINISHED);
    }
}
