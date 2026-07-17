package com.bnroll.billing.security;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ServicePrincipal {

    private final String serviceName;

}