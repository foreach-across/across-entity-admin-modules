import utils from "../support/utils.js";

context( 'Modal tests', () => {

    beforeEach( () => {
        cy.login( 'admin' )

        // Cypress.minimatch('/users/1/comments/2', '/users/*/comments', { matchBase: true })
        cy.intercept( 'GET', '/admin/entities/food/create?_partial=content', ( req ) => {
            console.log( "modal is loading..." )
        } ).as( 'ajaxModalGet' );
        cy.intercept( 'POST', '/admin/entities/food/create?_partial=::body', ( req ) => {
            console.log( "food is being saved" )
        } ).as( 'ajaxModalPost' );
        cy.intercept( 'GET', '/admin/entities/food?_partial=::itemsTable', ( req ) => {
            console.log( "list is being refreshed" )
        } ).as( 'ajaxEntitiesList' );
        cy.intercept( 'GET', new RegExp( "\\/admin\\/entities\\/food\\/.*\\/delete\\?_partial=content" ), ( req ) => {
            console.log( "delete modal loading..." )
        } ).as( 'ajaxModalDelete' );
        cy.intercept( 'POST', new RegExp( "\\/admin\\/entities\\/food\\/.*\\/delete\\?_partial=::body" ), ( req ) => {
            console.log( "delete is completed" )
        } ).as( 'ajaxModalDeleted' );
    } );

    it( 'Create a new food using a modal and delete it', function() {
        cy.goToMenuItem( 'ExperimentalModuleTestApplicationModule' ).goToMenuItem( 'Food' );

        cy.contains( "Create a new food" ).click();

        let food = "000" + utils.randomString( 20 );
        cy.log( "Saving food with name: " + food );

        //cy.contains( ".modal-content", "Name").type(food);
        cy.wait( "@ajaxModalGet" );
        cy.get( ".modal-content" ).should( "be.visible" );

        //cy.get( "textarea").type(food);
        cy.contains( "Save" ).click();
        cy.wait( "@ajaxModalPost" );
        cy.contains( "Unable to save, please check the form for one or more errors" );
        //TODO: does not always finish typing?
        //cy.get("textarea").type(food, { delay: 0 });
        cy.get( "textarea" ).invoke( 'val', food );
        cy.contains( "Save" ).click();

        cy.contains( food );
        cy.get( ".modal-content" ).should( "not.be.visible" );
        cy.findAllByText( food ).should( 'exist' );
        cy.contains( food )
                .closest( 'tr' )
                .then( element => {
                    const currentAction = cy.get( element ).find( '[data-tbl-field="currentAction"]' );
                    currentAction.should( 'have.value', '' );
                    currentAction.children().should( 'have.value', '' );

                    cy.wrap( element );
                } )
                .find( '.fa-times' )
                .click();

        cy.wait( "@ajaxModalDelete" );

        cy.get( ".modal-content" ).should( "be.visible" );
        cy.contains( 'button', 'Delete' ).click();

        cy.wait( 150 );
        cy.get( ".modal-content" ).should( "not.be.visible" );

        cy.get( '.em-sortableTable-table' )
                .findAllByText( food ).should( 'not.exist' );
    } );

} );