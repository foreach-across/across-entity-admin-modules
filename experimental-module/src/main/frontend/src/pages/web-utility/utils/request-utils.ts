import { getCookie } from "./utils";
import { JsonResponse, TextResponse } from "./response-types";

export function translateResponse(toTranslate: Response): Promise<TextResponse | JsonResponse> {
  const contentType = toTranslate.headers.get("content-type");
  if (contentType?.includes("json")) {
    return toTranslate.json().then((json) => {
      const jsonResponse = toTranslate;
      // @ts-ignore
      jsonResponse.jsonContent = json;
      return jsonResponse as JsonResponse;
    });
  }

  return toTranslate.text().then((text) => {
    const textResponse = toTranslate;
    // @ts-ignore
    textResponse.textContent = text;
    return textResponse as TextResponse;
  });
}

export interface RequestConfiguration {
  method: string;
  partial?: string;
  form?: string;
  url?: string;
  copyOriginalRequestParameters?: boolean;
  additionalQueryParameters?: any;
  responseUrl?: string;
  requestConfig?: any;
}

export const UPDATE_ID_VALUE = "$updateId";

export function executeRequest(partialConfiguration: RequestConfiguration): Promise<Response> {
  const {
    partial,
    form,
    method,
    url,
    copyOriginalRequestParameters,
    requestConfig,
    responseUrl,
  } = partialConfiguration;

  const baseUrl = url ? url : window.location.href.split("?")[0];
  const formToSerialize = form ? $(form) : undefined;
  let requestUrl = baseUrl;
  if (partial) {
    requestUrl = baseUrl.indexOf("?") === -1 ? `${baseUrl}?_partial=${partial}` : `${baseUrl}&_partial=${partial}`;
  }

  const additionalQueryParameters = { ...partialConfiguration.additionalQueryParameters };

  if (additionalQueryParameters) {
    Object.keys(additionalQueryParameters).forEach((queryParamName) => {
      const value = additionalQueryParameters[queryParamName];

      if (responseUrl && value === UPDATE_ID_VALUE) {
        const id = getIdFromUpdateUrl(responseUrl);

        if (id !== null) {
          additionalQueryParameters[queryParamName] = id;
        }
      }
    });
  }

  if (copyOriginalRequestParameters) {
    const queryParams = new URLSearchParams(window.location.search);

    // @ts-ignore
    for (const entry of queryParams.entries()) {
      const name = entry[0];
      const value = entry[1];

      if (!additionalQueryParameters[name]) {
        additionalQueryParameters[name] = value;
      }
    }
  }

  if (typeof formToSerialize !== "undefined") {
    // @ts-ignore
    return executeFormRequest(requestUrl, method, formToSerialize, requestConfig, additionalQueryParameters);
  }

  if (additionalQueryParameters) {
    Object.keys(additionalQueryParameters).forEach((queryParamName) => {
      const value = additionalQueryParameters[queryParamName];
      requestUrl += "&" + queryParamName + "=" + value;
    });
  }

  return executeFetchRequest(requestUrl, method, requestConfig);
}

export function executeFormRequest(
  url: string,
  method: string,
  form: any,
  requestConfig: any,
  additionalQueryParameters: any
): Promise<Response> {
  if (methodDoesNotSupportBody(method)) {
    const formAsUrlParams = new URLSearchParams(getFormData(form[0], additionalQueryParameters) as any);
    const targetUrl = url.indexOf("?") === -1 ? `${url}?${formAsUrlParams}` : `${url}&${formAsUrlParams}`;
    return executeFetchRequest(targetUrl, method, requestConfig);
  }

  const formConfiguration: any = $.extend(
    true,
    {},
    {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
    },
    requestConfig
  );

  if (form.attr("enctype") === "multipart/form-data") {
    formConfiguration.headers["Content-Type"] = "multipart/form-data";
    formConfiguration.body = getFormData(form[0], additionalQueryParameters);
    formConfiguration.processData = false;
    formConfiguration.contentType = false;
    formConfiguration.cache = "no-store";
  } else {
    formConfiguration.body = new URLSearchParams(getFormData(form[0], additionalQueryParameters) as any);
  }

  return executeFetchRequest(url, method, formConfiguration);
}

function getIdFromUpdateUrl(responseUrl: string) {
  const updateIdRegex = /\/([0-9]*)\/update/g;
  const match = updateIdRegex.exec(responseUrl) as any;
  if (match.length < 1) {
    return null;
  }
  return match[1];
}

function methodDoesNotSupportBody(method: string) {
  let lowerCased = method.toLowerCase();
  return lowerCased === "get" || lowerCased === "head";
}

export function executeFetchRequest(url: string, method: string, requestConfiguration?: any): Promise<Response> {
  const fetchConfiguration = $.extend(
    true,
    {},
    {
      method: method,
      headers: { "X-XSRF-Token": getCookie("XSRF-TOKEN") },
    },
    requestConfiguration
  );
  return fetch(url, fetchConfiguration);
}

export function getFormData(form: any, additionalQueryParameters: any): FormData {
  const formData = new FormData(form);

  if (typeof additionalQueryParameters !== "undefined") {
    Object.keys(additionalQueryParameters).forEach((queryParamName) => {
      const value = additionalQueryParameters[queryParamName];
      formData.set(queryParamName, value);
    });
  }

  return formData;
}
