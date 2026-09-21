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

describe('Notificacion e2e test', () => {
  const notificacionPageUrl = '/notificacion';
  let username: string;
  let password: string;
  // const notificacionSample = {"canal":"PUSH","destino":"amidst","estado":"ENVIADA","intentos":5};

  let notificacion;
  // let incidente;
  // let user;

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
      url: '/api/incidentes',
      body: {"titulo":"whoever","descripcion":"instead","severidad":"SEV3","estado":"RESUELTO","detectadoEn":"2023-12-03T23:09:16.966Z","reconocidoEn":"2023-12-04T05:06:30.568Z","mitigadoEn":"2023-12-04T22:04:34.989Z","resueltoEn":"2023-12-04T10:53:47.317Z","usuariosAfectados":17371,"cumplioObjetivo":true},
    }).then(({ body }) => {
      incidente = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"JoseEmilio.VillasenorMarroquin32","firstName":"Jorge Luis","lastName":"Flores Santacruz","email":"Ivan64@gmail.com","langKey":"lest softl","imageUrl":"drat uh-huh"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/notificacions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/notificacions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/notificacions/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/incidentes', {
      statusCode: 200,
      body: [incidente],
    });

    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

  });
   */

  afterEach(() => {
    if (notificacion) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/notificacions/${notificacion.id}`,
      }).then(() => {
        notificacion = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (incidente) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/incidentes/${incidente.id}`,
      }).then(() => {
        incidente = undefined;
      });
    }
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
  });
   */

  it('Notificacions menu should load Notificacions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('notificacion');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Notificacion').should('exist');
    cy.location('pathname').should('eq', notificacionPageUrl);
  });

  describe('Notificacion page', () => {
    it('should have translated page title', () => {
      cy.visit(notificacionPageUrl);
      cy.getEntityHeading('Notificacion').should('not.contain', 'oncallApp.notificacion.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(notificacionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Notificacion page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${notificacionPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Notificacion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', notificacionPageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/notificacions',
          body: {
            ...notificacionSample,
            incidente: incidente,
            destinatario: user,
          },
        }).then(({ body }) => {
          notificacion = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/notificacions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/notificacions?page=0&size=20>; rel="last",<http://localhost/api/notificacions?page=0&size=20>; rel="first"',
              },
              body: [notificacion],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(notificacionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(notificacionPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Notificacion page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('notificacion');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', notificacionPageUrl);
      });

      it('edit button click should load edit Notificacion page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Notificacion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', notificacionPageUrl);
      });

      it('edit button click should load edit Notificacion page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Notificacion');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', notificacionPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of Notificacion', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('notificacion').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', notificacionPageUrl);

        notificacion = undefined;
      });
    });
  });

  describe('new Notificacion page', () => {
    beforeEach(() => {
      cy.visit(notificacionPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Notificacion');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of Notificacion', () => {
      cy.get(`[data-cy="canal"]`).select('WEBHOOK');

      cy.get(`[data-cy="destino"]`).type('instruction');
      cy.get(`[data-cy="destino"]`).should('have.value', 'instruction');

      cy.get(`[data-cy="estado"]`).select('ENVIADA');

      cy.get(`[data-cy="enviadaEn"]`).type('2023-12-04T19:18');
      cy.get(`[data-cy="enviadaEn"]`).blur();
      cy.get(`[data-cy="enviadaEn"]`).should('have.value', '2023-12-04T19:18');

      cy.get(`[data-cy="intentos"]`).type('7');
      cy.get(`[data-cy="intentos"]`).should('have.value', '7');

      cy.get(`[data-cy="errorMensaje"]`).type('because');
      cy.get(`[data-cy="errorMensaje"]`).should('have.value', 'because');

      cy.get(`[data-cy="incidente"]`).select(1);
      cy.get(`[data-cy="destinatario"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        notificacion = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', notificacionPageUrl);
    });
  });
});
