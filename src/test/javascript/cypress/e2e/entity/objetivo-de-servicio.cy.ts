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

describe('ObjetivoDeServicio e2e test', () => {
  const objetivoDeServicioPageUrl = '/objetivo-de-servicio';
  let username: string;
  let password: string;
  // const objetivoDeServicioSample = {"tipo":"TIEMPO_DE_RESOLUCION","severidadAplicable":"SEV4","minutosObjetivo":5657};

  let objetivoDeServicio;
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
      body: {"nombre":"a without hoot","descripcion":"aside","criticidad":"TIER3","entorno":"STAGING","repositorioUrl":"utilized since after","activo":false},
    }).then(({ body }) => {
      servicio = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/objetivo-de-servicios+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/objetivo-de-servicios').as('postEntityRequest');
    cy.intercept('DELETE', '/api/objetivo-de-servicios/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/servicios', {
      statusCode: 200,
      body: [servicio],
    });

  });
   */

  afterEach(() => {
    if (objetivoDeServicio) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/objetivo-de-servicios/${objetivoDeServicio.id}`,
      }).then(() => {
        objetivoDeServicio = undefined;
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

  it('ObjetivoDeServicios menu should load ObjetivoDeServicios page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('objetivo-de-servicio');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ObjetivoDeServicio').should('exist');
    cy.location('pathname').should('eq', objetivoDeServicioPageUrl);
  });

  describe('ObjetivoDeServicio page', () => {
    it('should have translated page title', () => {
      cy.visit(objetivoDeServicioPageUrl);
      cy.getEntityHeading('ObjetivoDeServicio').should('not.contain', 'oncallApp.objetivoDeServicio.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(objetivoDeServicioPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ObjetivoDeServicio page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${objetivoDeServicioPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('ObjetivoDeServicio');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', objetivoDeServicioPageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/objetivo-de-servicios',
          body: {
            ...objetivoDeServicioSample,
            servicio: servicio,
          },
        }).then(({ body }) => {
          objetivoDeServicio = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/objetivo-de-servicios+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [objetivoDeServicio],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(objetivoDeServicioPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(objetivoDeServicioPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ObjetivoDeServicio page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('objetivoDeServicio');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', objetivoDeServicioPageUrl);
      });

      it('edit button click should load edit ObjetivoDeServicio page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ObjetivoDeServicio');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', objetivoDeServicioPageUrl);
      });

      it('edit button click should load edit ObjetivoDeServicio page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ObjetivoDeServicio');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', objetivoDeServicioPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of ObjetivoDeServicio', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('objetivoDeServicio').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', objetivoDeServicioPageUrl);

        objetivoDeServicio = undefined;
      });
    });
  });

  describe('new ObjetivoDeServicio page', () => {
    beforeEach(() => {
      cy.visit(objetivoDeServicioPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ObjetivoDeServicio');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of ObjetivoDeServicio', () => {
      cy.get(`[data-cy="tipo"]`).select('TIEMPO_DE_RECONOCIMIENTO');

      cy.get(`[data-cy="severidadAplicable"]`).select('SEV3');

      cy.get(`[data-cy="minutosObjetivo"]`).type('827');
      cy.get(`[data-cy="minutosObjetivo"]`).should('have.value', '827');

      cy.get(`[data-cy="descripcion"]`).type('rubbery sermon');
      cy.get(`[data-cy="descripcion"]`).should('have.value', 'rubbery sermon');

      cy.get(`[data-cy="servicio"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        objetivoDeServicio = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', objetivoDeServicioPageUrl);
    });
  });
});
