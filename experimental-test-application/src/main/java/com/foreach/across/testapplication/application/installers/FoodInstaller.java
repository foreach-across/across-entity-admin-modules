package com.foreach.across.testapplication.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.testapplication.application.domain.food.Food;
import com.foreach.across.testapplication.application.domain.food.FoodRepository;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Installer(name = "Food installer", description = "Installs some default food.", phase = InstallerPhase.AfterModuleBootstrap)
@RequiredArgsConstructor
public class FoodInstaller
{
	private final FoodRepository foodRepository;

	@InstallerMethod
	public void installDefaultFood() {
		foodRepository.saveAll(
				Arrays.asList(
						new Food( -1L, "Pizza margherita" ),
						new Food( -2L, "Pizza Hawaï" ),
						new Food( -3L, "Hamburger" ),
						new Food( -4L, "Hotdog" )
				)
		);
	}

}
