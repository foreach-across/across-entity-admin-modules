export function getCookie(name: string) {
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length === 2) {
    return parts.pop()!.split(";").shift();
  }
  return undefined;
}
