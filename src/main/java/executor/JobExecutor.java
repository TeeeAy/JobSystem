package executor;

import dao.JobDao;
import dto.JobInfo;
import entity.Job;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@NoArgsConstructor
public abstract class JobExecutor<T extends JobInfo> {

    protected JobDao jobDao;

    protected ScheduledThreadPoolExecutor executor;

    public JobExecutor(JobDao jobDao, int jobsLimit) {
        this.jobDao = jobDao;
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(
                jobsLimit,
                new ThreadPoolExecutor.CallerRunsPolicy());
        scheduledThreadPoolExecutor.setMaximumPoolSize(jobsLimit);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        executor = scheduledThreadPoolExecutor;
    }

    public abstract Job executeJob(T job);

    public abstract void cancelJobById(String jobId);


    public void shutDown() {
        executor.shutdown();
    }

    public int getRunningJobsAmount() {
        return executor.getActiveCount();
    }

    public int getWaitingJobsAmount() {
        return executor.getQueue().size();
    }

    public List<Job> getAllJobs() {
        return jobDao.getAllJobs();
    }

}
