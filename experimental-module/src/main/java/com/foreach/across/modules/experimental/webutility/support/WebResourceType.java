package com.foreach.across.modules.experimental.webutility.support;

import lombok.Getter;

public enum WebResourceType {
    CSS( ".css", "@static:/bic/web/" ),
    JS( ".js", "@static:/bic/web/" ),
    EXTERNAL_CSS( "", "" ),
    EXTERNAL_JS( "", "" );

    @Getter
    private String extension;
    @Getter
    private String prefix;

    WebResourceType( String extension, String prefix ) {
        this.extension = extension;
        this.prefix = prefix;
    }
}
