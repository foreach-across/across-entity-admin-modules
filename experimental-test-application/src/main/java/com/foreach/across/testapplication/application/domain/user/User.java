package com.foreach.across.testapplication.application.domain.user;

import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import com.foreach.across.testapplication.application.domain.company.Company;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_")
@EqualsAndHashCode(of = "id", callSuper = false)
public class User extends SettableIdBasedEntity<User>
{
	@Id
	@GeneratedValue(generator = "seq_user_id")
	@GenericGenerator(
			name = "seq_user_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_user_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "1")
			}
	)
	private Long id;

	@Column
	private LocalDate dateOfBirth;

	@Column
	@NotBlank
	private String name;

	@Column
	@Length(max = 255)
	@Email
	private String email;

	@ElementCollection
	private List<Address> address;

	@ElementCollection
	private Set<Degree> degrees;

	@ManyToOne
	@JoinColumn(name = "company_id")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "mentor_id")
	private User mentor;

	@Column
	@NumberFormat(style = NumberFormat.Style.CURRENCY)
	private BigDecimal netValue;

	@Column
	private Boolean active;

}
