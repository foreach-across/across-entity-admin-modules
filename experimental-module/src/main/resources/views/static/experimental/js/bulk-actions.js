EntityModule.registerInitializer(function (node) {
    $(".js-bulk-select").click(function (e) {
        e.stopPropagation();
    });

    $(".js-bulk-select-all").click(function (e) {
        const $this = $(this);
        $this.removeClass('partially-checked');
        const isChecked = $this.is(':checked');
        $('.js-bulk-select').each(function () {
            $(this).prop('checked', isChecked);
        });
        e.stopPropagation();
    });

    $('.em-sortableTable-table').on('bootstrapui.change', function (e) {
        const $selectAllElement = $('#bulkActionForm .js-bulk-select-all');
        const maxSelectedItems = $('#bulkActionForm .js-bulk-select').length;
        const selectedItemCount = $('#bulkActionForm .js-bulk-select:checked').length;
        if (selectedItemCount === maxSelectedItems) {
            $selectAllElement.removeClass("partially-checked");
            $selectAllElement.prop('checked', true);
        } else if (selectedItemCount > 0) {
            $selectAllElement.addClass("partially-checked");
            $selectAllElement.prop('checked', false);
        } else {
            $selectAllElement.removeClass("partially-checked");
            $selectAllElement.prop('checked', false);
        }
        if (selectedItemCount > 0) {
            showBulkActions(selectedItemCount);
        } else {
            hideBottomBlade();
        }
    });
});

function showBulkActions(selectedItemCount) {
    renderTemplateAndReplace('#bulk-action-set-template', "#bottom-blade", html => {
        $(html).find('.selected-count').text(`${selectedItemCount}`);
        // if ( !linkIsAllowed ) {
        //     $( html ).find( '#bulk-link-asset' )
        //         .attr( 'disabled', true )
        //         .attr( 'title', $( html ).find( '#bulk-link-asset' ).attr( 'data-disabled-text' ) );
        // }
        // if ( !unlinkIsAllowed ) {
        //     $( html ).find( '#bulk-unlink-asset' )
        //         .attr( 'disabled', true )
        //         .attr( 'title', $( html ).find( '#bulk-link-asset' ).attr( 'data-disabled-text' ) );
        // }
    });
    $('#bottom-blade').addClass("show");
    $('#bulk-delete-asset').off().click(function (e) {
        performBulkAction($('#bulkActionForm').data("em-entity"), 'delete', $('#bulkActionForm').serialize());
    });
}

function performBulkAction(entity, actionName, body) {
    fetch(`/admin/entities/${entity}/bulk/${actionName}`, {
        method: 'post',
        redirect: 'follow',
        headers: {'X-XSRF-Token': getCookie('XSRF-TOKEN'), 'Content-Type': 'application/x-www-form-urlencoded'},
        body: body
    }).then(response => {
        if (response.status === 200) {
            hideBottomBlade();
            refreshListView();
        }
    });
}

function renderTemplateAndReplace(id, destination, templateConfigurationFunction) {
    var result = document.querySelector(id).content.cloneNode(true);
    templateConfigurationFunction(result);
    $(destination).html(result);
}

function hideBottomBlade() {
    $(".main").css("position", "relative");
    $("#bottom-blade").removeClass("show");
    $("#bottom-blade").empty();
}

function refreshListView() {
    const url = window.location.href;
    fetch(url, {
        method: 'get',
        redirect: 'follow',
        headers: {'X-XSRF-Token': getCookie('XSRF-TOKEN')}
    })
        .then(convertResponseToText)
        .then(html => {
            const requestedContent = $(html).find('.em-view-listView').html();
            const $listViewBody = $('.em-view-listView');
            $listViewBody.html(requestedContent);
            EntityModule.initializeFormElements($listViewBody);
        })
        .catch(handleError)
}
