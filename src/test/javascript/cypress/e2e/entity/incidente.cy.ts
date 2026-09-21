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

describe('Incidente e2e test', () => {
  const incidentePageUrl = '/incidente';
  let username: string;
  let password: string;
  const incidenteSample = { titulo: 'developmental', severidad: 'SEV2', estado: 'CERRADO', detectadoEn: '2023-12-04T03:25:48.097Z' };

  let incidente;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/incidentes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/incidentes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/incidentes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (incidente) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/incidentes/${incidente.id}`,
      }).then(() => {
        incidente = undefined;
      });
    }
  });

  it('Incidentes menu should load Incidentes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('incidente');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Incidente').should('exist');
    cy.location('pathname').should('eq', incidentePageUrl);
  });

  describe('Incidente page', () => {
    it('should have translated page title', () => {
      cy.visit(incidentePageUrl);
      cy.getEntityHeading('Incidente').should('not.contain', 'oncallApp.incidente.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(incidentePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Incidente page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${incidentePageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Incidente');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', incidentePageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/incidentes',
          body: incidenteSample,
        }).then(({ body }) => {
          incidente = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/incidentes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/incidentes?page=0&size=20>; rel="last",<http://localhost/api/incidentes?page=0&size=20>; rel="first"',
              },
              body: [incidente],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(incidentePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Incidente page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('incidente');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', incidentePageUrl);
      });

      it('edit button click should load edit Incidente page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Incidente');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', incidentePageUrl);
      });

      it('edit button click should load edit Incidente page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Incidente');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', incidentePageUrl);
      });

      it('last delete button click should delete instance of Incidente', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('incidente').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', incidentePageUrl);

        incidente = undefined;
      });
    });
  });

  describe('new Incidente page', () => {
    beforeEach(() => {
      cy.visit(incidentePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Incidente');
    });

    it('should create an instance of Incidente', () => {
      cy.get(`[data-cy="titulo"]`).type('flustered');
      cy.get(`[data-cy="titulo"]`).should('have.value', 'flustered');

      cy.get(`[data-cy="descripcion"]`).type('forenenst afraid');
      cy.get(`[data-cy="descripcion"]`).should('have.value', 'forenenst afraid');

      cy.get(`[data-cy="severidad"]`).select('SEV3');

      cy.get(`[data-cy="estado"]`).select('RESUELTO');

      cy.get(`[data-cy="detectadoEn"]`).type('2023-12-04T12:33');
      cy.get(`[data-cy="detectadoEn"]`).blur();
      cy.get(`[data-cy="detectadoEn"]`).should('have.value', '2023-12-04T12:33');

      cy.get(`[data-cy="reconocidoEn"]`).type('2023-12-04T11:42');
      cy.get(`[data-cy="reconocidoEn"]`).blur();
      cy.get(`[data-cy="reconocidoEn"]`).should('have.value', '2023-12-04T11:42');

      cy.get(`[data-cy="mitigadoEn"]`).type('2023-12-04T14:31');
      cy.get(`[data-cy="mitigadoEn"]`).blur();
      cy.get(`[data-cy="mitigadoEn"]`).should('have.value', '2023-12-04T14:31');

      cy.get(`[data-cy="resueltoEn"]`).type('2023-12-04T12:57');
      cy.get(`[data-cy="resueltoEn"]`).blur();
      cy.get(`[data-cy="resueltoEn"]`).should('have.value', '2023-12-04T12:57');

      cy.get(`[data-cy="usuariosAfectados"]`).type('32259');
      cy.get(`[data-cy="usuariosAfectados"]`).should('have.value', '32259');

      cy.get(`[data-cy="cumplioObjetivo"]`).should('not.be.checked');
      cy.get(`[data-cy="cumplioObjetivo"]`).click();
      cy.get(`[data-cy="cumplioObjetivo"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        incidente = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', incidentePageUrl);
    });
  });
});
