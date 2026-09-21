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

describe('EventoDeIncidente e2e test', () => {
  const eventoDeIncidentePageUrl = '/evento-de-incidente';
  let username: string;
  let password: string;
  const eventoDeIncidenteSample = {
    tipo: 'ESCALAMIENTO',
    detalle: 'duster but',
    ocurridoEn: '2023-12-04T09:34:55.377Z',
    automatico: false,
  };

  let eventoDeIncidente;
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
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/incidentes',
      body: {
        titulo: 'plump fill',
        descripcion: 'calmly',
        severidad: 'SEV4',
        estado: 'CERRADO',
        detectadoEn: '2023-12-04T20:07:47.346Z',
        reconocidoEn: '2023-12-04T19:44:45.034Z',
        mitigadoEn: '2023-12-04T14:25:18.366Z',
        resueltoEn: '2023-12-04T04:03:55.614Z',
        usuariosAfectados: 15956,
        cumplioObjetivo: true,
      },
    }).then(({ body }) => {
      incidente = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/evento-de-incidentes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/evento-de-incidentes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/evento-de-incidentes/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/incidentes', {
      statusCode: 200,
      body: [incidente],
    });
  });

  afterEach(() => {
    if (eventoDeIncidente) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/evento-de-incidentes/${eventoDeIncidente.id}`,
      }).then(() => {
        eventoDeIncidente = undefined;
      });
    }
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

  it('EventoDeIncidentes menu should load EventoDeIncidentes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('evento-de-incidente');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('EventoDeIncidente').should('exist');
    cy.location('pathname').should('eq', eventoDeIncidentePageUrl);
  });

  describe('EventoDeIncidente page', () => {
    it('should have translated page title', () => {
      cy.visit(eventoDeIncidentePageUrl);
      cy.getEntityHeading('EventoDeIncidente').should('not.contain', 'oncallApp.eventoDeIncidente.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(eventoDeIncidentePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create EventoDeIncidente page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${eventoDeIncidentePageUrl}/new`);
        cy.getEntityCreateUpdateHeading('EventoDeIncidente');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', eventoDeIncidentePageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/evento-de-incidentes',
          body: {
            ...eventoDeIncidenteSample,
            incidente,
          },
        }).then(({ body }) => {
          eventoDeIncidente = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/evento-de-incidentes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/evento-de-incidentes?page=0&size=20>; rel="last",<http://localhost/api/evento-de-incidentes?page=0&size=20>; rel="first"',
              },
              body: [eventoDeIncidente],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(eventoDeIncidentePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details EventoDeIncidente page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('eventoDeIncidente');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', eventoDeIncidentePageUrl);
      });

      it('edit button click should load edit EventoDeIncidente page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EventoDeIncidente');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', eventoDeIncidentePageUrl);
      });

      it('edit button click should load edit EventoDeIncidente page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EventoDeIncidente');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', eventoDeIncidentePageUrl);
      });

      it('last delete button click should delete instance of EventoDeIncidente', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('eventoDeIncidente').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', eventoDeIncidentePageUrl);

        eventoDeIncidente = undefined;
      });
    });
  });

  describe('new EventoDeIncidente page', () => {
    beforeEach(() => {
      cy.visit(eventoDeIncidentePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('EventoDeIncidente');
    });

    it('should create an instance of EventoDeIncidente', () => {
      cy.get(`[data-cy="tipo"]`).select('ESCALAMIENTO');

      cy.get(`[data-cy="detalle"]`).type('vice');
      cy.get(`[data-cy="detalle"]`).should('have.value', 'vice');

      cy.get(`[data-cy="ocurridoEn"]`).type('2023-12-04T17:02');
      cy.get(`[data-cy="ocurridoEn"]`).blur();
      cy.get(`[data-cy="ocurridoEn"]`).should('have.value', '2023-12-04T17:02');

      cy.get(`[data-cy="automatico"]`).should('not.be.checked');
      cy.get(`[data-cy="automatico"]`).click();
      cy.get(`[data-cy="automatico"]`).should('be.checked');

      cy.get(`[data-cy="incidente"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        eventoDeIncidente = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', eventoDeIncidentePageUrl);
    });
  });
});
