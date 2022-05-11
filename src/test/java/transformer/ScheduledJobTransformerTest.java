package transformer;

import dto.ScheduledJobInfo;
import entity.ScheduledJob;
import entity.State;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduledJobTransformerTest{

    private final ScheduledJobTransformer scheduledJobTransformer = new ScheduledJobTransformer();

    @Test
    public void shouldTransform(){
        ScheduledJobInfo scheduledJobInfo = ScheduledJobInfo.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(10)
                .withRepetitionPeriod(70)
                .build();
        ScheduledJob job = ScheduledJob.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(10)
                .withRepetitionPeriod(70)
                        .withState(State.CREATED)
                .build();
        assertEquals(job, scheduledJobTransformer.transform(scheduledJobInfo));
    }

}
