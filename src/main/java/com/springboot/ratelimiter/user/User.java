package com.springboot.ratelimiter.user;

import com.springboot.ratelimiter.common.model.BaseDomainModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseDomainModel {

    private String id;
    private String name;
    private String email;

}
