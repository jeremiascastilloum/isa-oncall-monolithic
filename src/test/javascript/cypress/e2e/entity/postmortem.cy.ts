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

describe('Postmortem e2e test', () => {
  const postmortemPageUrl = '/postmortem';
  let username: string;
  let password: string;
  const postmortemSample = { titulo: 'duh since', resumen: 'past feminize far', causaRaiz: 'incidentally', publicado: true };

  let postmortem;
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
        titulo: 'customise uh-huh small',
        descripcion: 'afore since round',
        severidad: 'SEV3',
        estado: 'MITIGADO',
        detectadoEn: '2023-12-04T10:14:48.875Z',
        reconocidoEn: '2023-12-04T14:55:40.944Z',
        mitigadoEn: '2023-12-04T10:19:22.799Z',
        resueltoEn: '2023-12-04T08:25:24.412Z',
        usuariosAfectados: 21711,
        cumplioObjetivo: true,
      },
    }).then(({ body }) => {
      incidente = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/postmortems+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/postmortems').as('postEntityRequest');
    cy.intercept('DELETE', '/api/postmortems/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/incidentes', {
      statusCode: 200,
      body: [incidente],
    });

    cy.intercept('GET', '/api/accion-correctivas', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (postmortem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/postmortems/${postmortem.id}`,
      }).then(() => {
        postmortem = undefined;
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

  it('Postmortems menu should load Postmortems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('postmortem');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Postmortem').should('exist');
    cy.location('pathname').should('eq', postmortemPageUrl);
  });

  describe('Postmortem page', () => {
    it('should have translated page title', () => {
      cy.visit(postmortemPageUrl);
      cy.getEntityHeading('Postmortem').should('not.contain', 'oncallApp.postmortem.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(postmortemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Postmortem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${postmortemPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Postmortem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', postmortemPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/postmortems',
          body: {
            ...postmortemSample,
            incidente,
          },
        }).then(({ body }) => {
          postmortem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/postmortems+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/postmortems?page=0&size=20>; rel="last",<http://localhost/api/postmortems?page=0&size=20>; rel="first"',
              },
              body: [postmortem],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(postmortemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Postmortem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('postmortem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', postmortemPageUrl);
      });

      it('edit button click should load edit Postmortem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Postmortem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', postmortemPageUrl);
      });

      it('edit button click should load edit Postmortem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Postmortem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', postmortemPageUrl);
      });

      it('last delete button click should delete instance of Postmortem', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('postmortem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', postmortemPageUrl);

        postmortem = undefined;
      });
    });
  });

  describe('new Postmortem page', () => {
    beforeEach(() => {
      cy.visit(postmortemPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Postmortem');
    });

    it('should create an instance of Postmortem', () => {
      cy.get(`[data-cy="titulo"]`).type('ha revere gee');
      cy.get(`[data-cy="titulo"]`).should('have.value', 'ha revere gee');

      cy.get(`[data-cy="resumen"]`).type('once');
      cy.get(`[data-cy="resumen"]`).should('have.value', 'once');

      cy.get(`[data-cy="causaRaiz"]`).type('gah hmph how');
      cy.get(`[data-cy="causaRaiz"]`).should('have.value', 'gah hmph how');

      cy.get(`[data-cy="lineaDeTiempo"]`).type('how');
      cy.get(`[data-cy="lineaDeTiempo"]`).should('have.value', 'how');

      cy.get(`[data-cy="leccionesAprendidas"]`).type('knowledgeably contrast alive');
      cy.get(`[data-cy="leccionesAprendidas"]`).should('have.value', 'knowledgeably contrast alive');

      cy.get(`[data-cy="publicado"]`).should('not.be.checked');
      cy.get(`[data-cy="publicado"]`).click();
      cy.get(`[data-cy="publicado"]`).should('be.checked');

      cy.get(`[data-cy="publicadoEn"]`).type('2023-12-04T20:16');
      cy.get(`[data-cy="publicadoEn"]`).blur();
      cy.get(`[data-cy="publicadoEn"]`).should('have.value', '2023-12-04T20:16');

      cy.get(`[data-cy="incidente"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        postmortem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', postmortemPageUrl);
    });
  });
});
