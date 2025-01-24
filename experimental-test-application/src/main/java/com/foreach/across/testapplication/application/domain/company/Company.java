package com.foreach.across.testapplication.application.domain.company;

//import com.foreach.across.modules.filemanager.business.reference.FileReference;
import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
public class Company extends SettableIdBasedEntity<Company>
{
	@Id
	@GeneratedValue(generator = "seq_company_id")
	@GenericGenerator(
			name = "seq_company_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_company_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "1")
			}
	)
	private Long id;

	@Column
	@NotBlank
	private String name;

	/*
	@ManyToOne
	@JoinColumn(name = "work_regulations", referencedColumnName = "id")
	private FileReference workRegulations;
	*/
}
