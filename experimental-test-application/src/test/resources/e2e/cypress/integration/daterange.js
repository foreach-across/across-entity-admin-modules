import utils from "../support/utils.js";

context('Modal tests', () => {

    beforeEach(() => {
        cy.login('admin');
    });

    it('Verify dropdowns', function () {
        cy.goToMenuItem('ExperimentalModuleTestApplicationModule').goToMenuItem('Student');
        cy.get("[data-entity-query-property='lastModifiedDate']").should('have.class', 'custom-date-range-picker');

        cy.get("[data-entity-query-property='lastModifiedDate']").within(($dateRangeControl) => {
            cy.get(".js-custom-date-picker-dates").should("not.be.visible");
            cy.get("select").select("Pikkiediedate").should("have.value", "custom");
            cy.get(".js-custom-date-picker-dates").should("be.visible");
            cy.get("select").select("Last 14 days (absolute)").should("have.value", "last14d()");
            cy.get(".js-custom-date-picker-dates").should("not.be.visible");
            cy.get("select").select("Yesterday").should("have.value", "yesterday()");
            cy.get("select").select("Last week").should("have.value", "lastWeek()");
        });

        cy.get("[data-entity-query-property='createdDate']").within(($dateRangeControl) => {
            cy.get(".js-custom-date-picker-dates").should("not.be.visible");
            cy.get("select").select("Pikkiediedate").should("have.value", "custom");
            cy.get(".js-custom-date-picker-dates").should("be.visible");
            cy.get("select").select("Today").should("have.value", "today()");
            cy.get("select").select("Yesterday").should("have.value", "yesterday()");
            cy.get(".js-custom-date-picker-dates").should("not.be.visible");
            cy.get("select").select("Last week").should("have.value", "lastWeek()");
            cy.get("select").select("Last month").should("have.value", "lastMonth()");
            cy.get("select").select("Last year").should("have.value", "lastYear()");
        });
    });

    it('Verify non-today filters', function () {
        cy.goToMenuItem('ExperimentalModuleTestApplicationModule').goToMenuItem('Student');
        cy.contains("Create a new student").click();


        let random = "Student-" + utils.randomString(20);
        cy.contains("Name").type(random);
        let today = (new Date()).toLocaleDateString('nl-BE');
        cy.contains("Last modified date").type(today)
        cy.contains("Save").click();

        cy.goToMenuItem('ExperimentalModuleTestApplicationModule').goToMenuItem('Student');
        cy.findAllByText(random).should('exist');

        cy.get("[data-entity-query-property='lastModifiedDate']").within(($dateRangeControl) => {
            cy.get("select").select("Yesterday").should("have.value", "yesterday()");
        });

        cy.contains("Name").type(random);
        cy.get("[name='entityListForm'").submit();
        cy.findAllByText(random).should('not.exist');
        //cy.get("[name='entityListForm'").within( ($form) => {
        //  cy.contains("Name").invoke('val', random);
        //$form.submit();
        //} );
    });

});