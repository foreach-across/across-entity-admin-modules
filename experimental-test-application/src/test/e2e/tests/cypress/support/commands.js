//import '@testing-library/cypress/add-commands'

Cypress.Commands.add( "login", ( username ) => {
    cy.fixture( "users" ).then( users => {
        cy.visit( "/admin/login" );

        cy.get( '#username' )
                .clear()
                .type( username )
                .get( '#password' )
                .clear()
                .type( users[username] )
                .get( '.btn-primary' )
                .click();
    } );

} );

Cypress.Commands.add( 'goToMenuItem', ( title ) => {
    cy.get( '.sidebar' ).within( () => {
        cy.get( 'a[title="' + title + '"]' ).click();
    } );
} );