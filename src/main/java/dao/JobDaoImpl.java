package dao;

import entity.Job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class JobDaoImpl implements JobDao {

    private final Map<String, Job> jobMap = new ConcurrentHashMap<>();

    @Override
    public void saveJob(Job job) {
        job.setJobId(UUID.randomUUID().toString());
        jobMap.put(job.getJobId(), job);
    }

    @Override
    public Job getJobById(String jobId) {
        Job job = jobMap.get(jobId);
        if (job == null) {
            throw new JobNotFoundException();
        }
        return job;
    }

    @Override
    public List<Job> getAllJobs() {
        return new ArrayList<>(jobMap.values());
    }


}
