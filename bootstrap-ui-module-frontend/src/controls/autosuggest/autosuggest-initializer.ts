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

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
declare const Bloodhound: any;
declare const Handlebars: any;
import $ from 'jquery';

function registerAutosuggestControl() {
  BootstrapUiModule.Controls['AutoSuggest'] = {
    /**
     * Create a Typeahead autosuggest instance from a node with a configuration object.
     */
    create(node: any, configuration: any) {
      const typeahead: any = node.find('.js-typeahead');
      const selectedValue: any = node.find('.js-typeahead-value');
      const translateUrl = function(url: any) {
        return url.replace(
          '{{controlName}}',
          encodeURIComponent(selectedValue.attr('name'))
        );
      };

      const createBloodhoundEngine = function(configuration: any) {
        const base = {
          datumTokenizer: Bloodhound.tokenizers.whitespace,
          queryTokenizer: Bloodhound.tokenizers.whitespace,
          identify: 'id',
          remote: {
            wildcard: '{{query}}',
          },
        };

        const options = $.extend(true, base, configuration);
        if (options.remote && options.remote.url) {
          options.remote.url = translateUrl(options.remote.url);
        }
        if (options.prefetch && options.prefetch.url) {
          options.prefetch.url = translateUrl(options.prefetch.url);
        }

        const engine = new Bloodhound(options);
        engine.initialize();
        return engine;
      };

      // Build datasets - bloodhound engine + typeahead config
      const datasets = configuration._datasets;
      delete configuration._datasets;

      const ttDataSets: any[] = [];
      const datasetsByName: any = {};
      const templatesByDataSet: any = {};

      // configure templates
      $(node)
        .find('script[data-template]')
        .each((_, value) => {
          const asJqueryObject = $(value);
          const templateKey = asJqueryObject.data('template');
          if (templateKey && templateKey.includes('-')) {
            const keys = templateKey.split('-');
            const templateType = keys[0];
            const datasetName = keys[1];
            if (!templatesByDataSet[datasetName]) {
              templatesByDataSet[datasetName] = {};
            }
            // merge with configuration object and then handlebars compile each value
            templatesByDataSet[datasetName][templateType] = value.innerHTML;
          }
        });

      $.each(datasets, (_, value) => {
        const engine = createBloodhoundEngine(value.bloodhound);
        delete value.bloodhound;

        const options = $.extend({ display: 'label' }, value);
        if (engine) {
          options.source = engine.ttAdapter();
        }

        // merge view element templates with configuration templates
        if (templatesByDataSet[options.name]) {
          options.templates = $.extend(
            {},
            templatesByDataSet[options.name],
            options.templates
          );
        }

        // handlebars compile each given template
        for (const template in options.templates) {
          if (options.templates.hasOwnProperty(template)) {
            options.templates[template] = Handlebars.compile(
              options.templates[template]
            );
          }
        }

        datasetsByName[value.name] = engine;

        ttDataSets.push(options);
      });

      node.data('datasets', datasetsByName);

      // Initialize the typeahead
      typeahead.typeahead(configuration, ttDataSets);

      let selected: any;
      typeahead.on('typeahead:select', (_: any, suggestion: any) => {
        selected = suggestion;
        node.find('.js-typeahead-value').val(suggestion['id']);
      });
      typeahead.on('typeahead:change', (_: any, val: any) => {
        if (!selected || val !== selected['label']) {
          typeahead.typeahead('val', '');
          node.find('.js-typeahead-value').val('');
        }
      });
    },
  };
}

/**
 * Find and activate all auto-suggest instances with Typeahead and bloodhound.
 */
function autosuggestInitializer(node: any): void {
  $('[data-bootstrapui-autosuggest]', node).each(function() {
    const configuration = $.extend({}, $(this).data('bootstrapui-autosuggest'));

    BootstrapUiModule.Controls.AutoSuggest.create($(this), configuration);
  });
}

export { registerAutosuggestControl };
export default autosuggestInitializer;
