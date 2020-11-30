import utils from "../support/utils.js";

context('Depends On tests', () => {

    beforeEach(() => {
        cy.login('admin')
        cy.server();
        cy.route('GET', '/admin/entities/food/**').as("ajaxModalGet");
        cy.route('POST', '/admin/entities/food/**').as("ajaxModalPost");
    });

    it('do not show percentage when it does not contains alcohol', function () {
        cy.goToMenuItem('ExperimentalModuleTestApplicationModule').goToMenuItem('Food');

        cy.contains("Create a new food").click();

        let food = utils.randomString(20);
        cy.log("Saving food with name: " + food);

        //cy.contains( ".modal-content", "Name").type(food);
        cy.wait("@ajaxModalGet");

        //cy.get( "textarea").type(food);
        cy.contains("Save").click();
        cy.wait("@ajaxModalPost")
        cy.contains("Unable to save, please check the form for one or more errors");
        cy.get("textarea").type(food);
        cy.contains("Save").click();
        cy.wait("@ajaxModalPost")
        //cy.contains("Cancel").click();

    });

});