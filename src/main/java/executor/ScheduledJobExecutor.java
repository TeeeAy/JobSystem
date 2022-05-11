package executor;

import dao.JobDao;
import dto.ScheduledJobInfo;
import entity.Job;
import entity.ScheduledJob;
import entity.State;
import lombok.NoArgsConstructor;
import transformer.ScheduledJobTransformer;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public class ScheduledJobExecutor extends JobExecutor<ScheduledJobInfo>{

    private ScheduledJobTransformer scheduledJobTransformer;

    @Override
    public Job executeJob(ScheduledJobInfo jobInfo) {
        ScheduledJob job = scheduledJobTransformer.transform(jobInfo);
        jobDao.saveJob(job);
        job.setState(State.PENDING);
        Future<?> future = executor.scheduleAtFixedRate(job, 0,
                job.getRepetitionPeriod(), TimeUnit.SECONDS);
        job.setFuture(future);
        return job;
    }


    public ScheduledJobExecutor(JobDao jobDao, int jobsLimit, ScheduledJobTransformer scheduledJobTransformer) {
        super(jobDao, jobsLimit);
        this.scheduledJobTransformer  = scheduledJobTransformer;
    }

    @Override
    public void cancelJobById(String jobId) {
        Job job = jobDao.getJobById(jobId);
        if (job.getState() == State.CANCELLED) {
            throw new JobCancellationException();
        }
        job.getFuture().cancel(true);
        job.setState(State.CANCELLED);
    }
}
