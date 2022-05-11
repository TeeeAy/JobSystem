package executor;

import dao.JobDao;
import dto.OneTimeJobInfo;
import entity.Job;
import entity.State;
import lombok.NoArgsConstructor;
import transformer.OneTimeJobTransformer;

import java.util.Calendar;
import java.util.concurrent.Future;

@NoArgsConstructor
public class OneTimeJobExecutor extends JobExecutor<OneTimeJobInfo>{

    private OneTimeJobTransformer oneTimeJobJobTransformer;

    @Override
    public Job executeJob(OneTimeJobInfo jobInfo) {
        Job job = oneTimeJobJobTransformer.transform(jobInfo);
        jobDao.saveJob(job);
        job.setState(State.PENDING);
        Future<?> future = executor.submit(job);
        job.setFuture(future);
        return job;
    }

    public OneTimeJobExecutor(JobDao jobDao, int jobsLimit,
                              OneTimeJobTransformer oneTimeJobJobTransformer ) {
        super(jobDao, jobsLimit);
        this.oneTimeJobJobTransformer = oneTimeJobJobTransformer;
    }

    public void cancelJobById(String jobId) {
        Job job = jobDao.getJobById(jobId);
        if (job.getState() == State.FINISHED || job.getState() == State.CANCELLED) {
            throw new JobCancellationException();
        }
        job.getFuture().cancel(true);
        job.setState(State.CANCELLED);
    }

}
