package com.foreach.across.modules.experimental.webutility.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@DependsOn( "messageSource" )
public class MessageCodeHelper {

    private static MessageSource messageSource;

    public static String getMessage( String code ) {
        return getMessage( code, null );
    }

    public static String getMessage( String code, Object... vars ) {
        return messageSource.getMessage( code, vars, Locale.getDefault() );
    }

    @Autowired
    public void setMessageSource( MessageSource messageSource ) {
        MessageCodeHelper.messageSource = messageSource;
    }
}
