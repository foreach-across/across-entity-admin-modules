package com.foreach.across.testapplication.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.testapplication.application.domain.drink.Drink;
import com.foreach.across.testapplication.application.domain.drink.DrinkRepository;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Installer(name = "Drink installer", description = "Installs some default drinks.", phase = InstallerPhase.AfterModuleBootstrap)
@RequiredArgsConstructor
public class DrinkInstaller {
    private final DrinkRepository drinkRepository;

    @InstallerMethod
    public void installDefaultFood() {
        drinkRepository.saveAll(
                Arrays.asList(
                        new Drink(-1L, "Cola", false, null),
                        new Drink(-2L, "Fanta", false, null),
                        new Drink(-3L, "Sprite", false, null)
                )
        );
    }

}
