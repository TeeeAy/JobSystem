package dao;

import entity.Job;

import java.util.List;

public interface JobDao {

    void saveJob(Job job);

    Job getJobById(String jobId);

    List<Job> getAllJobs();

}
