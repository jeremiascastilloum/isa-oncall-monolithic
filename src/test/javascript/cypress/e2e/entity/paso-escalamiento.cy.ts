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

describe('PasoEscalamiento e2e test', () => {
  const pasoEscalamientoPageUrl = '/paso-escalamiento';
  let username: string;
  let password: string;
  // const pasoEscalamientoSample = {"orden":9,"esperaMinutos":67,"canal":"PUSH"};

  let pasoEscalamiento;
  // let politicaEscalamiento;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/politica-escalamientos',
      body: {"nombre":"except","descripcion":"think coolly","repetirVeces":4},
    }).then(({ body }) => {
      politicaEscalamiento = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/paso-escalamientos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/paso-escalamientos').as('postEntityRequest');
    cy.intercept('DELETE', '/api/paso-escalamientos/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/politica-escalamientos', {
      statusCode: 200,
      body: [politicaEscalamiento],
    });

    cy.intercept('GET', '/api/rotacions', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (pasoEscalamiento) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/paso-escalamientos/${pasoEscalamiento.id}`,
      }).then(() => {
        pasoEscalamiento = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (politicaEscalamiento) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/politica-escalamientos/${politicaEscalamiento.id}`,
      }).then(() => {
        politicaEscalamiento = undefined;
      });
    }
  });
   */

  it('PasoEscalamientos menu should load PasoEscalamientos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('paso-escalamiento');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PasoEscalamiento').should('exist');
    cy.location('pathname').should('eq', pasoEscalamientoPageUrl);
  });

  describe('PasoEscalamiento page', () => {
    it('should have translated page title', () => {
      cy.visit(pasoEscalamientoPageUrl);
      cy.getEntityHeading('PasoEscalamiento').should('not.contain', 'oncallApp.pasoEscalamiento.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(pasoEscalamientoPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PasoEscalamiento page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${pasoEscalamientoPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('PasoEscalamiento');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', pasoEscalamientoPageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/paso-escalamientos',
          body: {
            ...pasoEscalamientoSample,
            politica: politicaEscalamiento,
          },
        }).then(({ body }) => {
          pasoEscalamiento = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/paso-escalamientos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [pasoEscalamiento],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(pasoEscalamientoPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(pasoEscalamientoPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details PasoEscalamiento page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('pasoEscalamiento');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', pasoEscalamientoPageUrl);
      });

      it('edit button click should load edit PasoEscalamiento page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PasoEscalamiento');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', pasoEscalamientoPageUrl);
      });

      it('edit button click should load edit PasoEscalamiento page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PasoEscalamiento');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', pasoEscalamientoPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of PasoEscalamiento', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('pasoEscalamiento').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', pasoEscalamientoPageUrl);

        pasoEscalamiento = undefined;
      });
    });
  });

  describe('new PasoEscalamiento page', () => {
    beforeEach(() => {
      cy.visit(pasoEscalamientoPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PasoEscalamiento');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of PasoEscalamiento', () => {
      cy.get(`[data-cy="orden"]`).type('6');
      cy.get(`[data-cy="orden"]`).should('have.value', '6');

      cy.get(`[data-cy="esperaMinutos"]`).type('39');
      cy.get(`[data-cy="esperaMinutos"]`).should('have.value', '39');

      cy.get(`[data-cy="canal"]`).select('SMS');

      cy.get(`[data-cy="politica"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        pasoEscalamiento = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', pasoEscalamientoPageUrl);
    });
  });
});
