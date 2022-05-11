package executor;

import dao.JobDao;
import dto.ScheduledJobInfo;
import entity.Job;
import entity.OneTimeJob;
import entity.ScheduledJob;
import entity.State;
import executor.ScheduledJobExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import transformer.ScheduledJobTransformer;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledJobExecutorTest {

    @Mock
    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Mock
    JobDao jobDao;

    @Mock
    ScheduledJobTransformer jobTransformer;

    @InjectMocks
    ScheduledJobExecutor jobExecutor;


   @Test
    void shouldExecuteJob() {
        int seconds = 2;
        ScheduledJobInfo scheduledJobInfo = ScheduledJobInfo.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(seconds)
                .withRepetitionPeriod(70)
                .build();
        ScheduledJob job = new ScheduledJob("jobType1", 10, 70);
        ScheduledFuture<?> future = mock(ScheduledFuture.class);
        given(jobTransformer.transform(scheduledJobInfo)).willReturn(job);
        doReturn(future).when(scheduledThreadPoolExecutor).scheduleAtFixedRate(job, 0,
                job.getRepetitionPeriod(), TimeUnit.SECONDS);

        jobExecutor.executeJob(scheduledJobInfo);
        assertEquals(State.PENDING, job.getState());
        assertEquals(future, job.getFuture());

        then(jobDao).should(only()).saveJob(job);
        then(jobTransformer).should(only()).transform(scheduledJobInfo);
        then(scheduledThreadPoolExecutor).should(only()).scheduleAtFixedRate(job, 0,
                job.getRepetitionPeriod(), TimeUnit.SECONDS);
    }

    @Test
    void shouldCancelJob() {
        Job job = new ScheduledJob("jobType1", 10, 70);
        job.setFuture(mock(Future.class));
        given(jobDao.getJobById(job.getJobId())).willReturn(job);

        jobExecutor.cancelJobById(job.getJobId());
        assertEquals(State.CANCELLED, job.getState());

        then(jobDao).should(times(1)).getJobById(job.getJobId());
    }


    @Test
    void shouldThrowJobCancellationExceptionOnCancelledJob() {
        int seconds = 4;
        ScheduledJob job = new ScheduledJob("jobType1", seconds, 70);
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

}

