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

describe('Rotacion e2e test', () => {
  const rotacionPageUrl = '/rotacion';
  let username: string;
  let password: string;
  const rotacionSample = { nombre: 'where', tipo: 'DIARIA', zonaHoraria: 'sushi', activa: true };

  let rotacion;
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
      body: { nombre: 'healthily amount other', emailContacto: 'pastel', canalChat: 'geez give' },
    }).then(({ body }) => {
      equipo = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/rotacions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/rotacions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/rotacions/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/equipos', {
      statusCode: 200,
      body: [equipo],
    });

    cy.intercept('GET', '/api/turno-de-guardias', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/paso-escalamientos', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (rotacion) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/rotacions/${rotacion.id}`,
      }).then(() => {
        rotacion = undefined;
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

  it('Rotacions menu should load Rotacions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('rotacion');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Rotacion').should('exist');
    cy.location('pathname').should('eq', rotacionPageUrl);
  });

  describe('Rotacion page', () => {
    it('should have translated page title', () => {
      cy.visit(rotacionPageUrl);
      cy.getEntityHeading('Rotacion').should('not.contain', 'oncallApp.rotacion.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(rotacionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Rotacion page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${rotacionPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Rotacion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', rotacionPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/rotacions',
          body: {
            ...rotacionSample,
            equipo,
          },
        }).then(({ body }) => {
          rotacion = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/rotacions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [rotacion],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(rotacionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Rotacion page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('rotacion');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', rotacionPageUrl);
      });

      it('edit button click should load edit Rotacion page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Rotacion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', rotacionPageUrl);
      });

      it('edit button click should load edit Rotacion page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Rotacion');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', rotacionPageUrl);
      });

      it('last delete button click should delete instance of Rotacion', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('rotacion').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', rotacionPageUrl);

        rotacion = undefined;
      });
    });
  });

  describe('new Rotacion page', () => {
    beforeEach(() => {
      cy.visit(rotacionPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Rotacion');
    });

    it('should create an instance of Rotacion', () => {
      cy.get(`[data-cy="nombre"]`).type('bathrobe nervously');
      cy.get(`[data-cy="nombre"]`).should('have.value', 'bathrobe nervously');

      cy.get(`[data-cy="tipo"]`).select('DIARIA');

      cy.get(`[data-cy="zonaHoraria"]`).type('sham');
      cy.get(`[data-cy="zonaHoraria"]`).should('have.value', 'sham');

      cy.get(`[data-cy="activa"]`).should('not.be.checked');
      cy.get(`[data-cy="activa"]`).click();
      cy.get(`[data-cy="activa"]`).should('be.checked');

      cy.get(`[data-cy="equipo"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        rotacion = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', rotacionPageUrl);
    });
  });
});
