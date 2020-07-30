import { convertResponseToText, handleError } from "../experimental/utils/utils";
import { executeFormRequest, executeRequest } from "../experimental/utils/request-utils";

EntityModule.registerInitializer(function (node) {
  $("[data-modal-load]", node).each((idx: number, element: Node) => {
    initializeModalConfiguration(element);
  });
});

function initializeModalConfiguration(element: Node) {
  const $node = $(element);
  $node.on("click", (e: any) => {
    e.preventDefault();

    const config = $.extend(true, {}, $node.data("modal-load"));
    const targetModalId = config.target;

    $(targetModalId).one("show.bs.modal", (event: any) => {
      const handlerResults: any[] = [];
      config.content.forEach((action: any) => {
        handlerResults.push(ExperimentalModule.actionFactory.handle(action, {}));
      });
      Promise.all(handlerResults).then(() => EntityModule.initializeFormElements($(targetModalId)));
    });
  });
}

// function initializeModalConfiguration( element: Node )
// {
//     const $node = $( element );
//     $node.on( "click", ( e: any ) => {
//         e.preventDefault();
//
//         const config = $.extend( true, {}, $node.data( "modal-load" ) );
//         const targetModalId = "#" + config.target;
//
//         $( targetModalId ).one( "show.bs.modal", ( event: any ) => {
//             executeFetchRequest( config.content.url, config.content.method )
//                 .then( convertResponseToText )
//                 .then( ( html ) => {
//                     replaceModalContent( targetModalId, html );
//                     $( targetModalId ).trigger( "modal:content-loaded" );
//                 } )
//                 .catch( handleError );
//         } );
//
//         // configureModalSubmission( targetModalId, config );
//
//         $( targetModalId ).on( "hide.bs.modal", ( e: any ) => {
//             cleanUpModal( targetModalId );
//         } );
//     } );
// }

function replaceModalContent(target: string, content: any) {
  $(target + " .modal-body").html("</form>" + content);
  EntityModule.initializeFormElements($(target + " .modal-body"));
}

function configureModalSubmission(target: string, config: any) {
  const submissionConfiguration = config.submission;
  let $submissionBtn = $(target + " .js-modal-submit");
  $submissionBtn.prop("disabled", false);

  $submissionBtn.off().on("click", (e: any) => {
    // showSpinner( "#modal-submit" );
    e.preventDefault();
    e.stopPropagation();

    $(this).prop("disabled", true);
    $(this).find(".loading").removeClass("d-none");
    const requestConfiguration = $.extend(
      {},
      {
        type: submissionConfiguration.type,
        url: submissionConfiguration.url,
        method: submissionConfiguration.method,
        form: `${target} form`,
      }
    );
    executeRequest(requestConfiguration)
      .then((response: any) => {
        // hideSpinner( "#modal-submit" );
        if (response.redirected) {
          return Promise.resolve({ replace: false });
        } else {
          const contentType = response.headers.get("content-type");
          if (contentType.includes("application/json")) {
            return response.json();
          } else {
            return response.text().then((html: string) => {
              return {
                content: html,
                replace: true,
              };
            });
          }
        }
      })
      .then((data: any) => {
        $(target).modal("hide");
        if (data.success) {
          // $.each( data.properties, function ( propertyName, propertyData ) {
          //     updatePropertyData( propertyName, propertyData );
          // } );
        } else {
          if (data && data.replace) {
            replaceModalContent(target, data.content);
          } else {
            $(target).modal("hide");
            if (config.refreshOnClose && config.refreshOnClose) {
              handleRefreshOnClose(config.refreshOnClose);
            }
          }
        }
        $(target).trigger("modal:action-complete", data);
      })
      .catch(handleError);
  });
}

/*
    todo.stg: create a global javascript object which holds functions we can execute.
    This way we can reuse functionality across pages/components
 */
function handleRefreshOnClose(config: any) {
  // todo.stg can we extract the unpoly specifics in h.then logic?
  const executed = executeRequest(config);
  if (!config.type.startsWith("unpoly")) {
    if (Array.isArray(executed)) {
      executed.forEach((data) => {
        if (!data.partial.type.startsWith("unpoly")) {
          refreshPartial(data.promise);
        }
      });
    } else {
      refreshPartial(executed);
    }
  }
}

function cleanUpModal(target: string) {
  $(target + " .modal-body").html("");
  let $submissionBtn = $(target + " .js-modal-submit");
  $submissionBtn.prop("disabled", false);
  $submissionBtn.find(".loading").addClass("d-none");
}

/*
    todo.stg: create a global javascript object which holds functions we can execute.
    This way we can reuse functionality across pages/components
 */
function refreshPartial(partialConfiguration: any) {
  const { partial, form, method, url, target } = partialConfiguration;
  const baseUrl = url ? url : window.location.href.split("?")[0];
  const requestUrl = `${baseUrl}?_partial=${partial}`;
  const formToSerialize = form ? $(form) : null;
  executeFormRequest(requestUrl, method, formToSerialize)
    .then(convertResponseToText)
    .then((html: string) => {
      $(target).replaceWith(html);
      EntityModule.initializeFormElements($(target));
    });
}
