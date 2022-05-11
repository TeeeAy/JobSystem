package integration;

import dao.JobDaoImpl;
import dto.ScheduledJobInfo;
import entity.Job;
import entity.State;
import executor.ScheduledJobExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import transformer.ScheduledJobTransformer;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduledJobExecutorTest {

    private final static int JOBS_LIMIT = 2;

    private ScheduledJobExecutor jobExecutor;

    @BeforeEach
    void init() {
        jobExecutor = new ScheduledJobExecutor(new JobDaoImpl(), JOBS_LIMIT, new ScheduledJobTransformer());
    }

    @Test
    void shouldCancelJob() {
        int seconds = 2;
        ScheduledJobInfo scheduledJobInfo = ScheduledJobInfo.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(seconds)
                .withRepetitionPeriod(70)
                .build();
        Job job = jobExecutor.executeJob(scheduledJobInfo);
        sleep(seconds / 2);
        jobExecutor.cancelJobById(job.getJobId());
        assertEquals(State.CANCELLED, job.getState());
    }

    @Test
    void shouldGetRunningJobsAmount() {
        int seconds = 6;
        ScheduledJobInfo scheduledJobInfo1 = ScheduledJobInfo.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(seconds)
                .withRepetitionPeriod(70)
                .build();
        ScheduledJobInfo scheduledJobInfo2 = ScheduledJobInfo.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(seconds)
                .withRepetitionPeriod(70)
                .build();
        Job job1 = jobExecutor.executeJob(scheduledJobInfo1);
        Job job2 = jobExecutor.executeJob(scheduledJobInfo2);
        sleep(seconds / 2);
        assertEquals(2, jobExecutor.getRunningJobsAmount());
        assertEquals(State.RUNNING, job1.getState());
        assertEquals(State.RUNNING, job2.getState());
    }


    @Test
    void shouldGetWaitingJobsAmount() {
        int seconds = 6;
        ScheduledJobInfo scheduledJobInfo1 = ScheduledJobInfo.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(seconds)
                .withRepetitionPeriod(70)
                .build();
        ScheduledJobInfo scheduledJobInfo2 = ScheduledJobInfo.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(seconds)
                .withRepetitionPeriod(70)
                .build();
        ScheduledJobInfo waitingJobInfo = ScheduledJobInfo.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(seconds)
                .withRepetitionPeriod(70)
                .build();
        jobExecutor.executeJob(scheduledJobInfo1);
        jobExecutor.executeJob(scheduledJobInfo2);
        Job waitingJob = jobExecutor.executeJob(waitingJobInfo);
        sleep(seconds / 2);
        assertEquals(1, jobExecutor.getWaitingJobsAmount());
        assertEquals(State.PENDING, waitingJob.getState());
    }



    @Test
    void shouldExecuteJob() {
        int seconds = 2;
        ScheduledJobInfo scheduledJobInfo = ScheduledJobInfo.builder()
                .withJobType("jobType1")
                .withSecondsToExecute(seconds)
                .withRepetitionPeriod(70)
                .build();
        Job scheduledJob = jobExecutor.executeJob(scheduledJobInfo);
        sleep(seconds + seconds / 2);
        assertEquals(State.IDLE, scheduledJob.getState());
    }

    @AfterEach
    void tearDown() {
        jobExecutor.shutDown();
    }


    private static void sleep(int seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
