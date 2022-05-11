package transformer;

import dto.ScheduledJobInfo;
import entity.ScheduledJob;

public class ScheduledJobTransformer implements JobTransformer<ScheduledJobInfo, ScheduledJob>{
    @Override
    public ScheduledJob transform(ScheduledJobInfo jobInfo) {
        return ScheduledJob.builder()
                .withJobType(jobInfo.getJobType())
                .withSecondsToExecute(jobInfo.getSecondsToExecute())
                .withRepetitionPeriod(jobInfo.getRepetitionPeriod())
                .build();
    }
}
