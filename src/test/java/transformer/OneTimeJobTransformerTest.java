package transformer;

import dto.OneTimeJobInfo;
import entity.OneTimeJob;
import entity.State;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OneTimeJobTransformerTest {

    private final OneTimeJobTransformer OneTimeJobTransformer = new OneTimeJobTransformer();

    @Test
    public void shouldTransform(){
        OneTimeJobInfo oneTimeJobInfo = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(10)
                .build();
        OneTimeJob oneTimeJob = OneTimeJob.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(10)
                .withState(State.CREATED)
                .build();
        assertEquals(oneTimeJob, OneTimeJobTransformer.transform(oneTimeJobInfo));
    }
}
