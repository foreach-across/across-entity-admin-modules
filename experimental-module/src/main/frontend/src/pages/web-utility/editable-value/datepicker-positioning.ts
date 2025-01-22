function responsiveTableSupportingPlace(e: any) {
  let self = (e && e.data && e.data.picker) || this,
    vertical = self._options.widgetPositioning.vertical,
    horizontal = self._options.widgetPositioning.horizontal,
    parent = null;
  const cta = (self.component && self.component.length ? self.component : self._element)[0].getBoundingClientRect();
  if (self._options.widgetParent) {
    parent = self._options.widgetParent.append(self.widget);
  } else if (self._element.is("input")) {
    parent = self._element.after(self.widget).parent();
  } else if (self._options.inline) {
    parent = self._element.append(self.widget);
    return;
  } else {
    parent = self._element;
    self._element.children().first().after(self.widget);
  }

  // Top and bottom logic
  if (vertical === "auto") {
    //noinspection JSValidateTypes
    if (
      cta.top + self.widget.height() * 1.5 >= $(window).height()! + $(window).scrollTop()! &&
      self.widget.height() + self._element.outerHeight() < cta.top
    ) {
      vertical = "top";
    } else {
      vertical = "bottom";
    }
  }

  // Left and right logic
  if (horizontal === "auto") {
    if (
      parent.width() < cta.left + self.widget.outerWidth() / 2 &&
      cta.left + self.widget.outerWidth() > $(window).width()!
    ) {
      horizontal = "right";
    } else {
      horizontal = "left";
    }
  }

  if (vertical === "top") {
    self.widget.addClass("top").removeClass("bottom");
  } else {
    self.widget.addClass("bottom").removeClass("top");
  }

  if (horizontal === "right") {
    self.widget.addClass("float-right");
  } else {
    self.widget.removeClass("float-right");
  }

  if (parent.css("position") !== "relative") {
    const possibleParent = parent
      .parents()
      .filter(function () {
        return $(this).css("position") === "relative";
      })
      .first();
    if (possibleParent.length) {
      parent = possibleParent;
    }
  }

  const parentIsRelative = parent.css("position") === "relative";

  const parentPosition = parent[0].getBoundingClientRect();
  const elementPosition = self._element[0].getBoundingClientRect();

  let top, bottom, left, right;

  if (parentIsRelative) {
    top = vertical === "top" ? "auto" : elementPosition.y + elementPosition.height - parentPosition.y;
    bottom = vertical === "top" ? parentPosition.height - elementPosition.y + parentPosition.y : "auto";
    // todo
    left = horizontal === "left" ? elementPosition.x - parentPosition.x : "auto";
    right = horizontal === "left" ? "auto" : parentPosition.width - elementPosition.x + parentPosition.y;
  } else {
    top = vertical === "top" ? "auto" : elementPosition.bottom;
    bottom = vertical === "top" ? elementPosition.top : "auto";
    left = horizontal === "left" ? elementPosition.left : "auto";
    right = horizontal === "left" ? "auto" : elementPosition.right;
  }

  const positioning = {
    top: top === "auto" ? top : top + "px",
    bottom: bottom === "auto" ? bottom : bottom + "px",
    left: left === "auto" ? left : left + "px",
    right: right === "auto" ? right : right + "px",
  };

  self.widget.css(positioning);
}

function customizeDateTimePicker($dpNode: any) {
  if (!dpPrototypeUpdated) {
    const dp = $dpNode.data("datetimepicker");
    const prototype = Object.getPrototypeOf(dp);
    const originalFn = prototype._place;
    prototype._place = function (e: any) {
      var self = (e && e.data && e.data.picker) || this;
      if (self._element.closest(".table-responsive").length > 0) {
        responsiveTableSupportingPlace.bind(this)(e);
      } else {
        originalFn.bind(this)(e);
      }
    };
    dpPrototypeUpdated = true;
  }
}

let dpPrototypeUpdated: boolean = false;
EntityModule.registerInitializer(function (node) {
  if (!dpPrototypeUpdated) {
    const $node = $(node);
    const selector = '[data-bootstrapui-adapter-type="datetime"]';
    if ($node.is(selector)) {
      customizeDateTimePicker($node);
    } else {
      $(selector, node).each(function () {
        const $this = $(this);
        customizeDateTimePicker($this);
      });
    }
  }
});
