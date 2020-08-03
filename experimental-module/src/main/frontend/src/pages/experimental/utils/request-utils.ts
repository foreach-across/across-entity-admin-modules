import { getFormData } from "./form-utils";
import { getCookie } from "./utils";
import { JsonResponse, TextResponse } from "./response-types";

export function translateResponse(toTranslate: Response): Promise<TextResponse | JsonResponse> {
  const contentType = toTranslate.headers.get("content-type");
  if (contentType?.includes("json")) {
    return toTranslate.json().then((json) => {
      const jsonResponse = toTranslate;
      // @ts-ignore
      jsonResponse.jsonContent = json;
      console.log("jsonResponse", jsonResponse);
      return jsonResponse as JsonResponse;
    });
  }

  return toTranslate.text().then((text) => {
    const textResponse = toTranslate;
    // @ts-ignore
    textResponse.textContent = text;
    console.log("textResponse", textResponse);
    return textResponse as TextResponse;
  });
}

export interface RequestConfiguration {
  method: string;
  partial?: string;
  form?: string;
  url?: string;
}

export function executeRequest(partialConfiguration: RequestConfiguration): Promise<Response> {
  const { partial, form, method, url } = partialConfiguration;

  const currentUrl = url ? url : window.location.href;
  const requestUrl = partial
    ? currentUrl.indexOf("?") === -1
      ? `${currentUrl}?_partial=${partial}`
      : `${currentUrl}&_partial=${partial}`
    : currentUrl;
  const formToSerialize = form ? $(form) : null;

  if (formToSerialize) {
    return executeFormRequest(requestUrl, method, formToSerialize);
  }
  return executeFetchRequest(requestUrl, method, formToSerialize);
}

export function executeFormRequest(url: string, method: string, form: any): Promise<Response> {
  let formConfiguration: any = {
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
  };

  if (form.attr("enctype") === "multipart/form-data") {
    formConfiguration.body = new FormData(form[0]);
    formConfiguration.processData = false;
    formConfiguration.contentType = false;
    formConfiguration.cache = false;
  } else {
    formConfiguration.body = getFormData($(form[0]));
  }

  return executeFetchRequest(url, method, formConfiguration);
}

export function executeFetchRequest(url: string, method: string, requestConfiguration?: any): Promise<Response> {
  const fetchConfiguration = $.extend(
    true,
    {},
    {
      method: method,
      headers: { "X-XSRF-Token": getCookie("XSRF-TOKEN"), "Content-Type": "application/x-www-form-urlencoded" },
    },
    requestConfiguration
  );
  return fetch(url, fetchConfiguration);
}
