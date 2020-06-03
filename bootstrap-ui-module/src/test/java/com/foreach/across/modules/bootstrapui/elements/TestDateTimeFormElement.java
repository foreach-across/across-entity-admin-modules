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

import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
public class TestDateTimeFormElement extends AbstractBootstrapViewElementTest
{
	private static final String DATA_ATTRIBUTE =
			"data-target-input=\"nearest\" data-bootstrapui-datetimepicker='{\"buttons\":{}," +
					"\"format\":\"L LT\",\"extraFormats\":[\"YYYY-MM-DD HH:mm\",\"L\",\"YYYY-MM-DD\"]," +
					"\"locale\":\"en-GB\",\"exportFormat\":\"YYYY-MM-DD HH:mm\"}' data-bootstrapui-adapter-type='datetime'";

	private DateTimeFormElement datetime;

	@Before
	public void before() {
		datetime = new DateTimeFormElement();
	}

	@Test
	public void emptyDateTime() {
		renderAndExpect(
				datetime,
				"<div class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						"<input data-target=\"\" type=\"text\" class=\"datetimepicker-input form-control\"></input>" +
						"<div class=\"input-group-append\">" +
						"<div data-target=\"\" data-toggle=\"datetimepicker\" class=\"input-group-text\"><i class=\"fas fa-calendar\"></i></div>\n" +
						"</div>" +
						"<input type='hidden' />" +
						"</div>"
		);
	}

	@Test
	public void withDateAndControlName() throws ParseException {
		datetime.setRequired( true );
		datetime.setControlName( "birthday" );

		Date date = DateUtils.parseDate( "2015-08-07 10:31", "yyyy-MM-dd HH:mm" );
		datetime.setValue( date );

		renderAndExpect(
				datetime,
				"<div id=\"_dp-controller--birthday\" class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						"<input data-target=\"#_dp-controller--birthday\" name=\"_birthday\" id=\"birthday\" type=\"text\" class=\"datetimepicker-input form-control\" required='required'" +
						" value='2015-08-07 10:31' />" +
						"<div class=\"input-group-append\">" +
						"<div data-target=\"#_dp-controller--birthday\" data-toggle=\"datetimepicker\" class=\"input-group-text\"><i class=\"fas fa-calendar\"></i></div>" +
						"</div>" +
						"<input type='hidden' name='birthday' value='2015-08-07 10:31' />" +
						"</div>"
		);
	}

	@Test
	public void customIcon() {
		datetime.setAppend( html.i( css.fa.brands( "clock" ) ) );

		renderAndExpect(
				datetime,
				"<div class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						"<input data-target=\"\" class='datetimepicker-input form-control' type='text' />" +
						"<div class=\"input-group-append\"><i data-target=\"\" data-toggle=\"datetimepicker\" class=\"fab fa-clock\"></i></div>" +
						"<input type='hidden' />" +
						"</div>"
		);
	}

	@Test
	public void withLocalDate() {
		LocalDate date = LocalDate.of( 2015, 8, 7 );
		datetime.setLocalDate( date );

		renderAndExpect(
				datetime,
				"<div class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						"<input data-target=\"\" type=\"text\" class=\"datetimepicker-input form-control\" value=\"2015-08-07 00:00\"></input>" +
						"<div class=\"input-group-append\">" +
						"<div data-target=\"\" data-toggle=\"datetimepicker\" class=\"input-group-text\"><i class=\"fas fa-calendar\"></i></div>" +
						"</div>" +
						"<input type='hidden' value='2015-08-07 00:00' />" +
						"</div>"
		);
	}

	@Test
	public void withLocalTime() {
		String today = LocalDate.now().format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
		LocalTime time = LocalTime.of( 10, 31 );
		datetime.setLocalTime( time );

		renderAndExpect(
				datetime,
				"<div class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						"<input data-target=\"\" type=\"text\" class=\"datetimepicker-input form-control\"" +
						" value='" + today + " 10:31' />" +
						"<div class=\"input-group-append\">" +
						"<div data-target=\"\" data-toggle=\"datetimepicker\" class=\"input-group-text\"><i class=\"fas fa-calendar\"></i></div>" +
						"</div>" +
						"<input type='hidden' value='" + today + " 10:31' />" +
						"</div>"
		);
	}

	@Test
	public void withLocalDateTime() throws ParseException {
		Date date = DateUtils.parseDate( "2015-08-07 10:31", "yyyy-MM-dd HH:mm" );
		LocalDateTime localDateTime = datetime.getConfiguration().dateToLocalDateTime( date );
		datetime.setLocalDateTime( localDateTime );

		renderAndExpect(
				datetime,
				"<div class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						"<input data-target=\"\" type=\"text\" class=\"datetimepicker-input form-control\" value=\"2015-08-07 10:31\"></input>" +
						"<div class=\"input-group-append\">" +
						"<div data-target=\"\" data-toggle=\"datetimepicker\" class=\"input-group-text\"><i class=\"fas fa-calendar\"></i></div>" +
						"</div>" +
						"<input type='hidden' value='2015-08-07 10:31' />" +
						"</div>"
		);
	}

	@Test
	public void dateAndLocalDateTimeAreEqual() throws ParseException {
		Date date = DateUtils.parseDate( "2015-08-07 10:31", "yyyy-MM-dd HH:mm" );
		LocalDateTime localDateTime = datetime.getConfiguration().dateToLocalDateTime( date );
		datetime.setLocalDateTime( localDateTime );

		String expectedContent = "<div class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
				"<input data-target=\"\" class='datetimepicker-input form-control' type='text' " +
				" value='2015-08-07 10:31' />" +
				"<div class='input-group-append'>" +
				"<div data-target=\"\" data-toggle=\"datetimepicker\" class=\"input-group-text\"><i class=\"fas fa-calendar\"></i></div>" +
				"</div>" +
				"<input type='hidden' value='2015-08-07 10:31' />" +
				"</div>";
		renderAndExpect( datetime, expectedContent );
		DateTimeFormElement dateTimeWithDate = new DateTimeFormElement();
		dateTimeWithDate.setValue( date );
		renderAndExpect( dateTimeWithDate, expectedContent );
	}

	@Test
	public void retrieveTheDateForASpecificClass() throws ParseException {
		Date date = DateUtils.parseDate( "2015-08-07 10:31", "yyyy-MM-dd HH:mm" );
		LocalDateTime localDateTime = datetime.getConfiguration().dateToLocalDateTime( date );
		datetime.setLocalDateTime( localDateTime );

		assertEquals( date, datetime.getValue() );
		assertEquals( localDateTime.toLocalDate(), datetime.getLocalDate() );
		assertEquals( localDateTime.toLocalTime(), datetime.getLocalTime() );
		assertEquals( localDateTime, datetime.getLocalDateTime() );
	}

	@Test
	public void updateControlName() {
		DateTimeFormElement control = datetime;
		control.setControlName( "one" );
		render( control );
		control.setControlName( "two" );
		renderAndExpect(
				control,
				"<div id=\"_dp-controller--one\" class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						" <input data-target=\"#_dp-controller--one\" name=\"_two\" id=\"two\" type=\"text\" class=\"datetimepicker-input form-control\"></input>" +
						"<div class=\"input-group-append\">" +
						"<div data-target=\"#_dp-controller--one\" data-toggle=\"datetimepicker\" class=\"input-group-text\"><i class=\"fas fa-calendar\"></i></div>" +
						"</div>" +
						"<input name='two' type='hidden' />" +
						"</div>"
		);

		assertEquals( "two", control.getControlName() );
	}

	@Test
	public void updateControlNameThroughContainer() {
		ContainerViewElement container = new ContainerViewElement();
		FormInputElement control = datetime;
		control.setControlName( "one" );
		render( control );
		container.addChild( control );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		renderAndExpect(
				control,
				"<div id=\"_dp-controller--one\" class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						"<input data-target=\"#_dp-controller--one\" class='datetimepicker-input form-control' type='text' name='_prefix.one' id='prefix.one' />" +
						"<div class=\"input-group-append\">" +
						"<div data-target=\"#_dp-controller--one\" data-toggle=\"datetimepicker\" class=\"input-group-text\"><i class=\"fas fa-calendar\"></i></div>" +
						"</div>" +
						"<input name='prefix.one' type='hidden' />" +
						"</div>"
		);

		assertEquals( "prefix.one", control.getControlName() );
	}

	@Test
	public void controlNamesAreEscapedToSupportJQuery() {
		ContainerViewElement container = new ContainerViewElement();
		FormInputElement control = datetime;

		String controlName = "properties[props].property[aPro#per@ty].properties[props:abc,def=ghi]";
		control.setControlName( controlName );
		render( control );
		container.addChild( control );

		String escapedControlName = "properties\\[props\\]\\.property\\[aPro\\#per\\@ty\\]\\.properties\\[props\\:abc\\,def\\=ghi\\]";

		renderAndExpect(
				control,
				"<div id=\"_dp-controller--" + controlName + "\" class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						"<input data-target=\"#_dp-controller--" + escapedControlName + "\" class='datetimepicker-input form-control' type='text' name='_" + controlName + "' id='" + controlName + "' />" +
						"<div class=\"input-group-append\">" +
						"<div data-target=\"#_dp-controller--" + escapedControlName + "\" data-toggle=\"datetimepicker\" class=\"input-group-text\"><i class=\"fas fa-calendar\"></i></div>" +
						"</div>" +
						"<input name='" + controlName + "' type='hidden' />" +
						"</div>"
		);

		assertEquals( control.getControlName(), controlName );
		assertEquals( control.getHtmlId(), "_dp-controller--" + controlName );
	}
}
