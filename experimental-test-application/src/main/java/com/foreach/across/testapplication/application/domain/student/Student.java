package com.foreach.across.testapplication.application.domain.student;

import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
public class Student extends SettableIdBasedEntity<com.foreach.across.testapplication.application.domain.student.Student> {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "seq_student")
    @GenericGenerator(
            name = "seq_student",
            strategy = AcrossSequenceGenerator.STRATEGY,
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_student"),
                    @org.hibernate.annotations.Parameter(name = "allocationSize", value = "1")
            }
    )
    private Long id;

    @Length(max = 50)
    private String name;

    private LocalDateTime lastModifiedDate;

    private LocalDateTime createdDate;

    private LocalDateTime enrollmentDate;
}
