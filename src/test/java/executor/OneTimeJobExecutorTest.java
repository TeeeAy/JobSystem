package executor;

import dao.JobDao;
import dto.OneTimeJobInfo;
import entity.OneTimeJob;
import entity.State;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import transformer.OneTimeJobTransformer;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class OneTimeJobExecutorTest {

    @Mock
    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Mock
    JobDao jobDao;

    @Mock
    OneTimeJobTransformer jobTransformer;

    @InjectMocks
    OneTimeJobExecutor jobExecutor;

    @Test
    void shouldExecuteJob() {
        Runnable runnable = () -> log.info("Test shouldExecuteJob");
        OneTimeJobInfo oneTimeJobInfo = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .build();
        OneTimeJob oneTimeJob = OneTimeJob.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .build();
        ScheduledFuture<?> future = mock(ScheduledFuture.class);
        given(jobTransformer.transform(oneTimeJobInfo)).willReturn(oneTimeJob);
        doReturn(future).when(scheduledThreadPoolExecutor).submit(oneTimeJob);

        jobExecutor.executeJob(oneTimeJobInfo);
        assertEquals(State.PENDING, oneTimeJob.getState());
        assertEquals(future, oneTimeJob.getFuture());

        then(jobDao).should(only()).saveJob(oneTimeJob);
        then(jobTransformer).should(only()).transform(oneTimeJobInfo);
        then(scheduledThreadPoolExecutor).should(only()).submit(oneTimeJob);
    }



    @Test
    void shouldThrowJobCancellationExceptionOnFinishedJob() {
        Runnable runnable = () -> log.info("Test shouldThrowJobCancellationExceptionOnFinishedJob");
        OneTimeJob oneTimeJob = OneTimeJob.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .build();
        oneTimeJob.setState(State.FINISHED);
        given(jobDao.getJobById(oneTimeJob.getJobId())).willReturn(oneTimeJob);
        JobCancellationException jobCancellationException =
                assertThrows(JobCancellationException.class,
                        () -> jobExecutor.cancelJobById(oneTimeJob.getJobId()));
        assertTrue(jobCancellationException
                .getMessage()
                .contains(JobCancellationException.DEFAULT_CANCELLATION_ERROR_MESSAGE));
        then(jobDao).should(only()).getJobById(oneTimeJob.getJobId());
    }


    @Test
    void shouldThrowJobCancellationExceptionOnCancelledJob() {
        Runnable runnable = () -> log.info("Test shouldThrowJobCancellationExceptionOnCancelledJob");
        OneTimeJob oneTimeJob = OneTimeJob.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .build();
        oneTimeJob.setState(State.CANCELLED);
        given(jobDao.getJobById(oneTimeJob.getJobId())).willReturn(oneTimeJob);
        JobCancellationException jobCancellationException =
                assertThrows(JobCancellationException.class,
                        () -> jobExecutor.cancelJobById(oneTimeJob.getJobId()));
        assertTrue(jobCancellationException
                .getMessage()
                .contains(JobCancellationException.DEFAULT_CANCELLATION_ERROR_MESSAGE));
        then(jobDao).should(only()).getJobById(oneTimeJob.getJobId());
    }


    @Test
    void shouldCancelJob() {
        Runnable runnable = () -> log.info("Test shouldCancelJob");
        OneTimeJob oneTimeJob = OneTimeJob.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .build();
        oneTimeJob.setFuture(mock(Future.class));
        given(jobDao.getJobById(oneTimeJob.getJobId())).willReturn(oneTimeJob);

        jobExecutor.cancelJobById(oneTimeJob.getJobId());
        assertEquals(State.CANCELLED, oneTimeJob.getState());

        then(jobDao).should(only()).getJobById(oneTimeJob.getJobId());
    }

}
