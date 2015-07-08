package de.codecentric.boot.admin.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.StringUtils;

import com.netflix.appinfo.InstanceInfo;

import de.codecentric.boot.admin.model.Application;

public class DiscoveryClientFacade {

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired(required = false)
	private com.netflix.discovery.DiscoveryClient eurekaDiscoveryClient;

	@Value("${spring.boot.admin.discovery.management.context-path:}")
	private String managementContextPath;

	private String serviceContextPath = "";

	private String healthEndpoint = "health";

	public List<Application> discover() {
		if (eurekaDiscoveryClient != null) {
			return discoverViaEureka();
		}
		return discoverViaSpringCloud();
	}

	private List<Application> discoverViaEureka() {
		List<Application> applications = new ArrayList<>();
		for (com.netflix.discovery.shared.Application app : eurekaDiscoveryClient
				.getApplications().getRegisteredApplications()) {
			for (InstanceInfo instance : app.getInstancesAsIsFromEureka()) {
				applications.add(convert(instance));
			}
		}
		return Collections.unmodifiableList(applications);
	}

	private Application convert(InstanceInfo instance) {
		String serviceUrl = instance.getHomePageUrl();
		String managementUrl = instance.getMetadata().get("managementUrl");
		if(managementUrl == null) {
			managementUrl = append(serviceUrl, managementContextPath);
		}
		String healthCheckUrl = append(managementUrl, "health");
		return Application.create(instance.getAppName()).withHealthUrl(healthCheckUrl)
				.withManagementUrl(managementUrl).withServiceUrl(serviceUrl).build();
	}

	private List<Application> discoverViaSpringCloud() {
		List<Application> applications = new ArrayList<>();
		for (String serviceId : discoveryClient.getServices()) {
			for (ServiceInstance instance : discoveryClient.getInstances(serviceId)) {
				applications.add(convert(instance));
			}
		}
		return Collections.unmodifiableList(applications);
	}

	private Application convert(ServiceInstance instance) {
		String serviceUrl = append(instance.getUri().toString(), serviceContextPath);
		String managementUrl = append(instance.getUri().toString(), managementContextPath);
		String healthUrl = append(managementUrl, healthEndpoint);

		return Application.create(instance.getServiceId()).withHealthUrl(healthUrl)
				.withManagementUrl(managementUrl).withServiceUrl(serviceUrl).build();
	}

	public void setManagementContextPath(String managementContextPath) {
		this.managementContextPath = managementContextPath;
	}

	public void setServiceContextPath(String serviceContextPath) {
		this.serviceContextPath = serviceContextPath;
	}

	public void setHealthEndpoint(String healthEndpoint) {
		this.healthEndpoint = healthEndpoint;
	}

	private String append(String uri, String path) {
		String baseUri = uri.replaceFirst("/+$", "");
		if (StringUtils.isEmpty(path)) {
			return baseUri;
		}

		String normPath = path.replaceFirst("^/+", "").replaceFirst("/+$", "");
		return baseUri + "/" + normPath;
	}
}
