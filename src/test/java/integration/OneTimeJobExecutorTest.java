package integration;

import dao.JobDaoImpl;
import dto.OneTimeJobInfo;
import entity.Job;
import entity.State;
import executor.JobCancellationException;
import executor.OneTimeJobExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import transformer.OneTimeJobTransformer;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class OneTimeJobExecutorTest {

    private final static int JOBS_LIMIT = 2;

    private OneTimeJobExecutor jobExecutor;

    @BeforeEach
    void init() {
        jobExecutor = new OneTimeJobExecutor(new JobDaoImpl(), JOBS_LIMIT, new OneTimeJobTransformer());
    }

    @Test
    void shouldCancelJob() {
        int seconds = 2;
        OneTimeJobInfo oneTimeJobInfo = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withTask(() -> sleep(seconds))
                .build();
        Job job = jobExecutor.executeJob(oneTimeJobInfo);
        sleep(seconds / 2);
        jobExecutor.cancelJobById(job.getJobId());
        assertEquals(State.CANCELLED, job.getState());
    }

    @Test
    void shouldGetRunningJobsAmount() {
        int seconds = 6;
        OneTimeJobInfo oneTimeJobInfo1 = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withTask(() -> sleep(seconds))
                .build();
        OneTimeJobInfo oneTimeJobInfo2 = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withTask(() -> sleep(seconds))
                .build();
        Job job1 = jobExecutor.executeJob(oneTimeJobInfo1);
        Job job2 = jobExecutor.executeJob(oneTimeJobInfo2);
        sleep(seconds / 2);
        assertEquals(2, jobExecutor.getRunningJobsAmount());
        assertEquals(State.RUNNING, job1.getState());
        assertEquals(State.RUNNING, job2.getState());
    }

    @Test
    void shouldThrowJobCancellationExceptionOnFinishedJob() {
        int seconds = 4;
        OneTimeJobInfo oneTimeJobInfo = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withTask(() -> sleep(seconds))
                .build();
        Job job = jobExecutor.executeJob(oneTimeJobInfo);
        sleep(seconds + seconds / 2);
        JobCancellationException jobCancellationException =
                assertThrows(JobCancellationException.class,
                        () -> jobExecutor.cancelJobById(job.getJobId()));
        assertTrue(jobCancellationException
                .getMessage()
                .contains(JobCancellationException.DEFAULT_CANCELLATION_ERROR_MESSAGE));
    }



    @Test
    void shouldGetWaitingJobsAmount() {
        int seconds = 6;
        OneTimeJobInfo oneTimeJobInfo1 = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withTask(() -> sleep(seconds))
                .build();
        OneTimeJobInfo oneTimeJobInfo2 = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withTask(() -> sleep(seconds))
                .build();
        OneTimeJobInfo waitingJobInfo = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withTask(() -> sleep(seconds))
                .build();
        jobExecutor.executeJob(oneTimeJobInfo1);
        jobExecutor.executeJob(oneTimeJobInfo2);
        Job waitingJob = jobExecutor.executeJob(waitingJobInfo);
        sleep(seconds / 2);
        assertEquals(1, jobExecutor.getWaitingJobsAmount());
        assertEquals(State.PENDING, waitingJob.getState());
    }



    @Test
    void shouldExecuteJob() {
        int seconds = 2;
        OneTimeJobInfo oneTimeJobInfo = OneTimeJobInfo.builder()
                .withJobType("jobType1")
                .withTask(() -> sleep(seconds))
                .build();
        Job scheduledJob = jobExecutor.executeJob(oneTimeJobInfo);
        sleep(seconds + seconds / 2);
        assertEquals(State.FINISHED, scheduledJob.getState());
    }

    @AfterEach
    void tearDown() {
        jobExecutor.shutDown();
    }


    private static void sleep(int seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException ignored) {

        }
    }
}
