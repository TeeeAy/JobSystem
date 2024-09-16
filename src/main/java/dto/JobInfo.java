package dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(setterPrefix = "with", toBuilder = true)
public abstract class JobInfo {

    private String jobType;

    private Runnable task;

}
