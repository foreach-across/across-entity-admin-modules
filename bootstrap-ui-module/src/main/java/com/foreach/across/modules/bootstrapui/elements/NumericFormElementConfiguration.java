/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.bootstrapui.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.number.CurrencyStyleFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * Configuration class for a {@link NumericFormElement} based on
 * <a href="https://github.com/BobKnothe/autoNumeric">JQuery autoNumeric plugin</a>.  The order in which properties
 * are set is important, as setting a format or currency will update several properties at once.
 *
 * @author Arne Vandamme
 */
public class NumericFormElementConfiguration extends HashMap<String, Object>
{
	/**
	 * Predefined formats defining decimal spaces, rounding methods, negative formatters and optionally a sign.
	 */
	public enum Format
	{
		NUMBER,
		PERCENT,
		CURRENCY
	}

	public static final String ROUND_HALF_UP_SYMMETRIC = "S";
	public static final String ROUND_HALF_UP_ASYMMETRIC = "A";
	public static final String ROUND_HALF_DOWN_SYMMETRIC = "s";
	public static final String ROUND_HALF_DOWN_ASYMMETRIC = "a";
	public static final String ROUND_HALF_EVEN = "B";
	public static final String ROUND_UP = "U";
	public static final String ROUND_DOWN = "D";
	public static final String ROUND_CEILING = "C";
	public static final String ROUND_FLOOR = "F";
	public static final String ROUND_SWISS = "CHF";

	public static final String NEGATIVE_PARENTHESES = "(,)";
	public static final String NEGATIVE_BRACKETS = "[,]";
	public static final String NEGATIVE_BRACES = "{,}";
	public static final String NEGATIVE_ANGLES = "<,>";

	public static final String EMPTY_EMPTY = "empty";
	public static final String EMPTY_ZERO = "zero";
	public static final String EMPTY_SIGN = "sign";

	@JsonIgnore
	private Format format = Format.NUMBER;

	@JsonIgnore
	private boolean localizeOutputFormat = true;

	@JsonIgnore
	private boolean localizeDecimalSymbols = true;

	@JsonIgnore
	private boolean forceWhitespaceAroundSign = false;

	@JsonIgnore
	private Currency currency;

	@JsonIgnore
	private Locale locale = LocaleContextHolder.getLocale();

	public NumericFormElementConfiguration() {
		setDecimalPositions( 2 );
		setMinValue( Long.MIN_VALUE );
	}

	public NumericFormElementConfiguration( Format format ) {
		this();
		setFormat( format );
	}

	public NumericFormElementConfiguration( Currency currency ) {
		this();
		setCurrency( currency );
	}

	public NumericFormElementConfiguration( Locale locale ) {
		this();
		setCurrency( Currency.getInstance( locale ), locale );
	}

	public NumericFormElementConfiguration( NumericFormElementConfiguration configuration ) {
		this.format = configuration.format;
		this.currency = configuration.currency;
		this.localizeDecimalSymbols = configuration.localizeDecimalSymbols;
		this.localizeOutputFormat = configuration.localizeOutputFormat;
		this.forceWhitespaceAroundSign = configuration.forceWhitespaceAroundSign;

		putAll( configuration );
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat( Format format ) {
		this.format = format;

		if ( format == Format.PERCENT ) {
			setNegativeFormatter( null );
			setRoundingMode( RoundingMode.HALF_UP );
			setSignPositionRight( true );
			setSign( " %" );
		}
		else if ( format == Format.CURRENCY ) {
			setNegativeFormatter( NEGATIVE_PARENTHESES );
			setRoundingMode( RoundingMode.HALF_EVEN );
			setSignPositionRight( false );
		}
		else {
			setRoundingMode( RoundingMode.HALF_UP );
			setNegativeFormatter( null );
			setSign( null );
		}
	}

	public void setCurrency( Currency currency ) {
		setCurrency( currency, LocaleContextHolder.getLocale() );
	}

	/**
	 * Set currency to use and locale for which to retrieve the currency signal.
	 *
	 * @param currency instance
	 * @param locale   to use the currency symbol from
	 */
	public void setCurrency( Currency currency, Locale locale ) {
		this.currency = currency;

		if ( currency != null ) {
			setFormat( Format.CURRENCY );

			CurrencyStyleFormatter currencyFormatter = new CurrencyStyleFormatter();
			currencyFormatter.setCurrency( currency );
			currencyFormatter.setFractionDigits( 0 );

			String printed = currencyFormatter.print( 123, locale );

			setDecimalPositions( currency.getDefaultFractionDigits() );

			setSignPositionRight( StringUtils.startsWith( printed, "123" ) );
			setSign( StringUtils.replace( printed, "123", "" ) );
		}
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setDecimalPositions( int positions ) {
		put( "mDec", positions );
	}

	public void setGroupingSeparator( Character separator ) {
		put( "aSep", separator == null ? "" : separator );
	}

	public void setGroupingSize( int size ) {
		put( "dGroup", size );
	}

	public void setDecimalSeparator( char separator ) {
		put( "aDec", separator );
	}

	public void setSign( String sign ) {
		String actualSign = sign;

		if ( sign != null && isForceWhitespaceAroundSign() ) {
			if ( Objects.equals( 's', get( "pSign" ) ) ) {
				actualSign = " " + StringUtils.trim( sign );
			}
			else {
				actualSign = StringUtils.trim( sign ) + " ";
			}
		}

		put( "aSign", actualSign );
	}

	public void setSignPositionRight( boolean positionRight ) {
		put( "pSign", positionRight ? 's' : 'p' );
	}

	public void setMinValue( Number minValue ) {
		put( "vMin", minValue );
	}

	public void setMaxValue( Number maxValue ) {
		put( "vMax", maxValue );
	}

	public void setRoundingMode( RoundingMode roundingMode ) {
		setRoundingMode( convertJavaRoundingMode( roundingMode ) );
	}

	private String convertJavaRoundingMode( RoundingMode roundingMode ) {
		switch ( roundingMode ) {
			case CEILING:
				return ROUND_CEILING;
			case FLOOR:
				return ROUND_FLOOR;
			case DOWN:
				return ROUND_DOWN;
			case HALF_DOWN:
				return ROUND_HALF_DOWN_SYMMETRIC;
			case HALF_UP:
				return ROUND_HALF_UP_SYMMETRIC;
			case HALF_EVEN:
				return ROUND_HALF_EVEN;
			case UP:
				return ROUND_UP;
		}

		return ROUND_HALF_UP_SYMMETRIC;
	}

	private RoundingMode toJavaRoundingMode( String roundingMode ) {
		if ( ROUND_CEILING.equals( roundingMode ) ) {
			return RoundingMode.CEILING;
		}
		if ( ROUND_FLOOR.equals( roundingMode ) ) {
			return RoundingMode.FLOOR;
		}
		if ( ROUND_DOWN.equals( roundingMode ) ) {
			return RoundingMode.DOWN;
		}
		if ( ROUND_HALF_DOWN_SYMMETRIC.equals( roundingMode ) ) {
			return RoundingMode.HALF_DOWN;
		}
		if ( ROUND_HALF_UP_SYMMETRIC.equals( roundingMode ) ) {
			return RoundingMode.HALF_UP;
		}
		if ( ROUND_HALF_EVEN.equals( roundingMode ) ) {
			return RoundingMode.HALF_EVEN;
		}
		if ( ROUND_UP.equals( roundingMode ) ) {
			return RoundingMode.UP;
		}

		return RoundingMode.HALF_UP;
	}

	/**
	 * Set the multiplier to apply to the value before formatting and when posting back.
	 * Useful if percentage values are below 1 as the javascript control expects them between 0 and 100.
	 *
	 * @param multiplier defaults to 1
	 */
	public void setMultiplier( int multiplier ) {
		put( "multiplier", multiplier );
	}

	public int getMultiplier() {
		return (Integer) getOrDefault( "multiplier", 1 );
	}

	public void setRoundingMode( String mode ) {
		put( "mRound", mode );
	}

	public void setDecimalPadding( boolean padding ) {
		put( "aPad", padding );
	}

	/**
	 * Set the format to be used when rendering negative values if the texbox is not in focus.
	 * See for example {@link #NEGATIVE_BRACES}.
	 *
	 * @param formatter string
	 */
	public void setNegativeFormatter( String formatter ) {
		put( "nBracket", formatter );
	}

	public void setEmptyBehaviour( String behaviour ) {
		put( "wEmpty", behaviour );
	}

	public boolean isLocalizeOutputFormat() {
		return localizeOutputFormat;
	}

	/**
	 * Set to {@code true} if the output format for currency or percentage should be determined using the output locale,
	 * this could modify both position and symbol for both currency and percent.  Default is {@code true}.
	 *
	 * @param localizeOutputFormat true if parameters can be modified according to output locale
	 */
	public void setLocalizeOutputFormat( boolean localizeOutputFormat ) {
		this.localizeOutputFormat = localizeOutputFormat;
	}

	public boolean isLocalizeDecimalSymbols() {
		return localizeDecimalSymbols;
	}

	/**
	 * Set to {@code true} if you want to have the decimal symbols modified when calling {@link #localize(Locale)}.
	 *
	 * @param localizeDecimalSymbols true if decimal symbols should be gotten from the locale
	 */
	public void setLocalizeDecimalSymbols( boolean localizeDecimalSymbols ) {
		this.localizeDecimalSymbols = localizeDecimalSymbols;
	}

	public boolean isForceWhitespaceAroundSign() {
		return forceWhitespaceAroundSign;
	}

	/**
	 * Ensure that whitespace is added between the sign and the number itself, no matter the localized format.
	 *
	 * @param forceWhitespaceAroundSign true if space will be introduced
	 */
	public void setForceWhitespaceAroundSign( boolean forceWhitespaceAroundSign ) {
		this.forceWhitespaceAroundSign = forceWhitespaceAroundSign;
	}

	/**
	 * <p>Create a localized version of this configuration.  Meaning that number formatting specifications (decimal symbol,
	 * thousands separator etc) will be used from the current locale.</p>
	 * <p>
	 * This is different from the {@link #NumericFormElementConfiguration(Locale)} constructor as that one
	 * creates a configuration for the currency attached to the locale specified.  Even though the currency could be
	 * USD, you usually still want to render in the output locale.</p>
	 * <p>Properties {@link #isLocalizeOutputFormat()} and {@link #isLocalizeDecimalSymbols()} will determine which
	 * settings this method will modify.</p>
	 *
	 * @param locale instance
	 * @return clone with altered settings if {@link #isLocalizeOutputFormat()} is {@code true}
	 */
	public NumericFormElementConfiguration localize( Locale locale ) {
		NumericFormElementConfiguration clone = new NumericFormElementConfiguration( this );
		clone.locale = locale;

		if ( isLocalizeDecimalSymbols() ) {
			DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance( locale );
			clone.setDecimalSeparator(
					format == Format.CURRENCY ? symbols.getMonetaryDecimalSeparator() : symbols.getDecimalSeparator()
			);
			if ( !"".equals( clone.get( "aSep" ) ) ) {
				clone.setGroupingSeparator( symbols.getGroupingSeparator() );
			}
		}

		if ( isLocalizeOutputFormat() ) {
			if ( format == Format.CURRENCY ) {
				Currency currency = this.currency;

				if ( currency == null ) {
					currency = Currency.getInstance( locale );
					clone.setCurrency( currency );
				}

				NumberFormat numberFormat = NumberFormat.getCurrencyInstance( locale );
				numberFormat.setMaximumFractionDigits( 0 );
				numberFormat.setCurrency( currency );

				String printed = numberFormat.format( 123 );
				clone.sign( StringUtils.replace( printed, "123", "" ), StringUtils.startsWith( printed, "123" ) );
			}
			else if ( format == Format.PERCENT ) {
				NumberFormat numberFormat = NumberFormat.getPercentInstance( locale );
				numberFormat.setMaximumFractionDigits( 0 );

				String printed = numberFormat.format( new BigDecimal( "0.11" ) );
				clone.sign( StringUtils.replace( printed, "11", "" ), StringUtils.startsWith( printed, "11" ) );
			}
		}

		return clone;
	}

	private void sign( String sign, boolean positionRight ) {
		setSignPositionRight( positionRight );
		setSign( sign );
	}

	@Override
	public Object put( String key, Object value ) {
		if ( value == null ) {
			return remove( key );
		}
		else {
			return super.put( key, value );
		}
	}

	/**
	 * Create a {@link NumberFormat} that represents the same configuration.
	 *
	 * @return numberformat instance
	 */
	public NumberFormat createNumberFormat() {
		DecimalFormat format = new DecimalFormat();
		format.setGroupingSize( (Integer) getOrDefault( "dGroup", 3 ) );
		format.setGroupingUsed( !"".equals( get( "aSep" ) ) );
		format.setMaximumFractionDigits( (Integer) get( "mDec" ) );
		format.setMinimumFractionDigits( format.getMaximumFractionDigits() );
		format.setRoundingMode( toJavaRoundingMode( (String) get( "mRound" ) ) );
		format.setMultiplier( getMultiplier() );

		DecimalFormatSymbols symbols = new DecimalFormatSymbols( locale );
		symbols.setGroupingSeparator( format.isGroupingUsed() ? (Character) getOrDefault( "aSep", ',' ) : ',' );
		symbols.setDecimalSeparator( (Character) getOrDefault( "aDec", '.' ) );

		String sign = (String) get( "aSign" );

		if ( sign != null ) {
			boolean signSuffix = 's' == ( (Character) getOrDefault( "pSign", 'p' ) );
			if ( signSuffix ) {
				format.setPositiveSuffix( sign );
				format.setNegativeSuffix( sign );
			}
			else {
				format.setPositivePrefix( sign );
				format.setPositivePrefix( sign );
			}
		}

		String negativeFormatter = (String) get( "nBracket" );

		if ( negativeFormatter != null ) {
			symbols.setMinusSign( '+' );
			format.setNegativePrefix( negativeFormatter.split( "," )[0] + format.getPositivePrefix() );
			format.setNegativeSuffix( format.getPositiveSuffix() + negativeFormatter.split( "," )[1] );
		}

		format.setDecimalFormatSymbols( symbols );

		return format;
	}

	/**
	 * Create a localizable configuration for percentages.
	 *
	 * @param decimalPositions          number of decimal positions to show
	 * @param forceWhitespaceAroundSign true if whitespace should be added around the sign independent of the locale
	 * @return configuration
	 */
	public static NumericFormElementConfiguration percent( int decimalPositions, boolean forceWhitespaceAroundSign ) {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration( Format.PERCENT );
		configuration.setDecimalPositions( decimalPositions );
		configuration.setForceWhitespaceAroundSign( forceWhitespaceAroundSign );

		return configuration;
	}

	/**
	 * Create a localizable configuration for a given currency.
	 *
	 * @param currency                  currency to use
	 * @param decimalPositions          number of decimal positions to show
	 * @param forceWhitespaceAroundSign true if whitespace should be added around the sign independent of the locale
	 * @return configuration
	 */
	public static NumericFormElementConfiguration currency( Currency currency,
	                                                        int decimalPositions,
	                                                        boolean forceWhitespaceAroundSign ) {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		configuration.setForceWhitespaceAroundSign( forceWhitespaceAroundSign );
		configuration.setCurrency( currency );
		configuration.setDecimalPositions( decimalPositions );

		return configuration;
	}
}
