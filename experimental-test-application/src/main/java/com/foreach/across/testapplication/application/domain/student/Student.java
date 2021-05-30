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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
public class Student extends SettableIdBasedEntity<com.foreach.across.testapplication.application.domain.student.Student>
{
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

	private LocalDate createdDate;

	private Date enrollmentDate;
	private LocalDate firstClassJoinedDate;
	private LocalTime startsStudyingAt;
	private LocalTime stopStudingAt;
}
