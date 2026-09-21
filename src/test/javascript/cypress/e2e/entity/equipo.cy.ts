import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Equipo e2e test', () => {
  const equipoPageUrl = '/equipo';
  let username: string;
  let password: string;
  const equipoSample = { nombre: 'like', emailContacto: 'up brightly' };

  let equipo;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/equipos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/equipos').as('postEntityRequest');
    cy.intercept('DELETE', '/api/equipos/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (equipo) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/equipos/${equipo.id}`,
      }).then(() => {
        equipo = undefined;
      });
    }
  });

  it('Equipos menu should load Equipos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('equipo');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Equipo').should('exist');
    cy.location('pathname').should('eq', equipoPageUrl);
  });

  describe('Equipo page', () => {
    it('should have translated page title', () => {
      cy.visit(equipoPageUrl);
      cy.getEntityHeading('Equipo').should('not.contain', 'oncallApp.equipo.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(equipoPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Equipo page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${equipoPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Equipo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', equipoPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/equipos',
          body: equipoSample,
        }).then(({ body }) => {
          equipo = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/equipos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [equipo],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(equipoPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Equipo page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('equipo');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', equipoPageUrl);
      });

      it('edit button click should load edit Equipo page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Equipo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', equipoPageUrl);
      });

      it('edit button click should load edit Equipo page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Equipo');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', equipoPageUrl);
      });

      it('last delete button click should delete instance of Equipo', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('equipo').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', equipoPageUrl);

        equipo = undefined;
      });
    });
  });

  describe('new Equipo page', () => {
    beforeEach(() => {
      cy.visit(equipoPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Equipo');
    });

    it('should create an instance of Equipo', () => {
      cy.get(`[data-cy="nombre"]`).type('hovercraft');
      cy.get(`[data-cy="nombre"]`).should('have.value', 'hovercraft');

      cy.get(`[data-cy="emailContacto"]`).type('once passionate formal');
      cy.get(`[data-cy="emailContacto"]`).should('have.value', 'once passionate formal');

      cy.get(`[data-cy="canalChat"]`).type('and');
      cy.get(`[data-cy="canalChat"]`).should('have.value', 'and');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        equipo = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', equipoPageUrl);
    });
  });
});
