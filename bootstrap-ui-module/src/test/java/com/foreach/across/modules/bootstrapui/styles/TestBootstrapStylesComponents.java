/*
 * Copyright 2019 the original author or authors
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

package com.foreach.across.modules.bootstrapui.styles;

import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.styles.TestBootstrapStylesUtilities.assertStyle;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://getbootstrap.com/docs/4.3/components
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
class TestBootstrapStylesComponents
{
	@Test
	void alert() {
		assertStyle( css.alert ).is( "alert" );
		assertStyle( css.alert.primary ).is( "alert", "alert-primary" ).removes( "alert-primary" );
		assertStyle( css.alert.secondary ).is( "alert", "alert-secondary" ).removes( "alert-secondary" );
		assertStyle( css.alert.success ).is( "alert", "alert-success" ).removes( "alert-success" );
		assertStyle( css.alert.danger ).is( "alert", "alert-danger" ).removes( "alert-danger" );
		assertStyle( css.alert.warning ).is( "alert", "alert-warning" ).removes( "alert-warning" );
		assertStyle( css.alert.info ).is( "alert", "alert-info" ).removes( "alert-info" );
		assertStyle( css.alert.light ).is( "alert", "alert-light" ).removes( "alert-light" );
		assertStyle( css.alert.dark ).is( "alert", "alert-dark" ).removes( "alert-dark" );

		// link color
		assertStyle( css.alert.link ).is( "alert-link" );

		// additional content
		assertStyle( css.alert.heading ).is( "alert-heading" );

		// dismissible
		assertStyle( css.alert.dismissible ).is( "alert-dismissible" );
	}

	@Test
	void badge() {
		assertStyle( css.badge ).is( "badge" );
		assertStyle( css.badge.primary ).is( "badge", "badge-primary" ).removes( "badge-primary" );
		assertStyle( css.badge.secondary ).is( "badge", "badge-secondary" ).removes( "badge-secondary" );
		assertStyle( css.badge.success ).is( "badge", "badge-success" ).removes( "badge-success" );
		assertStyle( css.badge.danger ).is( "badge", "badge-danger" ).removes( "badge-danger" );
		assertStyle( css.badge.warning ).is( "badge", "badge-warning" ).removes( "badge-warning" );
		assertStyle( css.badge.info ).is( "badge", "badge-info" ).removes( "badge-info" );
		assertStyle( css.badge.light ).is( "badge", "badge-light" ).removes( "badge-light" );
		assertStyle( css.badge.dark ).is( "badge", "badge-dark" ).removes( "badge-dark" );

		// pill badges
		assertStyle( css.badge.pill ).is( "badge-pill" );
	}

	@Test
	void breadcrumb() {
		assertStyle( css.breadcrumb ).is( "breadcrumb" );
		assertStyle( css.breadcrumb.item ).is( "breadcrumb-item" );
	}

	@Test
	void button() {
		assertStyle( css.button ).is( "btn" );
		assertStyle( css.button.primary ).is( "btn", "btn-primary" ).removes( "btn-primary" );
		assertStyle( css.button.secondary ).is( "btn", "btn-secondary" ).removes( "btn-secondary" );
		assertStyle( css.button.success ).is( "btn", "btn-success" ).removes( "btn-success" );
		assertStyle( css.button.danger ).is( "btn", "btn-danger" ).removes( "btn-danger" );
		assertStyle( css.button.warning ).is( "btn", "btn-warning" ).removes( "btn-warning" );
		assertStyle( css.button.info ).is( "btn", "btn-info" ).removes( "btn-info" );
		assertStyle( css.button.light ).is( "btn", "btn-light" ).removes( "btn-light" );
		assertStyle( css.button.dark ).is( "btn", "btn-dark" ).removes( "btn-dark" );
		assertStyle( css.button.link ).is( "btn", "btn-link" ).removes( "btn-link" );

		// outline buttons
		assertStyle( css.button.outline.primary ).is( "btn", "btn-outline-primary" ).removes( "btn-outline-primary" );
		assertStyle( css.button.outline.secondary ).is( "btn", "btn-outline-secondary" ).removes( "btn-outline-secondary" );
		assertStyle( css.button.outline.success ).is( "btn", "btn-outline-success" ).removes( "btn-outline-success" );
		assertStyle( css.button.outline.danger ).is( "btn", "btn-outline-danger" ).removes( "btn-outline-danger" );
		assertStyle( css.button.outline.warning ).is( "btn", "btn-outline-warning" ).removes( "btn-outline-warning" );
		assertStyle( css.button.outline.info ).is( "btn", "btn-outline-info" ).removes( "btn-outline-info" );
		assertStyle( css.button.outline.light ).is( "btn", "btn-outline-light" ).removes( "btn-outline-light" );
		assertStyle( css.button.outline.dark ).is( "btn", "btn-outline-dark" ).removes( "btn-outline-dark" );

		// size
		assertStyle( css.button.small ).is( "btn-sm" );
		assertStyle( css.button.large ).is( "btn-lg" );
		assertStyle( css.button.block ).is( "btn-block" );

		// checkbox and radio buttons
		assertStyle( css.button.group.toggle ).is( "btn-group-toggle" );
	}

	@Test
	void buttonGroup() {
		assertStyle( css.button.group ).is( "btn-group" );

		// button toolbar
		assertStyle( css.button.toolbar ).is( "btn-toolbar" );

		// sizing
		assertStyle( css.button.group.small ).is( "btn-group-sm" );
		assertStyle( css.button.group.large ).is( "btn-group-lg" );

		// vertical variation
		assertStyle( css.button.group.vertical ).is( "btn-group-vertical" );
	}

	@Test
	void card() {
		assertStyle( css.card ).is( "card" );

		// content types
		assertStyle( css.card.body ).is( "card-body" );
		assertStyle( css.card.title ).is( "card-title" );
		assertStyle( css.card.subTitle ).is( "card-subtitle" );
		assertStyle( css.card.text ).is( "card-text" );
		assertStyle( css.card.link ).is( "card-link" );
		assertStyle( css.card.header ).is( "card-header" );
		assertStyle( css.card.footer ).is( "card-footer" );

		// images
		assertStyle( css.card.image.top ).is( "card-img-top" );
		assertStyle( css.card.image.bottom ).is( "card-img-bottom" );
		assertStyle( css.card.image.overlay ).is( "card-img-overlay" );

		// layout
		assertStyle( css.card.group ).is( "card-group" );
		assertStyle( css.card.deck ).is( "card-deck" );
		assertStyle( css.card.columns ).is( "card-columns" );
	}

	@Test
	void carousel() {
		assertStyle( css.carousel ).is( "carousel" );
		assertStyle( css.carousel.inner ).is( "carousel-inner" );
		assertStyle( css.carousel.item ).is( "carousel-item" );

		// controls
		assertStyle( css.carousel.control.previous ).is( "carousel-control-prev" );
		assertStyle( css.carousel.control.previous.icon ).is( "carousel-control-prev-icon" );
		assertStyle( css.carousel.control.next ).is( "carousel-control-next" );
		assertStyle( css.carousel.control.next.icon ).is( "carousel-control-next-icon" );

		// indicators
		assertStyle( css.carousel.indicators ).is( "carousel-indicators" );

		// captions
		assertStyle( css.carousel.caption ).is( "carousel-caption" );

		// crossfade
		assertStyle( css.carousel.fade ).is( "carousel-fade" );
	}

	@Test
	void collapse() {
		assertStyle( css.collapse ).is( "collapse" );
		assertStyle( css.collapsed ).is( "collapsed" );
		assertStyle( css.collapsing ).is( "collapsing" );
		assertStyle( css.accordion ).is( "accordion" );
	}

	@Test
	void dropdown() {
		assertStyle( css.dropdown ).is( "dropdown" );
		assertStyle( css.dropdown.toggle ).is( "dropdown-toggle" );
		assertStyle( css.dropdown.menu ).is( "dropdown-menu" );
		assertStyle( css.dropdown.item ).is( "dropdown-item" );
		assertStyle( css.dropdown.divider ).is( "dropdown-divider" );

		// split button
		assertStyle( css.dropdown.toggle.split ).is( "dropdown-toggle-split" );

		// directions
		assertStyle( css.dropdown.direction.up ).is( "dropup" );
		assertStyle( css.dropdown.direction.right ).is( "dropright" );
		assertStyle( css.dropdown.direction.left ).is( "dropleft" );
		assertStyle( css.dropUp ).is( "dropup" );
		assertStyle( css.dropRight ).is( "dropright" );
		assertStyle( css.dropLeft ).is( "dropleft" );

		// items
		assertStyle( css.dropdown.item.text ).is( "dropdown-item-text" );

		// menu alignment
		assertStyle( css.dropdown.menu.right ).is( "dropdown-menu", "dropdown-menu-right" ).removes( "dropdown-menu-right" );
		assertStyle( css.dropdown.menu.right.onLargeAndUp() ).is( "dropdown-menu", "dropdown-menu-lg-right" ).removes( "dropdown-menu-lg-right" );
		assertStyle( css.dropdown.menu.left.onSmallAndUp() ).is( "dropdown-menu", "dropdown-menu-sm-left" ).removes( "dropdown-menu-sm-left" );

		// menu content
		assertStyle( css.dropdown.header ).is( "dropdown-header" );
	}

	@Test
	void form() {
		assertStyle( css.form.group ).is( "form-group" );
		assertStyle( css.form.text ).is( "form-text" );

		// form controls
		assertStyle( css.form.control ).is( "form-control" );
		assertStyle( css.form.control.file ).is( "form-control-file" );
		assertStyle( css.form.control.small ).is( "form-control-sm" );
		assertStyle( css.form.control.large ).is( "form-control-lg" );
		assertStyle( css.form.control.plainText ).is( "form-control-plaintext" );
		assertStyle( css.form.control.range ).is( "form-control-range" );

		// checkboxes and radios
		assertStyle( css.form.check ).is( "form-check" );
		assertStyle( css.form.check.input ).is( "form-check-input" );
		assertStyle( css.form.check.label ).is( "form-check-label" );
		assertStyle( css.form.check.inline ).is( "form-check-inline" );

		// layout
		assertStyle( css.form.row ).is( "form-row" );
		assertStyle( css.form.horizontal.label ).is( "col-form-label" );
		assertStyle( css.form.horizontal.label.small ).is( "col-form-label", "col-form-label-sm" ).removes( "col-form-label-sm" );
		assertStyle( css.form.horizontal.label.large ).is( "col-form-label", "col-form-label-lg" ).removes( "col-form-label-lg" );
		assertStyle( css.form.inline ).is( "form-inline" );

		// validation
		assertStyle( css.validation.needsValidation ).is( "needs-validation" );
		assertStyle( css.validation.wasValidated ).is( "was-validated" );
		assertStyle( css.validation.valid ).is( "is-valid" );
		assertStyle( css.validation.invalid ).is( "is-invalid" );
		assertStyle( css.valid ).is( "is-valid" );
		assertStyle( css.invalid ).is( "is-invalid" );
		assertStyle( css.validation.valid.feedback ).is( "valid-feedback" );
		assertStyle( css.validation.invalid.feedback ).is( "invalid-feedback" );
		assertStyle( css.validation.valid.tooltip ).is( "valid-tooltip" );
		assertStyle( css.validation.invalid.tooltip ).is( "invalid-tooltip" );

		// custom forms
		assertThat( css.custom ).isSameAs( css.form.custom );
		assertStyle( css.custom.control ).is( "custom-control" );
		assertStyle( css.custom.control.input ).is( "custom-control-input" );
		assertStyle( css.custom.control.label ).is( "custom-control-label" );
		assertStyle( css.custom.control.inline ).is( "custom-control-inline" );
		assertStyle( css.custom.checkbox ).is( "custom-control", "custom-checkbox" ).removes( "custom-checkbox" );
		assertStyle( css.custom.radio ).is( "custom-control", "custom-radio" ).removes( "custom-radio" );
		assertStyle( css.custom.switchControl ).is( "custom-control", "custom-switch" ).removes( "custom-switch" );
		assertStyle( css.custom.select ).is( "custom-select" );
		assertStyle( css.custom.select.large ).is( "custom-select", "custom-select-lg" ).removes( "custom-select-lg" );
		assertStyle( css.custom.select.small ).is( "custom-select", "custom-select-sm" ).removes( "custom-select-sm" );
		assertStyle( css.custom.range ).is( "custom-range" );
		assertStyle( css.custom.file ).is( "custom-file" );
		assertStyle( css.custom.file.input ).is( "custom-file-input" );
	}

	@Test
	void inputGroup() {
		assertStyle( css.inputGroup ).is( "input-group" );
		assertStyle( css.inputGroup.text ).is( "input-group-text" );
		assertStyle( css.inputGroup.prepend ).is( "input-group-prepend" );
		assertStyle( css.inputGroup.append ).is( "input-group-append" );
		assertStyle( css.inputGroup.large ).is( "input-group-lg" );
		assertStyle( css.inputGroup.small ).is( "input-group-sm" );
	}

	@Test
	void jumbotron() {
		assertStyle( css.jumbotron ).is( "jumbotron" );
		assertStyle( css.jumbotron.fluid ).is( "jumbotron", "jumbotron-fluid" ).removes( "jumbotron-fluid" );
		assertStyle( css.lead ).is( "lead" );
	}

	@Test
	void listGroup() {
		assertStyle( css.listGroup ).is( "list-group" );
		assertStyle( css.listGroup.flush ).is( "list-group-flush" );
		assertStyle( css.listGroup.item ).is( "list-group-item" );
		assertStyle( css.listGroup.item.action ).is( "list-group-item", "list-group-item-action" ).removes( "list-group-item-action" );

		// horizontal
		assertStyle( css.listGroup.horizontal ).is( "list-group-horizontal" );
		assertStyle( css.listGroup.horizontal.small ).is( "list-group-horizontal-sm" );
		assertStyle( css.listGroup.horizontal.medium ).is( "list-group-horizontal-md" );
		assertStyle( css.listGroup.horizontal.large ).is( "list-group-horizontal-lg" );
		assertStyle( css.listGroup.horizontal.extraLarge ).is( "list-group-horizontal-xl" );

		// contextual classes
		assertStyle( css.listGroup.item.primary ).is( "list-group-item", "list-group-item-primary" ).removes( "list-group-item-primary" );
		assertStyle( css.listGroup.item.danger ).is( "list-group-item", "list-group-item-danger" ).removes( "list-group-item-danger" );
		assertStyle( css.listGroup.item.light ).is( "list-group-item", "list-group-item-light" ).removes( "list-group-item-light" );

		assertStyle( css.listGroup.item.action.secondary ).is( "list-group-item", "list-group-item-action", "list-group-item-secondary" );
		assertStyle( css.listGroup.item.action.warning ).is( "list-group-item", "list-group-item-action", "list-group-item-warning" );
		assertStyle( css.listGroup.item.action.dark ).is( "list-group-item", "list-group-item-action", "list-group-item-dark" );
	}

	@Test
	void tab() {
		assertStyle( css.tab.content ).is( "tab-content" );
		assertStyle( css.tab.pane ).is( "tab-pane" );
	}

	@Test
	void media() {
		assertStyle( css.media ).is( "media" );
		assertStyle( css.media.body ).is( "media-body" );
	}

	@Test
	void modal() {
		assertStyle( css.modal ).is( "modal" );
		assertStyle( css.modal.dialog ).is( "modal-dialog" );
		assertStyle( css.modal.content ).is( "modal-content" );
		assertStyle( css.modal.header ).is( "modal-header" );
		assertStyle( css.modal.title ).is( "modal-title" );
		assertStyle( css.modal.body ).is( "modal-body" );
		assertStyle( css.modal.footer ).is( "modal-footer" );
		assertStyle( css.modal.small ).is( "modal-sm" );
		assertStyle( css.modal.large ).is( "modal-lg" );
		assertStyle( css.modal.extraLarge ).is( "modal-xl" );
		assertStyle( css.modal.dialog.scrollable ).is( "modal-dialog-scrollable" );
		assertStyle( css.modal.dialog.centered ).is( "modal-dialog-centered" );
		assertStyle( css.modal.dialog.small ).is( "modal-dialog", "modal-sm" ).removes( "modal-sm" );
		assertStyle( css.modal.dialog.large ).is( "modal-dialog", "modal-lg" ).removes( "modal-lg" );
		assertStyle( css.modal.dialog.extraLarge ).is( "modal-dialog", "modal-xl" ).removes( "modal-xl" );
	}

	@Test
	void nav() {
		assertStyle( css.nav ).is( "nav" );
		assertStyle( css.nav.item ).is( "nav-item" );
		assertStyle( css.nav.link ).is( "nav-link" );
		assertStyle( css.nav.tabs ).is( "nav", "nav-tabs" ).removes( "nav-tabs" );
		assertStyle( css.nav.pills ).is( "nav", "nav-pills" ).removes( "nav-pills" );
		assertStyle( css.nav.fill ).is( "nav-fill" );
		assertStyle( css.nav.justified ).is( "nav-justified" );
	}

	@Test
	void navbar() {
		assertStyle( css.navbar ).is( "navbar" );
		assertStyle( css.navbar.brand ).is( "navbar-brand" );
		assertStyle( css.navbar.text ).is( "navbar-text" );
		assertStyle( css.navbar.nav ).is( "navbar-nav" );
		assertStyle( css.navbar.toggler ).is( "navbar-toggler" );
		assertStyle( css.navbar.toggler.icon ).is( "navbar-toggler-icon" );
		assertStyle( css.navbar.collapse ).is( "collapse", "navbar-collapse" ).removes( "navbar-collapse" );
		assertStyle( css.navbar.light ).is( "navbar", "navbar-light" ).removes( "navbar-light" );
		assertStyle( css.navbar.dark ).is( "navbar", "navbar-dark" ).removes( "navbar-dark" );

		// expand
		assertStyle( css.navbar.expand ).is( "navbar-expand" );
		assertStyle( css.navbar.expand.onSmallAndUp() ).is( "navbar-expand-sm" );
		assertStyle( css.navbar.expand.onMediumAndUp() ).is( "navbar-expand-md" );
		assertStyle( css.navbar.expand.onLargeAndUp() ).is( "navbar-expand-lg" );
		assertStyle( css.navbar.expand.onExtraLargeAndUp() ).is( "navbar-expand-xl" );
	}

	@Test
	void pagination() {
		assertStyle( css.pagination ).is( "pagination" );
		assertStyle( css.page.item ).is( "page-item" );
		assertStyle( css.pagination.page.item ).is( "page-item" );
		assertStyle( css.page.link ).is( "page-link" );
		assertStyle( css.pagination.page.link ).is( "page-link" );
		assertStyle( css.pagination.small ).is( "pagination", "pagination-sm" ).removes( "pagination-sm" );
		assertStyle( css.pagination.large ).is( "pagination", "pagination-lg" ).removes( "pagination-lg" );
	}

	@Test
	void progress() {
		assertStyle( css.progress ).is( "progress" );
		assertStyle( css.progress.bar ).is( "progress-bar" );
		assertStyle( css.progress.bar.striped ).is( "progress-bar", "progress-bar-striped" ).removes( "progress-bar-striped" );
		assertStyle( css.progress.bar.animated ).is( "progress-bar", "progress-bar-striped", "progress-bar-animated" ).removes( "progress-bar-animated" );
	}

	@Test
	void spinner() {
		assertStyle( css.spinner.border ).is( "spinner-border" );
		assertStyle( css.spinner.grow ).is( "spinner-grow" );
		assertStyle( css.spinner.grow.small ).is( "spinner-grow", "spinner-grow-sm" ).removes( "spinner-grow-sm" );
	}

	@Test
	void toast() {
		assertStyle( css.toast ).is( "toast" );
		assertStyle( css.toast.header ).is( "toast-header" );
		assertStyle( css.toast.body ).is( "toast-body" );
	}
}
