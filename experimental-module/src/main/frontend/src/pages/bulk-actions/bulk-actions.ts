import "./bulk-actions.scss";

EntityModule.registerInitializer(function (node) {
  let table = $(".em-sortableTable-table", node);
  if (table.data("bootstrapui-adapter-type")) {
    const containerAdapter = table.data("bootstrapui-adapter");

    $($(".js-exm-bulk-select-all", node).data("bootstrapui-adapter").getTarget()).on("bootstrapui.change", function (
      event,
      adapter
    ) {
      let allSelectorValue = adapter.getValue();
      const shouldSelect = allSelectorValue.length > 0;
      $("[data-bootstrapui-adapter-type]:not(.js-exm-bulk-select-all)", containerAdapter.getTarget()).each(function (
        idx,
        element
      ) {
        $(element).data("bootstrapui-adapter").selectValue(shouldSelect);
      });
    });
  }
});

// $( containerAdapter.getTarget() ).on( "bootstrapui.change", function( event, adapter ) {
//     let selectedValues = adapter.getValue().filter( ( value ) => value.context.name !== "exm-bulk-actions-all" );
//
//     if ( selectedValues.length > 0 ) {
//         $( "#exm-bulk-actions-all", node ).data( "bootstrapui-adapter" ).selectValue( maxSelection === selectedValues.length );
//     }
//
//     if ( selectedValues.length > 0 ) {
//         showBulkActions( selectedValues.length );
//     }
//     else {
//         hideBottomBlade();
//     }
// } );

// function showBulkActions( selectedItemCount ) {
//     renderTemplateAndReplace( '#bulk-action-set-template', "#bottom-blade", html => {
//         $( html ).find( '.selected-count' ).text( `${selectedItemCount}` );
//         // if ( !linkIsAllowed ) {
//         //     $( html ).find( '#bulk-link-asset' )
//         //         .attr( 'disabled', true )
//         //         .attr( 'title', $( html ).find( '#bulk-link-asset' ).attr( 'data-disabled-text' ) );
//         // }
//         // if ( !unlinkIsAllowed ) {
//         //     $( html ).find( '#bulk-unlink-asset' )
//         //         .attr( 'disabled', true )
//         //         .attr( 'title', $( html ).find( '#bulk-link-asset' ).attr( 'data-disabled-text' ) );
//         // }
//     } );
//     $( '#bottom-blade' ).addClass( "show" );
//     $( '#bulk-delete-asset' ).off().click( function( e ) {
//         performBulkAction( $( '#bulkActionForm' ).data( "em-entity" ), 'delete', $( '#bulkActionForm' ).serialize() );
//     } );
// }

// function performBulkAction( entity, actionName, body ) {
//     fetch( `/admin/entities/${entity}/bulk/${actionName}`, {
//         method: 'post',
//         redirect: 'follow',
//         headers: {'X-XSRF-Token': getCookie( 'XSRF-TOKEN' ), 'Content-Type': 'application/x-www-form-urlencoded'},
//         body: body
//     } ).then( response => {
//         if ( response.status === 200 ) {
//             hideBottomBlade();
//             refreshListView();
//         }
//     } );
// }
//
// function renderTemplateAndReplace( id, destination, templateConfigurationFunction ) {
//     var result = document.querySelector( id ).content.cloneNode( true );
//     templateConfigurationFunction( result );
//     $( destination ).html( result );
// }
//
// function hideBottomBlade() {
//     $( ".main" ).css( "position", "relative" );
//     $( "#bottom-blade" ).removeClass( "show" );
//     $( "#bottom-blade" ).empty();
// }

// function refreshListView() {
//     const url = window.location.href;
//     fetch( url, {
//         method: 'get', redirect: 'follow', headers: {'X-XSRF-Token': getCookie( 'XSRF-TOKEN' )}
//     } )
//             .then( convertResponseToText )
//             .then( html => {
//                 const requestedContent = $( html ).find( '.em-view-listView' ).html();
//                 const $listViewBody = $( '.em-view-listView' );
//                 $listViewBody.html( requestedContent );
//                 EntityModule.initializeFormElements( $listViewBody );
//             } )
//             .catch( handleError )
// }
