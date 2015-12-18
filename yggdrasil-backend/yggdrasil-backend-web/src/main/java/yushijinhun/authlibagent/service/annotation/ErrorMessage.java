package yushijinhun.authlibagent.service.annotation;

public @interface ErrorMessage {

	String error() default "";

	String cause() default "";

	String message() default "";

}
