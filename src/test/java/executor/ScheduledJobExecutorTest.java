package executor;

import dao.JobDao;
import dto.ScheduledJobInfo;
import entity.ScheduledJob;
import entity.State;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        Runnable runnable = () -> log.info("Test shouldExecuteJob");
        ScheduledJobInfo scheduledJobInfo = ScheduledJobInfo.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .withRepetitionPeriod(70)
                .build();
        ScheduledJob job = ScheduledJob.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .withRepetitionPeriod(70)
                .build();
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
        Runnable runnable = () -> log.info("Test shouldCancelJob");
        ScheduledJob job = ScheduledJob.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .withRepetitionPeriod(70)
                .build();
        job.setFuture(mock(Future.class));
        given(jobDao.getJobById(job.getJobId())).willReturn(job);

        jobExecutor.cancelJobById(job.getJobId());
        assertEquals(State.CANCELLED, job.getState());

        then(jobDao).should(times(1)).getJobById(job.getJobId());
    }


    @Test
    void shouldThrowJobCancellationExceptionOnCancelledJob() {
        Runnable runnable = () -> log.info("Test shouldThrowJobCancellationExceptionOnCancelledJob");
        ScheduledJob job = ScheduledJob.builder()
                .withJobType("jobType1")
                .withTask(runnable)
                .withRepetitionPeriod(70)
                .build();
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

