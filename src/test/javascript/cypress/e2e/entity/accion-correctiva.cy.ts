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

describe('AccionCorrectiva e2e test', () => {
  const accionCorrectivaPageUrl = '/accion-correctiva';
  let username: string;
  let password: string;
  // const accionCorrectivaSample = {"descripcion":"underneath out amidst","prioridad":"BAJA","estado":"COMPLETADA"};

  let accionCorrectiva;
  // let postmortem;

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
      url: '/api/postmortems',
      body: {"titulo":"meanwhile rosy duh","resumen":"provided what","causaRaiz":"psst","lineaDeTiempo":"citizen","leccionesAprendidas":"modulo mockingly failing","publicado":true,"publicadoEn":"2023-12-04T02:48:53.203Z"},
    }).then(({ body }) => {
      postmortem = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/accion-correctivas+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/accion-correctivas').as('postEntityRequest');
    cy.intercept('DELETE', '/api/accion-correctivas/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/postmortems', {
      statusCode: 200,
      body: [postmortem],
    });

    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (accionCorrectiva) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/accion-correctivas/${accionCorrectiva.id}`,
      }).then(() => {
        accionCorrectiva = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('AccionCorrectivas menu should load AccionCorrectivas page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('accion-correctiva');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AccionCorrectiva').should('exist');
    cy.location('pathname').should('eq', accionCorrectivaPageUrl);
  });

  describe('AccionCorrectiva page', () => {
    it('should have translated page title', () => {
      cy.visit(accionCorrectivaPageUrl);
      cy.getEntityHeading('AccionCorrectiva').should('not.contain', 'oncallApp.accionCorrectiva.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(accionCorrectivaPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AccionCorrectiva page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${accionCorrectivaPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('AccionCorrectiva');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', accionCorrectivaPageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/accion-correctivas',
          body: {
            ...accionCorrectivaSample,
            postmortem: postmortem,
          },
        }).then(({ body }) => {
          accionCorrectiva = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/accion-correctivas+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [accionCorrectiva],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(accionCorrectivaPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(accionCorrectivaPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details AccionCorrectiva page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('accionCorrectiva');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', accionCorrectivaPageUrl);
      });

      it('edit button click should load edit AccionCorrectiva page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AccionCorrectiva');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', accionCorrectivaPageUrl);
      });

      it('edit button click should load edit AccionCorrectiva page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AccionCorrectiva');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', accionCorrectivaPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of AccionCorrectiva', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('accionCorrectiva').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', accionCorrectivaPageUrl);

        accionCorrectiva = undefined;
      });
    });
  });

  describe('new AccionCorrectiva page', () => {
    beforeEach(() => {
      cy.visit(accionCorrectivaPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AccionCorrectiva');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of AccionCorrectiva', () => {
      cy.get(`[data-cy="descripcion"]`).type('phrase');
      cy.get(`[data-cy="descripcion"]`).should('have.value', 'phrase');

      cy.get(`[data-cy="prioridad"]`).select('ALTA');

      cy.get(`[data-cy="estado"]`).select('COMPLETADA');

      cy.get(`[data-cy="fechaLimite"]`).type('2023-12-04');
      cy.get(`[data-cy="fechaLimite"]`).blur();
      cy.get(`[data-cy="fechaLimite"]`).should('have.value', '2023-12-04');

      cy.get(`[data-cy="ticketUrl"]`).type('oof gown rowdy');
      cy.get(`[data-cy="ticketUrl"]`).should('have.value', 'oof gown rowdy');

      cy.get(`[data-cy="postmortem"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        accionCorrectiva = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', accionCorrectivaPageUrl);
    });
  });
});
