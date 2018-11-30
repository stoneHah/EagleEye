package com.stone.learn.licences.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import com.stone.learn.licences.clients.OrganizationDiscoveryClient;
import com.stone.learn.licences.clients.OrganizationFeignClient;
import com.stone.learn.licences.clients.OrganizationRestTemplateClient;
import com.stone.learn.licences.config.ServiceConfig;
import com.stone.learn.licences.model.License;
import com.stone.learn.licences.model.Organization;
import com.stone.learn.licences.repository.LicenseRepository;
import com.stone.learn.licences.support.context.UserContextHolder;
import com.stone.learn.licences.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class LicenseService {

    private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    ServiceConfig config;


//    @Autowired
//    OrganizationFeignClient organizationFeignClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestClient;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;


    private Organization retrieveOrgInfo(String organizationId, String clientType){
        Organization organization = null;

        switch (clientType) {
           /* case "feign":
                System.out.println("I am using the feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;*/
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
        }

        return organization;
    }

    public License getLicense(String organizationId, String licenseId, String clientType) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);

//        Organization org = retrieveOrgInfo(organizationId, clientType);
        Organization org = getOrganization(organizationId);

        return license
                .withOrganizationName( org.getName())
                .withContactName( org.getContactName())
                .withContactEmail( org.getContactEmail() )
                .withContactPhone( org.getContactPhone() )
                .withComment(config.getExampleProperty());
    }

    @HystrixCommand
    private Organization getOrganization(String organizationId) {
        return organizationRestClient.getOrganization(organizationId);
    }

    /**
     * 延长执行时间貌似没有执行
     * @param organizationId
     * @return
     */
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value="1500"),
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value="10"),
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value="75"),
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value="7000"),
            @HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_STATS_TIME_IN_MILLISECONDS, value="15000"),
            @HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_STATS_NUM_BUCKETS, value="3")
    },fallbackMethod = "buildFallbackLicenseList",
    threadPoolKey = "licenseByOrgThreadPool",
    threadPoolProperties = {
            @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE,value = "30"),
            @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE,value = "10")
    })
    public List<License> getLicensesByOrg(String organizationId) {
        logger.debug("LicenseService.getLicensesByOrg  Correlation id: {}", UserContextHolder.getContext().getCorrelationId());
        SleepUtils.randomSleep(2);

        return licenseRepository.findByOrganizationId(organizationId);
    }

    public List<License> buildFallbackLicenseList(String organizationId) {
        return Collections.singletonList(new License()
        .withId("000000-00-00000")
        .withOrganizationId(organizationId)
        .withProductName("Sorry no licensing information currently avalible"));
    }

    public void saveLicense(License license){
        license.withId( UUID.randomUUID().toString());

        licenseRepository.save(license);

    }

    public void updateLicense(License license){
      licenseRepository.save(license);
    }

    public void deleteLicense(License license){
        licenseRepository.deleteById( license.getLicenseId());
    }

}
