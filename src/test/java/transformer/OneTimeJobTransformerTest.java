package transformer;

import dto.OneTimeJobInfo;
import entity.OneTimeJob;
import entity.State;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class OneTimeJobTransformerTest {

    private final OneTimeJobTransformer OneTimeJobTransformer = new OneTimeJobTransformer();

    @Test
    public void shouldTransform(){
        Runnable runnable = () -> log.info("Test shouldTransform");
        OneTimeJobInfo oneTimeJobInfo = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .build();
        OneTimeJob oneTimeJob = OneTimeJob.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .withState(State.CREATED)
                .build();
        assertEquals(oneTimeJob, OneTimeJobTransformer.transform(oneTimeJobInfo));
    }
}
