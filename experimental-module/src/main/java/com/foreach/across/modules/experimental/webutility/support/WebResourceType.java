package com.foreach.across.modules.experimental.webutility.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class WebResourceType {

    @Getter
    private String extension;
    @Getter
    private String prefix;


    public static WebResourceType css(String prefix) {
        return new WebResourceType(".css", prefix);
    }

    public static WebResourceType js(String prefix) {
        return new WebResourceType(".js", prefix);
    }

    public boolean isCss() {
        return this.extension.equals(".css");
    }
}
