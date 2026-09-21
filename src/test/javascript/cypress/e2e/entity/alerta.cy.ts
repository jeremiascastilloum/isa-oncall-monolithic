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

describe('Alerta e2e test', () => {
  const alertaPageUrl = '/alerta';
  let username: string;
  let password: string;
  // const alertaSample = {"fingerprint":"skean tomography including","origen":"CLOUDWATCH","resumen":"qua","recibidaEn":"2023-12-04T02:47:04.854Z","procesada":true};

  let alerta;
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
      body: {"nombre":"while graffiti reopen","descripcion":"dally zowie","criticidad":"TIER2","entorno":"PRODUCCION","repositorioUrl":"daintily nimble consign","activo":false},
    }).then(({ body }) => {
      servicio = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/alertas+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/alertas').as('postEntityRequest');
    cy.intercept('DELETE', '/api/alertas/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/servicios', {
      statusCode: 200,
      body: [servicio],
    });

    cy.intercept('GET', '/api/incidentes', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (alerta) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/alertas/${alerta.id}`,
      }).then(() => {
        alerta = undefined;
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

  it('Alertas menu should load Alertas page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('alerta');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Alerta').should('exist');
    cy.location('pathname').should('eq', alertaPageUrl);
  });

  describe('Alerta page', () => {
    it('should have translated page title', () => {
      cy.visit(alertaPageUrl);
      cy.getEntityHeading('Alerta').should('not.contain', 'oncallApp.alerta.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(alertaPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Alerta page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${alertaPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Alerta');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', alertaPageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/alertas',
          body: {
            ...alertaSample,
            servicio: servicio,
          },
        }).then(({ body }) => {
          alerta = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/alertas+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/alertas?page=0&size=20>; rel="last",<http://localhost/api/alertas?page=0&size=20>; rel="first"',
              },
              body: [alerta],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(alertaPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(alertaPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Alerta page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('alerta');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', alertaPageUrl);
      });

      it('edit button click should load edit Alerta page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Alerta');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', alertaPageUrl);
      });

      it('edit button click should load edit Alerta page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Alerta');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', alertaPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of Alerta', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('alerta').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', alertaPageUrl);

        alerta = undefined;
      });
    });
  });

  describe('new Alerta page', () => {
    beforeEach(() => {
      cy.visit(alertaPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Alerta');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of Alerta', () => {
      cy.get(`[data-cy="fingerprint"]`).type('that truthfully mockingly');
      cy.get(`[data-cy="fingerprint"]`).should('have.value', 'that truthfully mockingly');

      cy.get(`[data-cy="origen"]`).select('HEALTHCHECK');

      cy.get(`[data-cy="resumen"]`).type('tenderly');
      cy.get(`[data-cy="resumen"]`).should('have.value', 'tenderly');

      cy.get(`[data-cy="payload"]`).type('bitter');
      cy.get(`[data-cy="payload"]`).should('have.value', 'bitter');

      cy.get(`[data-cy="recibidaEn"]`).type('2023-12-04T04:23');
      cy.get(`[data-cy="recibidaEn"]`).blur();
      cy.get(`[data-cy="recibidaEn"]`).should('have.value', '2023-12-04T04:23');

      cy.get(`[data-cy="procesada"]`).should('not.be.checked');
      cy.get(`[data-cy="procesada"]`).click();
      cy.get(`[data-cy="procesada"]`).should('be.checked');

      cy.get(`[data-cy="servicio"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        alerta = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', alertaPageUrl);
    });
  });
});
