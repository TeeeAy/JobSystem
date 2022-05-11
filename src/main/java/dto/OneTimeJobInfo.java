package dto;


import lombok.experimental.SuperBuilder;


@SuperBuilder(setterPrefix = "with", toBuilder = true)
public class OneTimeJobInfo  extends JobInfo{
}
