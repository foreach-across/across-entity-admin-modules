package com.foreach.across.testapplication.application.domain.drink;

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
public class Drink extends SettableIdBasedEntity<Drink> {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "seq_drink")
    @GenericGenerator(
            name = "seq_drink",
            strategy = AcrossSequenceGenerator.STRATEGY,
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_drink"),
                    @org.hibernate.annotations.Parameter(name = "allocationSize", value = "1")
            }
    )
    private Long id;

    @NotBlank
    private String name;

    private Boolean containsAlcohol;

    private String alcoholPercentage;

}
