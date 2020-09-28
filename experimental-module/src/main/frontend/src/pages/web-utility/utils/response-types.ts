export interface JsonResponse extends Response {
  jsonContent: string;
}

export interface TextResponse extends Response {
  textContent: string;
}
