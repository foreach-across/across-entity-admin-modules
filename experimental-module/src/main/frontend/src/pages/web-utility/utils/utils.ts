export function getCookie(name: string) {
  let matches = document.cookie.match(
    new RegExp("(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, "\\$1") + "=([^;]*)")
  );
  return matches ? decodeURIComponent(matches[1]) : undefined;
}

export function setCookie(name: string, value: any, options = {} as any) {
  options = {
    path: "/", // add other defaults here if necessary
    ...options,
  };

  if (options.expires instanceof Date) {
    options.expires = options.expires.toUTCString();
  }

  let updatedCookie = encodeURIComponent(name) + "=" + encodeURIComponent(value);

  for (let optionKey in options) {
    updatedCookie += "; " + optionKey;
    let optionValue = options[optionKey];
    if (optionValue !== true) {
      updatedCookie += "=" + optionValue;
    }
  }

  document.cookie = updatedCookie;
}

export function convertResponseToText(response: any) {
  if (response.redirected) {
    window.location.href = response.url + "&redirectUrl=" + encodeURI(window.location.href);
    return Promise.reject(null);
  } else if (response.ok) {
    return response.text();
  } else {
    return Promise.reject(null);
  }
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
