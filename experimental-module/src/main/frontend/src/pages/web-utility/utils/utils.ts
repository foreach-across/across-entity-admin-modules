export function getCookie(name: string) {
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length === 2) {
    return parts.pop()!.split(";").shift();
  }
  return undefined;
}

export const ax = {
  log: {
    sessionStorageKey: "ax.log.enabled",
    enabled: false,
    prefix: "[AX] ",

    enable() {
      this.enabled = true;
      window.sessionStorage && window.sessionStorage.setItem(this.sessionStorageKey, "true");
    },
    disable() {
      this.enabled = false;
      window.sessionStorage && window.sessionStorage.removeItem(this.sessionStorageKey);
    },
    info(message: any, params?: any[]) {
      this.send(console.info, message, params);
    },
    debug(message: any, params?: any[]) {
      this.send(console.debug, message, params);
    },
    error(message: any, params?: any[]) {
      this.send(console.error, message, params);
    },
    group(title: any, params?: any[]) {
      this.send(console.group, title, params);
    },
    groupCollapsed(title: any, params?: any[]) {
      this.send(console.groupCollapsed, title, params);
    },
    groupEnd() {
      this.send(console.groupEnd);
    },
    send(target: Function, message: any, params?: any[]) {
      if (this.enabled) {
        target(this.prefix + message, params ? params : "");
      }
    },
  },
};

if (window.sessionStorage && sessionStorage.getItem(ax.log.sessionStorageKey) === "true") {
  ax.log.enable();
}
