context( "Editable value tests (User / Company entities)", () => {

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

    context( "Updating values on list view", () => {

        beforeEach( () => {
            cy.goToMenuItem( 'ExperimentalModuleTestApplicationModule' ).goToMenuItem( 'User' );
            cy.wait( 150 );
        } );

        it( "Update property on listview", () => {
            cy.contains( "Deborah" )
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
    } );
} );