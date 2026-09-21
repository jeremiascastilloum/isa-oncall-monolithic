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

describe('PoliticaEscalamiento e2e test', () => {
  const politicaEscalamientoPageUrl = '/politica-escalamiento';
  let username: string;
  let password: string;
  // const politicaEscalamientoSample = {"nombre":"circa","repetirVeces":5};

  let politicaEscalamiento;
  // let servicio;

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
      url: '/api/servicios',
      body: {"nombre":"anenst","descripcion":"spectacles banish","criticidad":"TIER2","entorno":"PRODUCCION","repositorioUrl":"between","activo":true},
    }).then(({ body }) => {
      servicio = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/politica-escalamientos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/politica-escalamientos').as('postEntityRequest');
    cy.intercept('DELETE', '/api/politica-escalamientos/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/servicios', {
      statusCode: 200,
      body: [servicio],
    });

    cy.intercept('GET', '/api/paso-escalamientos', {
      statusCode: 200,
      body: [],
    });

  });
   */

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

  /* Disabled due to incompatibility
  afterEach(() => {
    if (servicio) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/servicios/${servicio.id}`,
      }).then(() => {
        servicio = undefined;
      });
    }
  });
   */

  it('PoliticaEscalamientos menu should load PoliticaEscalamientos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('politica-escalamiento');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PoliticaEscalamiento').should('exist');
    cy.location('pathname').should('eq', politicaEscalamientoPageUrl);
  });

  describe('PoliticaEscalamiento page', () => {
    it('should have translated page title', () => {
      cy.visit(politicaEscalamientoPageUrl);
      cy.getEntityHeading('PoliticaEscalamiento').should('not.contain', 'oncallApp.politicaEscalamiento.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(politicaEscalamientoPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PoliticaEscalamiento page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${politicaEscalamientoPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('PoliticaEscalamiento');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', politicaEscalamientoPageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/politica-escalamientos',
          body: {
            ...politicaEscalamientoSample,
            servicio: servicio,
          },
        }).then(({ body }) => {
          politicaEscalamiento = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/politica-escalamientos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [politicaEscalamiento],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(politicaEscalamientoPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(politicaEscalamientoPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details PoliticaEscalamiento page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('politicaEscalamiento');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', politicaEscalamientoPageUrl);
      });

      it('edit button click should load edit PoliticaEscalamiento page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PoliticaEscalamiento');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', politicaEscalamientoPageUrl);
      });

      it('edit button click should load edit PoliticaEscalamiento page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PoliticaEscalamiento');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', politicaEscalamientoPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of PoliticaEscalamiento', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('politicaEscalamiento').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', politicaEscalamientoPageUrl);

        politicaEscalamiento = undefined;
      });
    });
  });

  describe('new PoliticaEscalamiento page', () => {
    beforeEach(() => {
      cy.visit(politicaEscalamientoPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PoliticaEscalamiento');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of PoliticaEscalamiento', () => {
      cy.get(`[data-cy="nombre"]`).type('fibre runny birth');
      cy.get(`[data-cy="nombre"]`).should('have.value', 'fibre runny birth');

      cy.get(`[data-cy="descripcion"]`).type('among especially');
      cy.get(`[data-cy="descripcion"]`).should('have.value', 'among especially');

      cy.get(`[data-cy="repetirVeces"]`).type('1');
      cy.get(`[data-cy="repetirVeces"]`).should('have.value', '1');

      cy.get(`[data-cy="servicio"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        politicaEscalamiento = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', politicaEscalamientoPageUrl);
    });
  });
});
