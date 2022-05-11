package transformer;

import dto.JobInfo;
import entity.Job;

public interface JobTransformer<T extends JobInfo, R extends Job>{

    R transform(T jobInfo);

}
