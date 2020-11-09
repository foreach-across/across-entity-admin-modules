package com.foreach.across.modules.experimental.webutility.support;

import com.foreach.across.modules.bootstrapui.elements.NumericFormElementConfiguration;

import java.util.Currency;

/**
 * Can be set on an entityConfiguration
 * <pre>{@code
 * .attribute( NumericFormElementConfiguration.class, getEuroConfiguration() )
 * }</pre>
 */
public class NumericFormElementConfigurations {

    public static NumericFormElementConfiguration numericConfigurationWithoutFormatting() {
        NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
        configuration.setGroupingSeparator( null );
        return configuration;
    }

    public static NumericFormElementConfiguration amountNumericConfiguration() {
        NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
        configuration.setDecimalSeparator( ',' );
        configuration.setGroupingSeparator( '.' );
        configuration.setLocalizeDecimalSymbols( false );
        configuration.setDecimalPositions( 0 );
        return configuration;
    }

    public static NumericFormElementConfiguration getEuroConfiguration() {
        return currencyFormConfiguration( "EUR" );
    }

    public static NumericFormElementConfiguration currencyFormConfiguration( String currencyCode ) {
        NumericFormElementConfiguration configuration = NumericFormElementConfiguration.currency( Currency.getInstance( currencyCode ), 2, true );
        configuration.setDecimalPositions( 2 );
        configuration.setDecimalSeparator( ',' );
        configuration.setGroupingSeparator( '.' );
        configuration.setLocalizeDecimalSymbols( false );
        return configuration;
    }

    public static NumericFormElementConfiguration percentConfiguration(int decimalPositions, boolean forceWhitespaceAroundSign) {
        NumericFormElementConfiguration configuration = new NumericFormElementConfiguration(NumericFormElementConfiguration.Format.PERCENT);
        configuration.setDecimalPositions(decimalPositions);
        configuration.setForceWhitespaceAroundSign(forceWhitespaceAroundSign);
        configuration.setDecimalSeparator( ',' );
        return configuration;
    }
}