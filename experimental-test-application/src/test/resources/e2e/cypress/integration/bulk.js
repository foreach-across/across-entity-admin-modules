import utils from "../support/utils.js";

context('Depends On tests', () => {

    beforeEach(() => {
        cy.login('admin')

        // Cypress.minimatch('/users/1/comments/2', '/users/*/comments', { matchBase: true })
        cy.intercept('GET', '/admin/entities/food/create?_partial=content', (req) => {
            console.log("modal is loading...")
        }).as('ajaxModalGet');
        cy.intercept('POST', '/admin/entities/food/create?_partial=::body', (req) => {
            console.log("food is being saved")
        }).as('ajaxModalPost');
        cy.intercept('GET', '/admin/entities/food?_partial=::itemsTable', (req) => {
            console.log("list is being refreshed")
        }).as('ajaxEntitiesList');
        cy.server();
        cy.intercept('GET', new RegExp("\\/admin\\/entities\\/food\\/.*\\/delete\\?_partial=content"), (req) => {
            console.log("delete modal loading...")
        }).as('ajaxModalDelete');
        cy.intercept('POST', new RegExp("\\/admin\\/entities\\/food\\/.*\\/delete\\?_partial=::body"), (req) => {
            console.log("delete is completed")
        }).as('ajaxModalDeleted');
    });

    it('Create a new food using a modal', function () {
        cy.goToMenuItem('ExperimentalModuleTestApplicationModule').goToMenuItem('Food');

        cy.contains("Create a new food").click();

        let food = utils.randomString(20);
        cy.log("Saving food with name: " + food);

        //cy.contains( ".modal-content", "Name").type(food);
        cy.wait("@ajaxModalGet");
        cy.get(".modal-content").should("be.visible");

        //cy.get( "textarea").type(food);
        cy.contains("Save").click();
        cy.wait("@ajaxModalPost")
        cy.contains("Unable to save, please check the form for one or more errors");
        cy.get("textarea").type(food);
        cy.contains("Save").click();

        cy.wait("@ajaxEntitiesList");
        cy.get(".modal-content").should("not.be.visible");

        cy.contains(food).next().should('have.value', '').next().find('.fa-times').click();
        cy.wait("@ajaxModalDelete");
        cy.contains('button', 'Delete').click();
        cy.wait("@ajaxEntitiesList");
    });

});