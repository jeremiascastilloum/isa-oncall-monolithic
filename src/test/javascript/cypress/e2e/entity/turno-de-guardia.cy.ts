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

describe('TurnoDeGuardia e2e test', () => {
  const turnoDeGuardiaPageUrl = '/turno-de-guardia';
  let username: string;
  let password: string;
  // const turnoDeGuardiaSample = {"desde":"2023-12-04T00:07:34.228Z","hasta":"2023-12-04T04:29:52.820Z"};

  let turnoDeGuardia;
  // let rotacion;
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
      url: '/api/rotacions',
      body: {"nombre":"mmm fully","tipo":"SEMANAL","zonaHoraria":"ew quietly","activa":true},
    }).then(({ body }) => {
      rotacion = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"Pablo.IbarraGuillen81","firstName":"Adriana","lastName":"Guzmán Arreola","email":"Marisol.RoldanMontez@gmail.com","langKey":"loyally fu","imageUrl":"in-joke"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/turno-de-guardias+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/turno-de-guardias').as('postEntityRequest');
    cy.intercept('DELETE', '/api/turno-de-guardias/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/rotacions', {
      statusCode: 200,
      body: [rotacion],
    });

    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

  });
   */

  afterEach(() => {
    if (turnoDeGuardia) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/turno-de-guardias/${turnoDeGuardia.id}`,
      }).then(() => {
        turnoDeGuardia = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (rotacion) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/rotacions/${rotacion.id}`,
      }).then(() => {
        rotacion = undefined;
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

  it('TurnoDeGuardias menu should load TurnoDeGuardias page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('turno-de-guardia');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TurnoDeGuardia').should('exist');
    cy.location('pathname').should('eq', turnoDeGuardiaPageUrl);
  });

  describe('TurnoDeGuardia page', () => {
    it('should have translated page title', () => {
      cy.visit(turnoDeGuardiaPageUrl);
      cy.getEntityHeading('TurnoDeGuardia').should('not.contain', 'oncallApp.turnoDeGuardia.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(turnoDeGuardiaPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TurnoDeGuardia page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${turnoDeGuardiaPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('TurnoDeGuardia');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', turnoDeGuardiaPageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/turno-de-guardias',
          body: {
            ...turnoDeGuardiaSample,
            rotacion: rotacion,
            responsable: user,
          },
        }).then(({ body }) => {
          turnoDeGuardia = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/turno-de-guardias+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/turno-de-guardias?page=0&size=20>; rel="last",<http://localhost/api/turno-de-guardias?page=0&size=20>; rel="first"',
              },
              body: [turnoDeGuardia],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(turnoDeGuardiaPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(turnoDeGuardiaPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details TurnoDeGuardia page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('turnoDeGuardia');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', turnoDeGuardiaPageUrl);
      });

      it('edit button click should load edit TurnoDeGuardia page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TurnoDeGuardia');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', turnoDeGuardiaPageUrl);
      });

      it('edit button click should load edit TurnoDeGuardia page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TurnoDeGuardia');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', turnoDeGuardiaPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of TurnoDeGuardia', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('turnoDeGuardia').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', turnoDeGuardiaPageUrl);

        turnoDeGuardia = undefined;
      });
    });
  });

  describe('new TurnoDeGuardia page', () => {
    beforeEach(() => {
      cy.visit(turnoDeGuardiaPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TurnoDeGuardia');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of TurnoDeGuardia', () => {
      cy.get(`[data-cy="desde"]`).type('2023-12-04T10:04');
      cy.get(`[data-cy="desde"]`).blur();
      cy.get(`[data-cy="desde"]`).should('have.value', '2023-12-04T10:04');

      cy.get(`[data-cy="hasta"]`).type('2023-12-04T07:32');
      cy.get(`[data-cy="hasta"]`).blur();
      cy.get(`[data-cy="hasta"]`).should('have.value', '2023-12-04T07:32');

      cy.get(`[data-cy="esReemplazo"]`).should('not.be.checked');
      cy.get(`[data-cy="esReemplazo"]`).click();
      cy.get(`[data-cy="esReemplazo"]`).should('be.checked');

      cy.get(`[data-cy="nota"]`).type('piglet sprinkles');
      cy.get(`[data-cy="nota"]`).should('have.value', 'piglet sprinkles');

      cy.get(`[data-cy="rotacion"]`).select(1);
      cy.get(`[data-cy="responsable"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        turnoDeGuardia = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', turnoDeGuardiaPageUrl);
    });
  });
});
