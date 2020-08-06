package com.foreach.across.testapplication.application.domain.food;

import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
public class Food extends SettableIdBasedEntity<Food>
{
	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_camashop__company")
	@GenericGenerator(
			name = "seq_camashop__company",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_camashop__company"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "1")
			}
	)
	private Long id;
	@NotBlank
	private String name;
	private FoodAction currentAction;
}
