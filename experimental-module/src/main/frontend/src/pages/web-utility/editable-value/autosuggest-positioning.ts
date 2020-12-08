function repositionSuggestionList($this: any) {
  $this.on("typeahead:open", function () {
    const $menu = $this.find(".tt-menu");
    let $parent = $(".pcs-body-section");
    $parent.append($menu);

    let relativeParent = $menu
      .parents()
      .filter(function () {
        return $(this).css("position") === "relative";
      })
      .first();
    if (relativeParent.length > 0) {
      $parent = relativeParent;
    }

    const parentIsRelative = $parent.css("position") === "relative";

    const $input = $this.find(".tt-input");
    const elementPosition = $input[0].getBoundingClientRect();
    const parentPosition = $parent[0].getBoundingClientRect();

    $menu.show();

    let positioning = {
      top:
        (parentIsRelative ? elementPosition.y + elementPosition.height - parentPosition.y : elementPosition.y) + "px",
      left: (parentIsRelative ? elementPosition.x - parentPosition.x : elementPosition.x) + "px",
    };
    $menu.css(positioning);
    $menu.css("max-width", $this[0].getBoundingClientRect().width);
    $(this).data("ax-dropdown-menu", $menu);
  });
  $this.on("typeahead:close", function () {
    $this.append($(this).data("ax-dropdown-menu"));
    var $menu = $this.find(".tt-menu");
    // reset to original values
    $menu.css("top", "100%");
    $menu.css("left", "0px");
  });
}

EntityModule.registerInitializer(function (node) {
  $("[data-bootstrapui-adapter-type='autosuggest']", node).each(function () {
    const $this = $(this);
    if ($this.closest(".table-responsive")) {
      repositionSuggestionList($this);
    }
  });
});

interface TypeaheadValueHolder {
  selectedValue: any;
  inputValue: any;
  hiddenInputValue: any;
}

EntityModule.registerInitializer(function (node) {
  $(".axbum-typeahead", node).each(function () {
    const $this = $(this);
    const typeAheadInput = $(".js-typeahead.tt-input", $this);
    const hiddenInput = $(".js-typeahead-value", $this);
    const valueHolder: TypeaheadValueHolder = {
      selectedValue: null,
      hiddenInputValue: null,
      inputValue: null,
    };

    $this.on("typeahead:active", function () {
      valueHolder.selectedValue = typeAheadInput.typeahead("val");
      valueHolder.hiddenInputValue = hiddenInput.attr("value");
      valueHolder.inputValue = typeAheadInput.val();
    });

    $this.on("typeahead:close", function () {
      const selectedValueIsSame = typeAheadInput.typeahead("val") === valueHolder.selectedValue;
      const inputValueIsSame = typeAheadInput.val() === valueHolder.inputValue;
      const hiddenInputIsSame = typeAheadInput.val() === valueHolder.hiddenInputValue;
      if (inputValueIsSame && selectedValueIsSame && !hiddenInputIsSame) {
        typeAheadInput.typeahead("val", valueHolder.selectedValue);
        hiddenInput.val(valueHolder.hiddenInputValue);
      }
    });
  });
});
