package executor;

import dao.JobDao;
import dto.OneTimeJobInfo;
import entity.OneTimeJob;
import entity.State;
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
        int seconds = 2;
        OneTimeJobInfo oneTimeJobInfo = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(seconds)
                .build();
        OneTimeJob job = new OneTimeJob("jobType1", seconds);
        ScheduledFuture<?> future = mock(ScheduledFuture.class);
        given(jobTransformer.transform(oneTimeJobInfo)).willReturn(job);
        doReturn(future).when(scheduledThreadPoolExecutor).submit(job);

        jobExecutor.executeJob(oneTimeJobInfo);
        assertEquals(State.PENDING, job.getState());
        assertEquals(future, job.getFuture());

        then(jobDao).should(only()).saveJob(job);
        then(jobTransformer).should(only()).transform(oneTimeJobInfo);
        then(scheduledThreadPoolExecutor).should(only()).submit(job);
    }



    @Test
    void shouldThrowJobCancellationExceptionOnFinishedJob() {
        int seconds = 4;
        OneTimeJob job = new OneTimeJob("jobType1", seconds);
        job.setState(State.FINISHED);
        given(jobDao.getJobById(job.getJobId())).willReturn(job);
        JobCancellationException jobCancellationException =
                assertThrows(JobCancellationException.class,
                        () -> jobExecutor.cancelJobById(job.getJobId()));
        assertTrue(jobCancellationException
                .getMessage()
                .contains(JobCancellationException.DEFAULT_CANCELLATION_ERROR_MESSAGE));
        then(jobDao).should(only()).getJobById(job.getJobId());
    }


    @Test
    void shouldThrowJobCancellationExceptionOnCancelledJob() {
        int seconds = 4;
        OneTimeJob job = new OneTimeJob("jobType1", seconds);
        job.setState(State.CANCELLED);
        given(jobDao.getJobById(job.getJobId())).willReturn(job);
        JobCancellationException jobCancellationException =
                assertThrows(JobCancellationException.class,
                        () -> jobExecutor.cancelJobById(job.getJobId()));
        assertTrue(jobCancellationException
                .getMessage()
                .contains(JobCancellationException.DEFAULT_CANCELLATION_ERROR_MESSAGE));
        then(jobDao).should(only()).getJobById(job.getJobId());
    }


    @Test
    void shouldCancelJob() {
        int seconds = 2;
        OneTimeJob job = new OneTimeJob("jobType1", seconds);
        job.setFuture(mock(Future.class));
        given(jobDao.getJobById(job.getJobId())).willReturn(job);

        jobExecutor.cancelJobById(job.getJobId());
        assertEquals(State.CANCELLED, job.getState());

        then(jobDao).should(only()).getJobById(job.getJobId());
    }
}
