/*
 * Copyright 2019 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import adapterUtils from '../../support/utils/control-adapters';

describe('ControlAdapter - Select', function() {
  const multiValueSelectTests = function(selector) {
    it('value holds option label, option value and option item', function() {
      cy.get(selector)
        .select('2', { force: true })
        .then(select => {
          adapterUtils.assertAdapterHoldsAmountOfValues(select, 1);
          adapterUtils.assertAdapterValueSelected(
            select,
            0,
            'Two',
            '2',
            select.find('option:selected')[0]
          );

          const adapter = adapterUtils.getAdapterForElement(select);
          adapter.reset();
        });
    });

    it('modifying value', function() {
      cy.get(selector)
        .then(select => adapterUtils.assertAdapterNoValueSelected(select))
        .select(['1', '2'], { force: true })
        .then(select => {
          adapterUtils.assertAdapterHoldsAmountOfValues(select, 2);
          adapterUtils.assertAdapterValueSelected(select, 0, 'One', '1');
          adapterUtils.assertAdapterValueSelected(select, 1, 'Two', '2');
        });
    });

    it('reset selects initial value', function() {
      cy.get(selector)
        .select(['2', 'Three'], { force: true })
        .then(select => {
          adapterUtils.assertAdapterValueSelected(select, 0, 'Two', '2');
          adapterUtils.assertAdapterValueSelected(select, 1, '3', 'Three');

          adapterUtils.getAdapterForElement(select).reset();
          adapterUtils.assertAdapterNoValueSelected(select);
        });
    });
  };

  const singleValueSelectTests = function(selector) {
    it('value holds option label, option value and option item', function() {
      cy.get(selector).then(select => {
        adapterUtils.assertAdapterValueSelected(
          select,
          0,
          'One',
          '1',
          Cypress.$('option:selected', select)[0]
        );
      });
    });

    it('modifying value', function() {
      cy.get(selector)
        .then(select =>
          adapterUtils.assertAdapterValueSelected(select, 0, 'One', '1')
        )
        .select('2', { force: true })
        .then(select =>
          adapterUtils.assertAdapterValueSelected(select, 0, 'Two', '2')
        );
    });

    it('reset selects initial value', function() {
      cy.get(selector)
        .select('Three', { force: true })
        .then(select => {
          adapterUtils.assertAdapterValueSelected(select, 0, '3', 'Three');
          adapterUtils.getAdapterForElement(select).reset();
          adapterUtils.assertAdapterValueSelected(select, 0, 'One', '1');
        });
    });
  };

  const baseSelectTests = function(selector, eventName) {
    afterEach('reset adapter', function() {
      cy.get(selector).then(select => {
        adapterUtils.getAdapterForElement(select).reset();
      });
    });

    it('adapter exists', function() {
      adapterUtils.assertThatAdapterExists(selector);
    });

    it('does not have underlying control adapters', function() {
      adapterUtils.assertHasUnderlyingControlAdapters(selector, 0);
    });

    it(`bootstrapui.change is fired when a ${eventName} event is emitted on the select`, function() {
      cy.get(selector).then(select => {
        adapterUtils.assertThatBootstrapUiChangeIsTriggeredOn(
          select,
          eventName
        );
      });
    });
  };

  before(function() {
    cy.visit('/control-adapters');
  });

  describe('single select', () => {
    const selector = '#ca-select';
    baseSelectTests(selector, 'change');
    singleValueSelectTests(selector);
  });

  describe('multi select', () => {
    const selector = '#ca-multi-select';
    baseSelectTests(selector, 'change');
    multiValueSelectTests(selector);
  });

  describe('bootstrap select', () => {
    const selector = '#ca-bootstrap-select';
    baseSelectTests(selector, 'changed.bs.select');
    singleValueSelectTests(selector);
  });

  describe('bootstrap multi select', () => {
    const selector = '#ca-bootstrap-multi-select';
    baseSelectTests(selector, 'changed.bs.select');
    multiValueSelectTests(selector);
  });
});
