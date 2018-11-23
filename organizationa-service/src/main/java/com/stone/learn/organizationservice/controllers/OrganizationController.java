package com.stone.learn.organizationservice.controllers;

import com.stone.learn.organizationservice.model.Organization;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/11/22
 **/
@RestController
@RequestMapping("/v1/organizations")
public class OrganizationController {

    @GetMapping("/{organizationId}")
    public Organization getOrganization(@PathVariable("organizationId") String organizationId) {
        Organization organization = new Organization();
        organization.setId(organizationId);
        return organization;
    }
}
