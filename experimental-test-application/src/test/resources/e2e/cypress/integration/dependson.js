import utils from "../support/utils.js";

context( 'Depends On tests', () => {

    beforeEach( () => {
        cy.login( 'admin' )
    } );

    it( 'do not show percentage when it does not contains alcohol', function() {
        cy.goToMenuItem( 'ExperimentalModuleTestApplicationModule' ).goToMenuItem( 'Drink' );
        cy.contains( "Create a new drink" ).click();

        // Default state
        cy.contains( "Alcohol percentage" ).should( 'not.be.visible' ).should( 'have.value', '' );

        // Toggle
        cy.contains( "Contains alcohol" ).click();
        cy.contains( "Alcohol percentage" ).should( 'be.visible' ).should( 'have.value', '' );
        cy.contains( "Contains alcohol" ).click();

        // Save
        let drink = utils.randomString( 20 );
        cy.log( "Saving drink with name: " + drink );
        cy.contains( "Name" ).type( drink );
        cy.contains( "Save" ).click();

        // Verify
        cy.goToMenuItem( 'Drink' );
        cy.contains( drink ).closest( 'tr' )
                .then( element => {
                    cy.get( element ).find( '[data-tbl-field="containsAlcohol"]' ).contains( 'No' );
                    const alcoholPercentage = cy.get( element ).find( '[data-tbl-field="alcoholPercentage"]' );
                    alcoholPercentage.children().should( 'have.value', '' );
                    alcoholPercentage.should( 'have.value', '' );
                } );
    } );

    it( 'show percentage when it contains alcohol', function() {
        cy.goToMenuItem( 'ExperimentalModuleTestApplicationModule' ).goToMenuItem( 'Drink' );
        cy.contains( "Create a new drink" ).click();

        // Default state
        cy.contains( "Alcohol percentage" ).should( 'not.be.visible' ).should( 'have.value', '' );

        // Toggle
        cy.contains( "Contains alcohol" ).click();

        // Save
        let drink = utils.randomString( 20 );
        let percentage = Math.floor( Math.random() * 100 ) + "%";
        cy.log( "Saving drink with name: " + drink );
        cy.contains( "Name" ).type( drink );
        cy.contains( "Alcohol percentage" ).type( percentage );
        cy.contains( "Save" ).click();

        // Verify
        cy.goToMenuItem( 'Drink' );
        cy.contains( drink ).closest( 'tr' )
                .then( element => {
                    cy.get( element ).find( '[data-tbl-field="containsAlcohol"]' ).contains( 'Yes' );
                    cy.get( element ).find( '[data-tbl-field="alcoholPercentage"]' ).contains( percentage );
                } );
    } );
} );