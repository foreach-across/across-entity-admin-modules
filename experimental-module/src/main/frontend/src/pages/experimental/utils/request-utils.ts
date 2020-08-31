import {getCookie} from "./utils";
import {JsonResponse, TextResponse} from "./response-types";

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
    requestConfig?: any;
}

export function executeRequest(partialConfiguration: RequestConfiguration): Promise<Response> {
    const {partial, form, method, url, copyOriginalRequestParameters, requestConfig} = partialConfiguration;

    const baseUrl = url ? url : window.location.href.split("?")[0];
    const formToSerialize = form ? $(form) : undefined;
    let requestUrl = baseUrl;
    if (partial) {
        requestUrl = baseUrl.indexOf("?") === -1 ? `${baseUrl}?_partial=${partial}` : `${baseUrl}&_partial=${partial}`;
    }

    if (typeof formToSerialize !== "undefined") {
        return executeFormRequest(requestUrl, method, formToSerialize, requestConfig, copyOriginalRequestParameters ?? true);
    }
    return executeFetchRequest(requestUrl, method, requestConfig);
}

export function executeFormRequest(url: string, method: string, form: any, requestConfig: any, copyOriginalRequestParameters: boolean): Promise<Response> {
    if (methodDoesNotSupportBody(method)) {
        const formAsUrlParams = new URLSearchParams(getFormData(form[0], copyOriginalRequestParameters) as any);
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
        formConfiguration.body = getFormData(form[0], copyOriginalRequestParameters);
        formConfiguration.processData = false;
        formConfiguration.contentType = false;
        formConfiguration.cache = false;
    } else {
        formConfiguration.body = new URLSearchParams(getFormData(form[0], copyOriginalRequestParameters) as any);
    }

    return executeFetchRequest(url, method, formConfiguration);
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
            headers: {"X-XSRF-Token": getCookie("XSRF-TOKEN"), "Content-Type": "application/x-www-form-urlencoded"},
        },
        requestConfiguration
    );
    return fetch(url, fetchConfiguration);
}

export function getFormData(form: any, copyOriginalRequestParameters: boolean): FormData {
    const queryParams = new URLSearchParams(window.location.search);
    const formData = new FormData(form);

    if (copyOriginalRequestParameters) {
        // @ts-ignore
        for (const entry of queryParams.entries()) {
            if (!formData.has(entry[0])) {
                formData.set(entry[0], entry[1]);
            }
        }
    }


    return formData;
}
