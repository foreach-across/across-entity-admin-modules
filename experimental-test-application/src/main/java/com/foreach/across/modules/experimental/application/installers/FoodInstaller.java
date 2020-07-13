package com.foreach.across.modules.experimental.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.experimental.application.domain.food.Food;
import com.foreach.across.modules.experimental.application.domain.food.FoodRepository;
import lombok.RequiredArgsConstructor;

@Installer(name = "Food installer", description = "Installs some default food.", phase = InstallerPhase.AfterModuleBootstrap)
@RequiredArgsConstructor
public class FoodInstaller
{
	private final FoodRepository foodRepository;

	@InstallerMethod
	public void installDefaultFood() {
		Food pizza = new Food( -1L, "Pizza" );
		Food hamburger = new Food( -2L, "Hamburger" );

		foodRepository.save( pizza );
		foodRepository.save( hamburger );
	}

}
