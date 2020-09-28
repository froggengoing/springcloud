
DiscoveryClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config, AbstractDiscoveryClientOptionalArgs args,
				Provider<BackupRegistry> backupRegistryProvider, EndpointRandomizer endpointRandomizer) {
	//省略args入参对healthCheckHandlerProvider、healthCheckCallbackProvider等的赋值
	//因为args的实现是MutableDiscoveryClientOptionalArgs，其属性值都是null

	//com.netflix.appinfo.ApplicationInfoManager封装了配置信息
	this.applicationInfoManager = applicationInfoManager;
	InstanceInfo myInfo = applicationInfoManager.getInfo();

	clientConfig = config;
	staticClientConfig = clientConfig;
	transportConfig = config.getTransportConfig();
	instanceInfo = myInfo;
	if (myInfo != null) {
		appPathIdentifier = instanceInfo.getAppName() + "/" + instanceInfo.getId();
	} else {
		logger.warn("Setting instanceInfo to a passed in null value");
	}

	this.backupRegistryProvider = backupRegistryProvider;
	this.endpointRandomizer = endpointRandomizer;
	this.urlRandomizer = new EndpointUtils.InstanceInfoBasedUrlRandomizer(instanceInfo);
	localRegionApps.set(new Applications());

	fetchRegistryGeneration = new AtomicLong(0);

	remoteRegionsToFetch = new AtomicReference<String>(clientConfig.fetchRegistryForRemoteRegions());
	remoteRegionsRef = new AtomicReference<>(remoteRegionsToFetch.get() == null ? null : remoteRegionsToFetch.get().split(","));
	////从eureka server拉起注册表信息  eureka.client.fetch-register
	if (config.shouldFetchRegistry()) {
		this.registryStalenessMonitor = new ThresholdLevelsMetric(this, METRIC_REGISTRY_PREFIX + "lastUpdateSec_", new long[]{15L, 30L, 60L, 120L, 240L, 480L});
	} else {
		this.registryStalenessMonitor = ThresholdLevelsMetric.NO_OP_METRIC;
	}
	//当前的客户端是否应该注册到erueka中：eureka.client.register-with-eureka  
	if (config.shouldRegisterWithEureka()) {
		this.heartbeatStalenessMonitor = new ThresholdLevelsMetric(this, METRIC_REGISTRATION_PREFIX + "lastHeartbeatSec_", new long[]{15L, 30L, 60L, 120L, 240L, 480L});
	} else {
		this.heartbeatStalenessMonitor = ThresholdLevelsMetric.NO_OP_METRIC;
	}

	logger.info("Initializing Eureka in region {}", clientConfig.getRegion());

	if (!config.shouldRegisterWithEureka() && !config.shouldFetchRegistry()) {
		//如果既不注册也不拉取，直接返回。省略了部分代码
		return;  // no need to setup up an network tasks and we are done
	}

	try {
		 //线程池大小为2，一个用户发送心跳，另外1个缓存刷新
		// default size of 2 - 1 each for heartbeat and cacheRefresh
		scheduler = Executors.newScheduledThreadPool(2,
				new ThreadFactoryBuilder()
						.setNameFormat("DiscoveryClient-%d")
						.setDaemon(true)
						.build());

		heartbeatExecutor = new ThreadPoolExecutor(
				1, clientConfig.getHeartbeatExecutorThreadPoolSize(), 0, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>(),
				new ThreadFactoryBuilder()
						.setNameFormat("DiscoveryClient-HeartbeatExecutor-%d")
						.setDaemon(true)
						.build()
		);  // use direct handoff

		cacheRefreshExecutor = new ThreadPoolExecutor(
				1, clientConfig.getCacheRefreshExecutorThreadPoolSize(), 0, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>(),
				new ThreadFactoryBuilder()
						.setNameFormat("DiscoveryClient-CacheRefreshExecutor-%d")
						.setDaemon(true)
						.build()
		);  // use direct handoff
		//初始化client与server交互的jersey客户端
		eurekaTransport = new EurekaTransport();
		scheduleServerEndpointTask(eurekaTransport, args);

		AzToRegionMapper azToRegionMapper;
		if (clientConfig.shouldUseDnsForFetchingServiceUrls()) {
			azToRegionMapper = new DNSBasedAzToRegionMapper(clientConfig);
		} else {
			azToRegionMapper = new PropertyBasedAzToRegionMapper(clientConfig);
		}
		if (null != remoteRegionsToFetch.get()) {
			azToRegionMapper.setRegionsToFetch(remoteRegionsToFetch.get().split(","));
		}
		instanceRegionChecker = new InstanceRegionChecker(azToRegionMapper, clientConfig.getRegion());
	} catch (Throwable e) {
		throw new RuntimeException("Failed to initialize DiscoveryClient!", e);
	}
	 //拉取注册表的信息，fetchRegistry(false)
	if (clientConfig.shouldFetchRegistry() && !fetchRegistry(false)) {
		fetchRegistryFromBackup();
	}
	 //应该是扩展点，在服务实例进行注册前的预处理
	// call and execute the pre registration handler before all background tasks (inc registration) is started
	if (this.preRegistrationHandler != null) {
		this.preRegistrationHandler.beforeRegistration();
	}

	if (clientConfig.shouldRegisterWithEureka() && clientConfig.shouldEnforceRegistrationAtInit()) {
		try {
			//执行注册
			//Register with the eureka service by making the appropriate REST call.
			if (!register() ) {
				throw new IllegalStateException("Registration error at startup. Invalid server response.");
			}
		} 
	}
	//初始心跳定时任务，缓存刷新
	// finally, init the schedule tasks (e.g. cluster resolvers, heartbeat, instanceInfo replicator, fetch
	initScheduledTasks();

	try {
		Monitors.registerObject(this);
	} catch (Throwable e) {
		logger.warn("Cannot register timers", e);
	}

	// This is a bit of hack to allow for existing code using DiscoveryManager.getInstance()
	// to work with DI'd DiscoveryClient
	DiscoveryManager.getInstance().setDiscoveryClient(this);
	DiscoveryManager.getInstance().setEurekaClientConfig(config);

	initTimestampMs = System.currentTimeMillis();
}
	
//com.netflix.discovery.shared.transport.decorator.RetryableEurekaHttpClient#execute
protected <R> EurekaHttpResponse<R> execute(RequestExecutor<R> requestExecutor) {
	List<EurekaEndpoint> candidateHosts = null;
	int endpointIdx = 0;
	//numberOfRetries初始化默认为3
	for (int retry = 0; retry < numberOfRetries; retry++) {
		EurekaHttpClient currentHttpClient = delegate.get();
		EurekaEndpoint currentEndpoint = null;
		if (currentHttpClient == null) {
			if (candidateHosts == null) {
				//获取可用的url
				candidateHosts = getHostCandidates();
				if (candidateHosts.isEmpty()) {
					throw new TransportException("There is no known eureka server; cluster server list is empty");
				}
			}
			if (endpointIdx >= candidateHosts.size()) {
				throw new TransportException("Cannot execute request on any known server");
			}

			currentEndpoint = candidateHosts.get(endpointIdx++);
			//创建new RedirectingEurekaHttpClient(endpoint.getServiceUrl(), delegateFactory, dnsService);
			currentHttpClient = clientFactory.newClient(currentEndpoint);
		}

		try {
			//发送请求
			//requestExecutor实际是EurekaHttpClientDecorator#getApplications
			EurekaHttpResponse<R> response = requestExecutor.execute(currentHttpClient);
			if (serverStatusEvaluator.accept(response.getStatusCode(), requestExecutor.getRequestType())) {
				delegate.set(currentHttpClient);
				if (retry > 0) {
					logger.info("Request execution succeeded on retry #{}", retry);
				}
				return response;
			}
			logger.warn("Request execution failure with status code {}; retrying on another server if available", response.getStatusCode());
		} catch (Exception e) {
			logger.warn("Request execution failed with message: {}", e.getMessage());  // just log message as the underlying client should log the stacktrace
		}

		// Connection error or 5xx from the server that must be retried on another server
		delegate.compareAndSet(currentHttpClient, null);
		if (currentEndpoint != null) {
			quarantineSet.add(currentEndpoint);
		}
	}
	throw new TransportException("Retry limit reached; giving up on completing the request");
}
//com.netflix.discovery.shared.transport.decorator.RedirectingEurekaHttpClient#execute
protected <R> EurekaHttpResponse<R> execute(RequestExecutor<R> requestExecutor) {
	EurekaHttpClient currentEurekaClient = delegateRef.get();
	if (currentEurekaClient == null) {
		//serviceEndpoint封装了serverUrl比如 http://eureka7002.com:7002/eureka/
		//实际类型为：com.netflix.discovery.shared.transport.decorator.MetricsCollectingEurekaHttpClient
		AtomicReference<EurekaHttpClient> currentEurekaClientRef = new AtomicReference<>(factory.newClient(serviceEndpoint));
		try {
			EurekaHttpResponse<R> response = executeOnNewServer(requestExecutor, currentEurekaClientRef);
			TransportUtils.shutdown(delegateRef.getAndSet(currentEurekaClientRef.get()));
			return response;
		} 
	} else {
		try {
			return requestExecutor.execute(currentEurekaClient);
		}
	}
}
//RedirectingEurekaHttpClient#executeOnNewServer
private <R> EurekaHttpResponse<R> executeOnNewServer(RequestExecutor<R> requestExecutor,
													 AtomicReference<EurekaHttpClient> currentHttpClientRef) {
	URI targetUrl = null;
	for (int followRedirectCount = 0; followRedirectCount < MAX_FOLLOWED_REDIRECTS; followRedirectCount++) {
	//使用上文生成的MetricsCollectingEurekaHttpClient执行
		EurekaHttpResponse<R> httpResponse = requestExecutor.execute(currentHttpClientRef.get());
		if (httpResponse.getStatusCode() != 302) {
			if (followRedirectCount == 0) {
				logger.debug("Pinning to endpoint {}", targetUrl);
			} else {
				logger.info("Pinning to endpoint {}, after {} redirect(s)", targetUrl, followRedirectCount);
			}
			return httpResponse;
		}

		targetUrl = getRedirectBaseUri(httpResponse.getLocation());
		if (targetUrl == null) {
			throw new TransportException("Invalid redirect URL " + httpResponse.getLocation());
		}

		currentHttpClientRef.getAndSet(null).shutdown();
		currentHttpClientRef.set(factory.newClient(new DefaultEndpoint(targetUrl.toString())));
	}
}

//com.netflix.discovery.shared.transport.decorator.MetricsCollectingEurekaHttpClient#execute
//MetricsCollectingEurekaHttpClient的请求类型
//Register
//Cancel
//SendHeartBeat
//StatusUpdate
//DeleteStatusOverride
//GetApplication
//GetDelta
//GetVip
//GetSecureVip
//GetApplication
//GetInstance
//GetApplicationInstance
protected <R> EurekaHttpResponse<R> execute(RequestExecutor<R> requestExecutor) {
	//这里的请求类型时GetApplications
	EurekaHttpClientRequestMetrics requestMetrics = metricsByRequestType.get(requestExecutor.getRequestType());
	Stopwatch stopwatch = requestMetrics.latencyTimer.start();
	try {
		//这里的delegate是com.netflix.discovery.shared.transport.jersey.JerseyApplicationClient
		EurekaHttpResponse<R> httpResponse = requestExecutor.execute(delegate);
		requestMetrics.countersByStatus.get(mappedStatus(httpResponse)).increment();
		return httpResponse;
	} catch (Exception e) {
		requestMetrics.connectionErrors.increment();
		exceptionsMetric.count(e);
		throw e;
	} finally {
		stopwatch.stop();
	}
}
//com.netflix.discovery.shared.transport.jersey.JerseyApplicationClient
public EurekaHttpResponse<Applications> getApplications(String... regions) {
	return getApplicationsInternal("apps/", regions);
}
//com.netflix.discovery.shared.transport.jersey.AbstractJerseyEurekaHttpClient#getApplicationsInternal
private EurekaHttpResponse<Applications> getApplicationsInternal(String urlPath, String[] regions) {
	ClientResponse response = null;
	String regionsParamValue = null;
	try {
		WebResource webResource = jerseyClient.resource(serviceUrl).path(urlPath);
		if (regions != null && regions.length > 0) {
			regionsParamValue = StringUtil.join(regions);
			webResource = webResource.queryParam("regions", regionsParamValue);
		}
		Builder requestBuilder = webResource.getRequestBuilder();
		addExtraHeaders(requestBuilder);
		response = requestBuilder.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

		Applications applications = null;
		if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity()) {
			applications = response.getEntity(Applications.class);
		}
		return anEurekaHttpResponse(response.getStatus(), Applications.class)
				.headers(headersOf(response))
				.entity(applications)
				.build();
	} finally {
		if (logger.isDebugEnabled()) {
			logger.debug("Jersey HTTP GET {}/{}?{}; statusCode={}",
					serviceUrl, urlPath,
					regionsParamValue == null ? "" : "regions=" + regionsParamValue,
					response == null ? "N/A" : response.getStatus()
			);
		}
		if (response != null) {
			response.close();
		}
	}
}



//################################################server
//com.netflix.eureka.resources.InstanceResource
public Response renewLease(
		@HeaderParam(PeerEurekaNode.HEADER_REPLICATION) String isReplication,
		@QueryParam("overriddenstatus") String overriddenStatus,
		@QueryParam("status") String status,
		@QueryParam("lastDirtyTimestamp") String lastDirtyTimestamp) {
	boolean isFromReplicaNode = "true".equals(isReplication);
	//调用续约逻辑
	//org.springframework.cloud.netflix.eureka.server.InstanceRegistry#renew
	boolean isSuccess = registry.renew(app.getName(), id, isFromReplicaNode);

	// Not found in the registry, immediately ask for a register
	if (!isSuccess) {
		logger.warn("Not Found (Renew): {} - {}", app.getName(), id);
		return Response.status(Status.NOT_FOUND).build();
	}
	// Check if we need to sync based on dirty time stamp, the client
	// instance might have changed some value
	Response response;
	if (lastDirtyTimestamp != null && serverConfig.shouldSyncWhenTimestampDiffers()) {
		response = this.validateDirtyTimestamp(Long.valueOf(lastDirtyTimestamp), isFromReplicaNode);
		// Store the overridden status since the validation found out the node that replicates wins
		if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
				&& (overriddenStatus != null)
				&& !(InstanceStatus.UNKNOWN.name().equals(overriddenStatus))
				&& isFromReplicaNode) {
			registry.storeOverriddenStatusIfRequired(app.getAppName(), id, InstanceStatus.valueOf(overriddenStatus));
		}
	} else {
		response = Response.ok().build();
	}
	logger.debug("Found (Renew): {} - {}; reply status={}", app.getName(), id, response.getStatus());
	return response;
}
