context('Website login', () => {

    beforeEach(() => {
        cy.visit('/admin/entities/drink')
    });

    it('should redirect to login page if not logged on', function () {
        cy.url().should('include', 'login')
    });

    it('allows login with admin/admin credentials', function () {
        cy.get('#username')
            .type('admin');
        cy.get('#password')
            .type('admin');
        cy.get('button[type=submit]').click();

        // Administrators see the users page by default
        cy.url().should('include', '/admin/entities/drink');
    });
});