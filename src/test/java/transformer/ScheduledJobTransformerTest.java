package transformer;

import dto.ScheduledJobInfo;
import entity.ScheduledJob;
import entity.State;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ScheduledJobTransformerTest{

    private final ScheduledJobTransformer scheduledJobTransformer = new ScheduledJobTransformer();

    @Test
    public void shouldTransform(){
        Runnable runnable = () -> log.info("Test shouldTransform");
        ScheduledJobInfo scheduledJobInfo = ScheduledJobInfo.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .withRepetitionPeriod(70)
                .build();
        ScheduledJob job = ScheduledJob.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .withRepetitionPeriod(70)
                .withState(State.CREATED)
                .build();
        assertEquals(job, scheduledJobTransformer.transform(scheduledJobInfo));
    }

}
