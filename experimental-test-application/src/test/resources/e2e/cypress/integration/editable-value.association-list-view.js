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
        //TODO: better way of handling this ?
        // wait long for date pickers to initialize ?
        cy.wait( 1500 );
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

    function isInValueMode( property ) {
        property.find( "form.editable-value-form" )
                .should( 'not.exist' );
    }

    beforeEach( () => {
        cy.login( 'admin' );

        cy.intercept( "POST", new RegExp( "\\/admin\\/entities\\/user\\/.*\\/update\\?view=editableValues" ), ( req ) => {
            console.log( "Control submit has completed" );
        } ).as( "ajaxEditableValueSubmit" );
    } );

    context( "Updating values on association list view", () => {

        beforeEach( () => {
            cy.goToMenuItem( 'ExperimentalModuleTestApplicationModule' ).goToMenuItem( 'User' );
            navigateToUser( "Deborah", true );
            cy.get( "[data-ax-menu-path='user\.mentor'] > a" )
                    .click();
            cy.wait( 150 );
        } );

        it( "Update property on association list view", () => {
            cy.contains( "Jors" )
                    .closest( "tr" )
                    .then( ( row ) => {
                        const company = () => cy.wrap( row ).find( "[data-em-property='company']" );

                        valueModeOfProperty( company() )
                                .should( "be.visible" )
                                .contains( "AGFA" );

                        openControl( company() );

                        company().find( "select.form-control" )
                                .select( "Kodak", {force: true} )
                                .invoke( 'val' );

                        submitControl( company() );
                        cy.wait( "@ajaxEditableValueSubmit" );

                        isInValueMode( company() );

                        valueModeOfProperty( company() )
                                .should( "be.visible" )
                                .contains( "Kodak" );
                    } );
        } );

        it( "Updating the value of a nested property of the association owner, updates the association title and referencing items", () => {
            cy.contains( "Jors" )
                    .closest( "tr" )
                    .then( ( row ) => {
                        const mentorName = () => cy.wrap( row ).find( "[data-em-property='mentor\.name']" );

                        valueModeOfProperty( mentorName() )
                                .should( "be.visible" )
                                .contains( "Deborah" );

                        openControl( mentorName() );

                        mentorName().find( "textarea.form-control" )
                                .clear()
                                .type( "Debrah" )
                                .should( "have.value", "Debrah" );

                        submitControl( mentorName() );
                        cy.wait( "@ajaxEditableValueSubmit" );

                        isInValueMode( mentorName() );

                        valueModeOfProperty( mentorName() )
                                .should( "be.visible" )
                                .contains( "Debrah" );

                        // found whilst it really doesn't exist...
                        cy.contains( "Deborah" )
                                .filter( ( i, elem ) => {
                                    return elem.closest( "[data-em-editable-value-role='control-container']" ).length === 0;
                                } )
                                .should( 'not.exist' );

                        openControl( mentorName() );

                        mentorName().find( "textarea.form-control" )
                                .clear()
                                .type( "Deborah" )
                                .should( "have.value", "Deborah" );

                        submitControl( mentorName() );
                        cy.wait( "@ajaxEditableValueSubmit" );

                        isInValueMode( mentorName() );

                        valueModeOfProperty( mentorName() )
                                .should( "be.visible" )
                                .contains( "Deborah" );
                    } );
        } );
    } );
} );