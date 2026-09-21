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

describe('Servicio e2e test', () => {
  const servicioPageUrl = '/servicio';
  let username: string;
  let password: string;
  const servicioSample = { nombre: 'unused husband', criticidad: 'TIER2', entorno: 'PRODUCCION', activo: true };

  let servicio;
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
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/equipos',
      body: { nombre: 'mentor', emailContacto: 'briefly bleakly chap', canalChat: 'paltry' },
    }).then(({ body }) => {
      equipo = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/servicios+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/servicios').as('postEntityRequest');
    cy.intercept('DELETE', '/api/servicios/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/equipos', {
      statusCode: 200,
      body: [equipo],
    });

    cy.intercept('GET', '/api/objetivo-de-servicios', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/alertas', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/politica-escalamientos', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/incidentes', {
      statusCode: 200,
      body: [],
    });
  });

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

  it('Servicios menu should load Servicios page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('servicio');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Servicio').should('exist');
    cy.location('pathname').should('eq', servicioPageUrl);
  });

  describe('Servicio page', () => {
    it('should have translated page title', () => {
      cy.visit(servicioPageUrl);
      cy.getEntityHeading('Servicio').should('not.contain', 'oncallApp.servicio.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(servicioPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Servicio page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${servicioPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Servicio');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', servicioPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/servicios',
          body: {
            ...servicioSample,
            equipo,
          },
        }).then(({ body }) => {
          servicio = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/servicios+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [servicio],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(servicioPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Servicio page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('servicio');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', servicioPageUrl);
      });

      it('edit button click should load edit Servicio page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Servicio');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', servicioPageUrl);
      });

      it('edit button click should load edit Servicio page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Servicio');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', servicioPageUrl);
      });

      it('last delete button click should delete instance of Servicio', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('servicio').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', servicioPageUrl);

        servicio = undefined;
      });
    });
  });

  describe('new Servicio page', () => {
    beforeEach(() => {
      cy.visit(servicioPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Servicio');
    });

    it('should create an instance of Servicio', () => {
      cy.get(`[data-cy="nombre"]`).type('separately since');
      cy.get(`[data-cy="nombre"]`).should('have.value', 'separately since');

      cy.get(`[data-cy="descripcion"]`).type('deceivingly vibration yowza');
      cy.get(`[data-cy="descripcion"]`).should('have.value', 'deceivingly vibration yowza');

      cy.get(`[data-cy="criticidad"]`).select('TIER2');

      cy.get(`[data-cy="entorno"]`).select('STAGING');

      cy.get(`[data-cy="repositorioUrl"]`).type('an');
      cy.get(`[data-cy="repositorioUrl"]`).should('have.value', 'an');

      cy.get(`[data-cy="activo"]`).should('not.be.checked');
      cy.get(`[data-cy="activo"]`).click();
      cy.get(`[data-cy="activo"]`).should('be.checked');

      cy.get(`[data-cy="equipo"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        servicio = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', servicioPageUrl);
    });
  });
});
