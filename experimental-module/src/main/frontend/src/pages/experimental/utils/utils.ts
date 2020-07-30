export function getCookie(name: string) {
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length === 2) {
    return parts.pop()!.split(";").shift();
  }
  return undefined;
}

export function convertResponseToText(response: any) {
  if (response.redirected) {
    window.location.href = response.url + "&redirectUrl=" + encodeURI(window.location.href);
    return Promise.reject(null);
  } else {
    return response.text();
  }
}

export function convertResponseToTextWithoutSettingRedirectUrl(response: any) {
  if (response.redirected) {
    window.location.href = response.url;
    return Promise.reject(null);
  } else {
    return response.text();
  }
}

export function convertResponseToJson(response: any) {
  if (response.redirected) {
    window.location.href = response.url + "&redirectUrl=" + encodeURI(window.location.href);
  } else {
    return response.json();
  }
}

export function handleError(e: any) {
  console.error("Unexpected error", e);
}
