package net.retakethe.policyauction.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.retakethe.policyauction.data.api.types.UserRole;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestrictedPage {
	UserRole[] allowedRoles() default {UserRole.NONE};
}
