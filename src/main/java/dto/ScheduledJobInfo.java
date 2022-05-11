package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@AllArgsConstructor
@SuperBuilder(setterPrefix = "with", toBuilder = true)
public class ScheduledJobInfo extends JobInfo{

    private Integer repetitionPeriod;

}
