package com.foreach.across.modules.experimental.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
public class UserResource {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime birthDate;
}
