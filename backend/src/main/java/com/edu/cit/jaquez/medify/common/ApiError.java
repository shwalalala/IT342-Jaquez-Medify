package com.edu.cit.jaquez.medify.common;

public record ApiError(String code, String message, Object details) {
}
