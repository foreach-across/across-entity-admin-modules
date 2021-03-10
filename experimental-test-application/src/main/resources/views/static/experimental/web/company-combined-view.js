$(document).ready(function () {
    console.log('Loading initial partial');
    const body = $(".js-lv2");
    body.addClass("partial-loading");
    body.append('<div class="partial-spinner"></div>');

    let url = "food?_partial=::body";

    fetch(url, {
        method: "GET",
        headers: {"X-XSRF-Token": getCookie("XSRF-TOKEN")},
    })
        .then((response) => {
            if (response.redirected) {
                window.location.href = response.url + "&redirectUrl=" + encodeURI(window.location.href);
                return Promise.reject(null);
            } else if (response.ok) {
                return response.text();
            } else {
                return Promise.reject(null);
            }
        })
        .then((data) => {
            body.removeClass("partial-loading");
            body.html(data);

            EntityModule.initializeFormElements($(".js-lv2"));
        });
});

function getCookie(name) {
    let matches = document.cookie.match(
        new RegExp("(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, "\\$1") + "=([^;]*)")
    );
    return matches ? decodeURIComponent(matches[1]) : undefined;
}