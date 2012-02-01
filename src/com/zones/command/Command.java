package com.zones.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zones.model.ZoneBase;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    
    String name();
    String usage() default "/<command>";
    String[] aliases();
    String description();
    
    int min() default 0;
    int max() default -1;
    
    boolean requiresSelected() default false;
    boolean requiresSelection() default false;
    boolean requiresPlayer() default true;
    Class<? extends ZoneBase> requiredType() default ZoneBase.class;
    String requiredPermission() default "";
    
}
