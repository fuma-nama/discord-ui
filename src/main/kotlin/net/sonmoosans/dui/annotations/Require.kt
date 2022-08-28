package net.sonmoosans.dui.annotations

/**
 * Mentioned States ID will be used in This component
 *
 * You might change default ID scope to avoid ID duplication
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class RequireStates(vararg val states: String)

/**
 * Mentioned Hooks ID will be used in This component
 *
 * You might change default ID scope to avoid ID duplication
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class RequireHooks(vararg val hooks: String)

/**
 * Mentioned Listeners ID will be used in This component
 *
 * You might change default ID scope to avoid ID duplication
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class RequireListener(vararg val listeners: String)