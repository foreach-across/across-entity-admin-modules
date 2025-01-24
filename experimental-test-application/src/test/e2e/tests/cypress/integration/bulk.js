import utils from "../support/utils.js";

context( 'Bulk action tests', () => {

    beforeEach( () => {
        cy.login( 'admin' );

        // Cypress.minimatch('/users/1/comments/2', '/users/*/comments', { matchBase: true })
        cy.intercept( 'GET', '/admin/entities/food/create?_partial=content', ( req ) => {
            console.log( "modal is loading..." )
        } ).as( 'ajaxModalGet' );
        cy.intercept( 'POST', '/admin/entities/food/create?_partial=::body', ( req ) => {
            console.log( "food is being saved" )
        } ).as( 'ajaxModalPost' );
        cy.intercept( 'GET', new RegExp( "\\/admin\\/entities\\/food\\/.*\\/delete\\?_partial=content" ), ( req ) => {
            console.log( "delete modal loading..." )
        } ).as( 'ajaxModalDelete' );
        cy.intercept( 'POST', new RegExp( "\\/admin\\/entities\\/food\\/.*\\/delete\\?_partial=::body" ), ( req ) => {
            console.log( "delete is completed" )
        } ).as( 'ajaxModalDeleted' );
    } );

    it( 'Create a new food using a modal and bulk assign', function() {
        cy.goToMenuItem( 'ExperimentalModuleTestApplicationModule' ).goToMenuItem( 'Food' );

        cy.contains( "Create a new food" ).click();

        let food = utils.randomString( 20 );
        cy.log( "Saving food with name: " + food );

        //cy.contains( ".modal-content", "Name").type(food);
        cy.wait( "@ajaxModalGet" );
        cy.get( ".modal-content" ).should( "be.visible" );

        cy.contains( "Save" ).click();
        cy.wait( "@ajaxModalPost" );
        cy.contains( "Unable to save, please check the form for one or more errors" );
        //TODO: does not always finish typing?
        //cy.get("textarea").type(food, { delay: 0 });
        cy.get( "textarea" ).invoke( 'val', food );
        cy.get( "#btn-save" ).click();

        cy.contains( food );
        cy.get( ".modal-content" ).should( "not.be.visible" );

        // Check everything
        cy.get( '.form-check-input' ).check( {force: true} )
                .should( 'be.checked' );
        cy.get( 'form[name="bulkActionForm"] .bootstrap-select select' )
                .select( 'BAKE_OVEN', {force: true} ).should( 'have.value', 'BAKE_OVEN' );
        cy.wait( 200 );
        cy.get( 'form[name="bulkActionForm' ).submit();

        cy.get( '.em-sortableTable-table' ).findAllByText( "Bake stove" ).should( 'not.exist' );
        cy.get( '.em-sortableTable-table' ).findAllByText( "Baking oven" ).should( 'exist' );
        cy.get( '.em-sortableTable-table' ).findAllByText( "Stored" ).should( 'not.exist' );

        // Check everything
        cy.get( '.form-check-input' ).check( {force: true} )
                .should( 'be.checked' );
        cy.get( 'form[name="bulkActionForm"] .bootstrap-select select' )
                .select( 'RESET', {force: true} ).should( 'have.value', 'RESET' );
        cy.wait( 200 );
        cy.get( 'form[name="bulkActionForm' ).submit();

        cy.get( '.em-sortableTable-table' ).findAllByText( "Bake stove" ).should( 'not.exist' );
        cy.get( '.em-sortableTable-table' ).findAllByText( "Baking oven" ).should( 'not.exist' );
        cy.get( '.em-sortableTable-table' ).findAllByText( "Stored" ).should( 'exist' );

        // For some reason, it doesn't properly send out an event the first time, so that the bulk action state is updated...
        cy.get( 'tbody .form-check-input' ).first().check()
                .should( 'be.checked' );
        cy.get( 'tbody .form-check-input' ).first().check()
                .should( 'be.checked' );
        cy.get( 'form[name="bulkActionForm"] .bootstrap-select select' )
                .select( 'BAKE_STOVE', {force: true} )
                .should( 'have.value', 'BAKE_STOVE' );
        cy.wait( 200 );
        cy.get( 'form[name="bulkActionForm' ).submit();

        cy.get( '.em-sortableTable-table' ).findAllByText( "Baking stove" ).should( 'exist' );
        cy.get( '.em-sortableTable-table' ).findAllByText( "Baking oven" ).should( 'not.exist' );
        cy.get( '.em-sortableTable-table' ).findAllByText( "Stored" ).should( 'exist' );
    } );

} );