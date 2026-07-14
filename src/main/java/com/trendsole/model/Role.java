package com.trendsole.model;

/**
 * Role Enum - Defines the roles a user can have in the system.
 *
 * What is an Enum?
 * - An Enum is a special Java type that represents a FIXED set of constants.
 * - A user can only have one of these predefined roles.
 *
 * Current roles:
 * - USER  → A regular customer who can browse and buy products.
 * - ADMIN → An administrator who can manage products, orders, etc. (for future use)
 */
public enum Role {
    USER,
    ADMIN
}
