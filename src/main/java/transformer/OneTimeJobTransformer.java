package transformer;

import dto.OneTimeJobInfo;
import entity.OneTimeJob;

public class OneTimeJobTransformer implements JobTransformer<OneTimeJobInfo, OneTimeJob>{
    @Override
    public OneTimeJob transform(OneTimeJobInfo jobInfo) {
        return OneTimeJob.builder()
                .withJobType(jobInfo.getJobType())
                .withSecondsToExecute(jobInfo.getSecondsToExecute())
                .build();
    }
}
