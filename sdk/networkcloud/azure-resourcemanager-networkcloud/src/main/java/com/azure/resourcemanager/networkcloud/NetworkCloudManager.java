// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.networkcloud;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;
import com.azure.core.http.HttpPipelinePosition;
import com.azure.core.http.policy.AddDatePolicy;
import com.azure.core.http.policy.AddHeadersFromContextPolicy;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.HttpLoggingPolicy;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.http.policy.HttpPolicyProviders;
import com.azure.core.http.policy.RequestIdPolicy;
import com.azure.core.http.policy.RetryOptions;
import com.azure.core.http.policy.RetryPolicy;
import com.azure.core.http.policy.UserAgentPolicy;
import com.azure.core.management.http.policy.ArmChallengeAuthenticationPolicy;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.Configuration;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.networkcloud.fluent.NetworkCloud;
import com.azure.resourcemanager.networkcloud.implementation.BareMetalMachineKeySetsImpl;
import com.azure.resourcemanager.networkcloud.implementation.BareMetalMachinesImpl;
import com.azure.resourcemanager.networkcloud.implementation.BmcKeySetsImpl;
import com.azure.resourcemanager.networkcloud.implementation.CloudServicesNetworksImpl;
import com.azure.resourcemanager.networkcloud.implementation.ClusterManagersImpl;
import com.azure.resourcemanager.networkcloud.implementation.ClustersImpl;
import com.azure.resourcemanager.networkcloud.implementation.ConsolesImpl;
import com.azure.resourcemanager.networkcloud.implementation.DefaultCniNetworksImpl;
import com.azure.resourcemanager.networkcloud.implementation.HybridAksClustersImpl;
import com.azure.resourcemanager.networkcloud.implementation.L2NetworksImpl;
import com.azure.resourcemanager.networkcloud.implementation.L3NetworksImpl;
import com.azure.resourcemanager.networkcloud.implementation.MetricsConfigurationsImpl;
import com.azure.resourcemanager.networkcloud.implementation.NetworkCloudBuilder;
import com.azure.resourcemanager.networkcloud.implementation.OperationsImpl;
import com.azure.resourcemanager.networkcloud.implementation.RackSkusImpl;
import com.azure.resourcemanager.networkcloud.implementation.RacksImpl;
import com.azure.resourcemanager.networkcloud.implementation.StorageAppliancesImpl;
import com.azure.resourcemanager.networkcloud.implementation.TrunkedNetworksImpl;
import com.azure.resourcemanager.networkcloud.implementation.VirtualMachinesImpl;
import com.azure.resourcemanager.networkcloud.implementation.VolumesImpl;
import com.azure.resourcemanager.networkcloud.models.BareMetalMachineKeySets;
import com.azure.resourcemanager.networkcloud.models.BareMetalMachines;
import com.azure.resourcemanager.networkcloud.models.BmcKeySets;
import com.azure.resourcemanager.networkcloud.models.CloudServicesNetworks;
import com.azure.resourcemanager.networkcloud.models.ClusterManagers;
import com.azure.resourcemanager.networkcloud.models.Clusters;
import com.azure.resourcemanager.networkcloud.models.Consoles;
import com.azure.resourcemanager.networkcloud.models.DefaultCniNetworks;
import com.azure.resourcemanager.networkcloud.models.HybridAksClusters;
import com.azure.resourcemanager.networkcloud.models.L2Networks;
import com.azure.resourcemanager.networkcloud.models.L3Networks;
import com.azure.resourcemanager.networkcloud.models.MetricsConfigurations;
import com.azure.resourcemanager.networkcloud.models.Operations;
import com.azure.resourcemanager.networkcloud.models.RackSkus;
import com.azure.resourcemanager.networkcloud.models.Racks;
import com.azure.resourcemanager.networkcloud.models.StorageAppliances;
import com.azure.resourcemanager.networkcloud.models.TrunkedNetworks;
import com.azure.resourcemanager.networkcloud.models.VirtualMachines;
import com.azure.resourcemanager.networkcloud.models.Volumes;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Entry point to NetworkCloudManager. The Network Cloud APIs provide management of the on-premises clusters and their
 * resources, such as, racks, bare metal hosts, virtual machines, workload networks and more.
 */
public final class NetworkCloudManager {
    private Operations operations;

    private BareMetalMachines bareMetalMachines;

    private CloudServicesNetworks cloudServicesNetworks;

    private ClusterManagers clusterManagers;

    private Clusters clusters;

    private DefaultCniNetworks defaultCniNetworks;

    private HybridAksClusters hybridAksClusters;

    private L2Networks l2Networks;

    private L3Networks l3Networks;

    private RackSkus rackSkus;

    private Racks racks;

    private StorageAppliances storageAppliances;

    private TrunkedNetworks trunkedNetworks;

    private VirtualMachines virtualMachines;

    private Volumes volumes;

    private BareMetalMachineKeySets bareMetalMachineKeySets;

    private BmcKeySets bmcKeySets;

    private MetricsConfigurations metricsConfigurations;

    private Consoles consoles;

    private final NetworkCloud clientObject;

    private NetworkCloudManager(HttpPipeline httpPipeline, AzureProfile profile, Duration defaultPollInterval) {
        Objects.requireNonNull(httpPipeline, "'httpPipeline' cannot be null.");
        Objects.requireNonNull(profile, "'profile' cannot be null.");
        this.clientObject =
            new NetworkCloudBuilder()
                .pipeline(httpPipeline)
                .endpoint(profile.getEnvironment().getResourceManagerEndpoint())
                .subscriptionId(profile.getSubscriptionId())
                .defaultPollInterval(defaultPollInterval)
                .buildClient();
    }

    /**
     * Creates an instance of NetworkCloud service API entry point.
     *
     * @param credential the credential to use.
     * @param profile the Azure profile for client.
     * @return the NetworkCloud service API instance.
     */
    public static NetworkCloudManager authenticate(TokenCredential credential, AzureProfile profile) {
        Objects.requireNonNull(credential, "'credential' cannot be null.");
        Objects.requireNonNull(profile, "'profile' cannot be null.");
        return configure().authenticate(credential, profile);
    }

    /**
     * Creates an instance of NetworkCloud service API entry point.
     *
     * @param httpPipeline the {@link HttpPipeline} configured with Azure authentication credential.
     * @param profile the Azure profile for client.
     * @return the NetworkCloud service API instance.
     */
    public static NetworkCloudManager authenticate(HttpPipeline httpPipeline, AzureProfile profile) {
        Objects.requireNonNull(httpPipeline, "'httpPipeline' cannot be null.");
        Objects.requireNonNull(profile, "'profile' cannot be null.");
        return new NetworkCloudManager(httpPipeline, profile, null);
    }

    /**
     * Gets a Configurable instance that can be used to create NetworkCloudManager with optional configuration.
     *
     * @return the Configurable instance allowing configurations.
     */
    public static Configurable configure() {
        return new NetworkCloudManager.Configurable();
    }

    /** The Configurable allowing configurations to be set. */
    public static final class Configurable {
        private static final ClientLogger LOGGER = new ClientLogger(Configurable.class);

        private HttpClient httpClient;
        private HttpLogOptions httpLogOptions;
        private final List<HttpPipelinePolicy> policies = new ArrayList<>();
        private final List<String> scopes = new ArrayList<>();
        private RetryPolicy retryPolicy;
        private RetryOptions retryOptions;
        private Duration defaultPollInterval;

        private Configurable() {
        }

        /**
         * Sets the http client.
         *
         * @param httpClient the HTTP client.
         * @return the configurable object itself.
         */
        public Configurable withHttpClient(HttpClient httpClient) {
            this.httpClient = Objects.requireNonNull(httpClient, "'httpClient' cannot be null.");
            return this;
        }

        /**
         * Sets the logging options to the HTTP pipeline.
         *
         * @param httpLogOptions the HTTP log options.
         * @return the configurable object itself.
         */
        public Configurable withLogOptions(HttpLogOptions httpLogOptions) {
            this.httpLogOptions = Objects.requireNonNull(httpLogOptions, "'httpLogOptions' cannot be null.");
            return this;
        }

        /**
         * Adds the pipeline policy to the HTTP pipeline.
         *
         * @param policy the HTTP pipeline policy.
         * @return the configurable object itself.
         */
        public Configurable withPolicy(HttpPipelinePolicy policy) {
            this.policies.add(Objects.requireNonNull(policy, "'policy' cannot be null."));
            return this;
        }

        /**
         * Adds the scope to permission sets.
         *
         * @param scope the scope.
         * @return the configurable object itself.
         */
        public Configurable withScope(String scope) {
            this.scopes.add(Objects.requireNonNull(scope, "'scope' cannot be null."));
            return this;
        }

        /**
         * Sets the retry policy to the HTTP pipeline.
         *
         * @param retryPolicy the HTTP pipeline retry policy.
         * @return the configurable object itself.
         */
        public Configurable withRetryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = Objects.requireNonNull(retryPolicy, "'retryPolicy' cannot be null.");
            return this;
        }

        /**
         * Sets the retry options for the HTTP pipeline retry policy.
         *
         * <p>This setting has no effect, if retry policy is set via {@link #withRetryPolicy(RetryPolicy)}.
         *
         * @param retryOptions the retry options for the HTTP pipeline retry policy.
         * @return the configurable object itself.
         */
        public Configurable withRetryOptions(RetryOptions retryOptions) {
            this.retryOptions = Objects.requireNonNull(retryOptions, "'retryOptions' cannot be null.");
            return this;
        }

        /**
         * Sets the default poll interval, used when service does not provide "Retry-After" header.
         *
         * @param defaultPollInterval the default poll interval.
         * @return the configurable object itself.
         */
        public Configurable withDefaultPollInterval(Duration defaultPollInterval) {
            this.defaultPollInterval =
                Objects.requireNonNull(defaultPollInterval, "'defaultPollInterval' cannot be null.");
            if (this.defaultPollInterval.isNegative()) {
                throw LOGGER
                    .logExceptionAsError(new IllegalArgumentException("'defaultPollInterval' cannot be negative"));
            }
            return this;
        }

        /**
         * Creates an instance of NetworkCloud service API entry point.
         *
         * @param credential the credential to use.
         * @param profile the Azure profile for client.
         * @return the NetworkCloud service API instance.
         */
        public NetworkCloudManager authenticate(TokenCredential credential, AzureProfile profile) {
            Objects.requireNonNull(credential, "'credential' cannot be null.");
            Objects.requireNonNull(profile, "'profile' cannot be null.");

            StringBuilder userAgentBuilder = new StringBuilder();
            userAgentBuilder
                .append("azsdk-java")
                .append("-")
                .append("com.azure.resourcemanager.networkcloud")
                .append("/")
                .append("1.0.0-beta.1");
            if (!Configuration.getGlobalConfiguration().get("AZURE_TELEMETRY_DISABLED", false)) {
                userAgentBuilder
                    .append(" (")
                    .append(Configuration.getGlobalConfiguration().get("java.version"))
                    .append("; ")
                    .append(Configuration.getGlobalConfiguration().get("os.name"))
                    .append("; ")
                    .append(Configuration.getGlobalConfiguration().get("os.version"))
                    .append("; auto-generated)");
            } else {
                userAgentBuilder.append(" (auto-generated)");
            }

            if (scopes.isEmpty()) {
                scopes.add(profile.getEnvironment().getManagementEndpoint() + "/.default");
            }
            if (retryPolicy == null) {
                if (retryOptions != null) {
                    retryPolicy = new RetryPolicy(retryOptions);
                } else {
                    retryPolicy = new RetryPolicy("Retry-After", ChronoUnit.SECONDS);
                }
            }
            List<HttpPipelinePolicy> policies = new ArrayList<>();
            policies.add(new UserAgentPolicy(userAgentBuilder.toString()));
            policies.add(new AddHeadersFromContextPolicy());
            policies.add(new RequestIdPolicy());
            policies
                .addAll(
                    this
                        .policies
                        .stream()
                        .filter(p -> p.getPipelinePosition() == HttpPipelinePosition.PER_CALL)
                        .collect(Collectors.toList()));
            HttpPolicyProviders.addBeforeRetryPolicies(policies);
            policies.add(retryPolicy);
            policies.add(new AddDatePolicy());
            policies.add(new ArmChallengeAuthenticationPolicy(credential, scopes.toArray(new String[0])));
            policies
                .addAll(
                    this
                        .policies
                        .stream()
                        .filter(p -> p.getPipelinePosition() == HttpPipelinePosition.PER_RETRY)
                        .collect(Collectors.toList()));
            HttpPolicyProviders.addAfterRetryPolicies(policies);
            policies.add(new HttpLoggingPolicy(httpLogOptions));
            HttpPipeline httpPipeline =
                new HttpPipelineBuilder()
                    .httpClient(httpClient)
                    .policies(policies.toArray(new HttpPipelinePolicy[0]))
                    .build();
            return new NetworkCloudManager(httpPipeline, profile, defaultPollInterval);
        }
    }

    /**
     * Gets the resource collection API of Operations.
     *
     * @return Resource collection API of Operations.
     */
    public Operations operations() {
        if (this.operations == null) {
            this.operations = new OperationsImpl(clientObject.getOperations(), this);
        }
        return operations;
    }

    /**
     * Gets the resource collection API of BareMetalMachines. It manages BareMetalMachine.
     *
     * @return Resource collection API of BareMetalMachines.
     */
    public BareMetalMachines bareMetalMachines() {
        if (this.bareMetalMachines == null) {
            this.bareMetalMachines = new BareMetalMachinesImpl(clientObject.getBareMetalMachines(), this);
        }
        return bareMetalMachines;
    }

    /**
     * Gets the resource collection API of CloudServicesNetworks. It manages CloudServicesNetwork.
     *
     * @return Resource collection API of CloudServicesNetworks.
     */
    public CloudServicesNetworks cloudServicesNetworks() {
        if (this.cloudServicesNetworks == null) {
            this.cloudServicesNetworks = new CloudServicesNetworksImpl(clientObject.getCloudServicesNetworks(), this);
        }
        return cloudServicesNetworks;
    }

    /**
     * Gets the resource collection API of ClusterManagers. It manages ClusterManager.
     *
     * @return Resource collection API of ClusterManagers.
     */
    public ClusterManagers clusterManagers() {
        if (this.clusterManagers == null) {
            this.clusterManagers = new ClusterManagersImpl(clientObject.getClusterManagers(), this);
        }
        return clusterManagers;
    }

    /**
     * Gets the resource collection API of Clusters. It manages Cluster.
     *
     * @return Resource collection API of Clusters.
     */
    public Clusters clusters() {
        if (this.clusters == null) {
            this.clusters = new ClustersImpl(clientObject.getClusters(), this);
        }
        return clusters;
    }

    /**
     * Gets the resource collection API of DefaultCniNetworks. It manages DefaultCniNetwork.
     *
     * @return Resource collection API of DefaultCniNetworks.
     */
    public DefaultCniNetworks defaultCniNetworks() {
        if (this.defaultCniNetworks == null) {
            this.defaultCniNetworks = new DefaultCniNetworksImpl(clientObject.getDefaultCniNetworks(), this);
        }
        return defaultCniNetworks;
    }

    /**
     * Gets the resource collection API of HybridAksClusters. It manages HybridAksCluster.
     *
     * @return Resource collection API of HybridAksClusters.
     */
    public HybridAksClusters hybridAksClusters() {
        if (this.hybridAksClusters == null) {
            this.hybridAksClusters = new HybridAksClustersImpl(clientObject.getHybridAksClusters(), this);
        }
        return hybridAksClusters;
    }

    /**
     * Gets the resource collection API of L2Networks. It manages L2Network.
     *
     * @return Resource collection API of L2Networks.
     */
    public L2Networks l2Networks() {
        if (this.l2Networks == null) {
            this.l2Networks = new L2NetworksImpl(clientObject.getL2Networks(), this);
        }
        return l2Networks;
    }

    /**
     * Gets the resource collection API of L3Networks. It manages L3Network.
     *
     * @return Resource collection API of L3Networks.
     */
    public L3Networks l3Networks() {
        if (this.l3Networks == null) {
            this.l3Networks = new L3NetworksImpl(clientObject.getL3Networks(), this);
        }
        return l3Networks;
    }

    /**
     * Gets the resource collection API of RackSkus.
     *
     * @return Resource collection API of RackSkus.
     */
    public RackSkus rackSkus() {
        if (this.rackSkus == null) {
            this.rackSkus = new RackSkusImpl(clientObject.getRackSkus(), this);
        }
        return rackSkus;
    }

    /**
     * Gets the resource collection API of Racks. It manages Rack.
     *
     * @return Resource collection API of Racks.
     */
    public Racks racks() {
        if (this.racks == null) {
            this.racks = new RacksImpl(clientObject.getRacks(), this);
        }
        return racks;
    }

    /**
     * Gets the resource collection API of StorageAppliances. It manages StorageAppliance.
     *
     * @return Resource collection API of StorageAppliances.
     */
    public StorageAppliances storageAppliances() {
        if (this.storageAppliances == null) {
            this.storageAppliances = new StorageAppliancesImpl(clientObject.getStorageAppliances(), this);
        }
        return storageAppliances;
    }

    /**
     * Gets the resource collection API of TrunkedNetworks. It manages TrunkedNetwork.
     *
     * @return Resource collection API of TrunkedNetworks.
     */
    public TrunkedNetworks trunkedNetworks() {
        if (this.trunkedNetworks == null) {
            this.trunkedNetworks = new TrunkedNetworksImpl(clientObject.getTrunkedNetworks(), this);
        }
        return trunkedNetworks;
    }

    /**
     * Gets the resource collection API of VirtualMachines. It manages VirtualMachine.
     *
     * @return Resource collection API of VirtualMachines.
     */
    public VirtualMachines virtualMachines() {
        if (this.virtualMachines == null) {
            this.virtualMachines = new VirtualMachinesImpl(clientObject.getVirtualMachines(), this);
        }
        return virtualMachines;
    }

    /**
     * Gets the resource collection API of Volumes. It manages Volume.
     *
     * @return Resource collection API of Volumes.
     */
    public Volumes volumes() {
        if (this.volumes == null) {
            this.volumes = new VolumesImpl(clientObject.getVolumes(), this);
        }
        return volumes;
    }

    /**
     * Gets the resource collection API of BareMetalMachineKeySets. It manages BareMetalMachineKeySet.
     *
     * @return Resource collection API of BareMetalMachineKeySets.
     */
    public BareMetalMachineKeySets bareMetalMachineKeySets() {
        if (this.bareMetalMachineKeySets == null) {
            this.bareMetalMachineKeySets =
                new BareMetalMachineKeySetsImpl(clientObject.getBareMetalMachineKeySets(), this);
        }
        return bareMetalMachineKeySets;
    }

    /**
     * Gets the resource collection API of BmcKeySets. It manages BmcKeySet.
     *
     * @return Resource collection API of BmcKeySets.
     */
    public BmcKeySets bmcKeySets() {
        if (this.bmcKeySets == null) {
            this.bmcKeySets = new BmcKeySetsImpl(clientObject.getBmcKeySets(), this);
        }
        return bmcKeySets;
    }

    /**
     * Gets the resource collection API of MetricsConfigurations. It manages ClusterMetricsConfiguration.
     *
     * @return Resource collection API of MetricsConfigurations.
     */
    public MetricsConfigurations metricsConfigurations() {
        if (this.metricsConfigurations == null) {
            this.metricsConfigurations = new MetricsConfigurationsImpl(clientObject.getMetricsConfigurations(), this);
        }
        return metricsConfigurations;
    }

    /**
     * Gets the resource collection API of Consoles. It manages Console.
     *
     * @return Resource collection API of Consoles.
     */
    public Consoles consoles() {
        if (this.consoles == null) {
            this.consoles = new ConsolesImpl(clientObject.getConsoles(), this);
        }
        return consoles;
    }

    /**
     * @return Wrapped service client NetworkCloud providing direct access to the underlying auto-generated API
     *     implementation, based on Azure REST API.
     */
    public NetworkCloud serviceClient() {
        return this.clientObject;
    }
}