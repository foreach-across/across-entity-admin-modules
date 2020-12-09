context( "Editable value tests (User / Company entities)", () => {

    function navigateToUser( name, toDetailPage ) {
        cy.contains( name )
                .closest( 'tr' )
                .find( '[data-em-button-role="edit"]' )
                .click();
        if ( toDetailPage ) {
            cy.url()
                    .then( url => {
                        cy.visit( url.substr( 0, url.length - "/update".length ) )
                    } );
        }
        cy.wait( 150 );
    }

    function property( property ) {
        return cy.get( `[data-em-property='${property}']` );
    }

    function valueModeOfProperty( property ) {
        return property.find( "[data-em-editable-value-role='value']" )
    }

    function openControl( property ) {
        property.find( ".cta-item.cta-edit" )
                .click( {force: true} );

        property.closest( '[data-em-property]' )
                .find( "[data-editable-value-control='true']" )
                .should( 'be.visible' );
    }

    function submitControl( property ) {
        property.find( "[data-action='save']" )
                .click();
    }

    beforeEach( () => {
        cy.login( 'admin' );

        cy.intercept( "POST", new RegExp( "\\/admin\\/entities\\/user\\/.*\\/update\\?view=editableValues" ), ( req ) => {
            console.log( "Control submit has completed" );
        } ).as( "ajaxEditableValueSubmit" );
    } );

    const singleEntityPageTests = function( name ) {

        const nameReplacement = name.substring( 0, 2 ) + 1 + name.substring( 2 );

        it( "Datepicker", () => {
            const datepicker = () => property( "dateOfBirth" );
            valueModeOfProperty( datepicker() )
                    .should( "be.visible" )
                    .should( "not.have.value", "1 Jan 2019" );

            openControl( datepicker() );

            datepicker().find( "input.form-control" )
                    .clear()
                    .type( "01/01/2019" )
                    .should( "have.value", "01/01/2019" )
                    .blur();

            submitControl( datepicker() );
            cy.wait( "@ajaxEditableValueSubmit" );

            valueModeOfProperty( datepicker() )
                    .should( "be.visible" )
                    .contains( "1 Jan 2019" );

        } );

        it( "Single line text", () => {
            const textInput = () => property( "email" );
            valueModeOfProperty( textInput() )
                    .should( "be.visible" )
                    .contains( name + "@local" );
            openControl( textInput() );

            textInput().find( "input.form-control" )
                    .clear()
                    .type( nameReplacement + "@local" )
                    .should( "have.value", nameReplacement + "@local" );

            submitControl( textInput() );
            cy.wait( "@ajaxEditableValueSubmit" );

            valueModeOfProperty( textInput() )
                    .should( "be.visible" )
                    .contains( nameReplacement + "@local" );

            openControl( textInput() );

            textInput().find( "input.form-control" )
                    .clear()
                    .type( name + "@local" )
                    .should( "have.value", name + "@local" )
                    .blur();

            submitControl( textInput() );
            cy.wait( "@ajaxEditableValueSubmit" );

            valueModeOfProperty( textInput() )
                    .should( "be.visible" )
                    .contains( name + "@local" );

            // openControl( textInput() );
            //
            // // type {enter} triggers a cy.submit() which then complains about nested forms
            // textInput().find( "input.form-control" )
            //         .clear()
            //         .type( "John@local{enter}" );
            //
            // cy.wait( "@ajaxEditableValueSubmit" );
            //
            // valueModeOfProperty( textInput() )
            //         .should( "be.visible" )
            //         .contains( "John@local" );
        } );

        it( "Embedded collection", () => {

        } );

        it( "Multi-checkbox", () => {
            const checkboxList = () => property( "degrees" );
            valueModeOfProperty( checkboxList() )
                    .contains( "Bachelor" );

            openControl( checkboxList() );

            checkboxList()
                    .find( "[data-editable-value-control='true']" )
                    .contains( "Master" )
                    .closest( ".custom-checkbox" )
                    .find( "input.custom-control-input" )
                    .check( {force: true} )
                    .should( "be.checked" );

            submitControl( checkboxList() );
            cy.wait( "@ajaxEditableValueSubmit" );

            valueModeOfProperty( checkboxList() )
                    .should( "be.visible" )
                    .contains( "Bachelor, Master" );
        } );

        it( "Autosuggest", () => {
            const autosuggest = () => property( "mentor" );
            valueModeOfProperty( autosuggest() )
                    .should( "be.visible" )
                    .should( "have.value", "" );
            openControl( autosuggest() );

            autosuggest().find( "input.tt-input" )
                    .clear()
                    .type( "Debo" )
                    .should( "have.value", "Debo" );

            autosuggest().find( ".tt-menu" )
                    .contains( "Debo" )
                    .click();

            submitControl( autosuggest() );
            cy.wait( "@ajaxEditableValueSubmit" );

            valueModeOfProperty( autosuggest() )
                    .should( "be.visible" )
                    .contains( "Deborah" );
        } );

        it( "Numeric", () => {
            const numericInput = () => property( "netValue" );
            valueModeOfProperty( numericInput() )
                    .should( "be.visible" )
                    .contains( "123.00" );
            openControl( numericInput() );

            numericInput().find( "input.form-control" )
                    .clear()
                    .type( "9875" )
                    .should( "have.value", "£9,875" )
                    .blur();

            submitControl( numericInput() );
            cy.wait( "@ajaxEditableValueSubmit" );

            valueModeOfProperty( numericInput() )
                    .should( "be.visible" )
                    .contains( "9,875" );

            // openControl( numericInput() );
            //
            // numericInput().find( "input.form-control" )
            //         .clear()
            //         .type( "123{enter}" );
            //
            // cy.wait( "@ajaxEditableValueSubmit" );
            //
            // valueModeOfProperty( numericInput() )
            //         .should( "be.visible" )
            //         .contains( "123" );
        } );

        it( "Invalid content renders validation error", () => {
            const textInput = () => property( "email" );
            valueModeOfProperty( textInput() )
                    .should( "be.visible" )
                    .contains( name + "@local" );

            openControl( textInput() );

            textInput().find( "input.form-control" )
                    .clear()
                    .type( "invalid" )
                    .should( "have.value", "invalid" );

            submitControl( textInput() );
            cy.wait( "@ajaxEditableValueSubmit" );

            textInput().find( ".editable-value-form" )
                    .should( "be.visible" )
                    .find( ".invalid-feedback" )
                    .contains( "Email address is not well-formed" );

        } );

        it( "Updating label value also updates the title and breadcrumb", () => {
            const textInput = () => property( "name" );
            valueModeOfProperty( textInput() )
                    .should( "be.visible" )
                    .contains( name );

            cy.get( ".page-header" )
                    .contains( name );

            cy.get( ".breadcrumb-item.active" )
                    .contains( name );

            openControl( textInput() );

            textInput().find( "textarea.form-control" )
                    .clear()
                    .type( nameReplacement )
                    .should( "have.value", nameReplacement )
                    .blur();

            submitControl( textInput() );
            cy.wait( "@ajaxEditableValueSubmit" );

            valueModeOfProperty( textInput() )
                    .should( "be.visible" )
                    .contains( nameReplacement );

            cy.get( ".page-header" )
                    .contains( nameReplacement );

            cy.get( ".breadcrumb-item.active" )
                    .contains( nameReplacement );

            openControl( textInput() );

            textInput().find( "textarea.form-control" )
                    .clear()
                    .type( name )
                    .should( "have.value", name )
                    .blur();

            submitControl( textInput() );
            cy.wait( "@ajaxEditableValueSubmit" );
        } );
    };

    context( "Updating values on update view", () => {

        beforeEach( () => {
            cy.goToMenuItem( "ExperimentalModuleTestApplicationModule" ).goToMenuItem( "User" );
            navigateToUser( "John" );
        } );

        singleEntityPageTests( "John" );

        it( "Select", () => {
            const select = () => property( "company" );
            valueModeOfProperty( select() )
                    .should( "be.visible" )
                    .should( "have.value", "" );

            openControl( select() );

            select().find( "select.form-control" )
                    .select( "Kodak", {force: true} )
                    .invoke( 'val' );

            submitControl( select() );
            cy.wait( "@ajaxEditableValueSubmit" );

            valueModeOfProperty( select() )
                    .should( "be.visible" )
                    .contains( "Kodak" );
        } );

        it( "Single checkbox", () => {
            const checkbox = () => property( "active" );
            valueModeOfProperty( checkbox() )
                    .should( "be.visible" )
                    .contains( "Yes" );

            openControl( checkbox() );

            checkbox().find( "input.custom-control-input" )
                    .uncheck( {force: true} )
                    .should( "not.be.checked" );

            submitControl( checkbox() );
            cy.wait( "@ajaxEditableValueSubmit" );

            valueModeOfProperty( checkbox() )
                    .should( "be.visible" )
                    .contains( "No" );
        } );
    } );

    context( "Updating values on detail view", () => {

        beforeEach( () => {
            cy.goToMenuItem( 'ExperimentalModuleTestApplicationModule' ).goToMenuItem( 'User' );
            navigateToUser( "Svetty", true );
        } );

        singleEntityPageTests( "Svetty" );

        // it( "Single select and checkbox automatically submit after selection (INCLUDE_ACTIONS = false)", () => {
        //
        // } );

    } );
    //
    // context( "Updating values on list view", () => {
    //
    //     beforeEach( () => {
    //         cy.goToMenuItem( 'ExperimentalModuleTestApplicationModule' ).goToMenuItem( 'User' );
    //     } );
    //
    //     it( "?", {} );
    //
    //     it( "Updating the value of a nested property updates referencing items ", () => {
    //
    //     } );
    // } );
    //
    // context( "Updating values on association list view", () => {
    //
    //     beforeEach( () => {
    //         cy.goToMenuItem( 'ExperimentalModuleTestApplicationModule' ).goToMenuItem( 'User' );
    //     } );
    //
    //     it( "?", () => {
    //
    //     } );
    //
    //     it( "Updating the value of a nested property of the association owner, updates the association title and referencing items", () => {
    //
    //     } );
    // } );

} );