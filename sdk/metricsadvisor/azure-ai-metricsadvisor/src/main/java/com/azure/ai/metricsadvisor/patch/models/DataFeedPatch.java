// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


package com.azure.ai.metricsadvisor.patch.models;

import com.azure.ai.metricsadvisor.implementation.util.Option;
import com.azure.ai.metricsadvisor.models.DataFeedAccessMode;
import com.azure.ai.metricsadvisor.models.DataFeedAutoRollUpMethod;
import com.azure.ai.metricsadvisor.models.DataFeedMissingDataPointFillType;
import com.azure.ai.metricsadvisor.models.DataFeedRollupType;
import com.azure.ai.metricsadvisor.models.DataFeedStatus;
import com.azure.ai.metricsadvisor.models.DatasourceAuthenticationType;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.OffsetDateTime;
import java.util.List;

/** The DataFeedDetailPatch model. */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dataSourceType",
        defaultImpl = DataFeedPatch.class)
@JsonTypeName("DataFeedDetailPatch")
@JsonSubTypes({
    @JsonSubTypes.Type(name = "AzureApplicationInsights", value = AzureApplicationInsightsDataFeedPatch.class),
    @JsonSubTypes.Type(name = "AzureBlob", value = AzureBlobDataFeedPatch.class),
    @JsonSubTypes.Type(name = "AzureCosmosDB", value = AzureCosmosDBDataFeedPatch.class),
    @JsonSubTypes.Type(name = "AzureDataExplorer", value = AzureDataExplorerDataFeedPatch.class),
    @JsonSubTypes.Type(name = "AzureDataLakeStorageGen2", value = AzureDataLakeStorageGen2DataFeedPatch.class),
    @JsonSubTypes.Type(name = "AzureEventHubs", value = AzureEventHubsDataFeedPatch.class),
    @JsonSubTypes.Type(name = "AzureLogAnalytics", value = AzureLogAnalyticsDataFeedPatch.class),
    @JsonSubTypes.Type(name = "AzureTable", value = AzureTableDataFeedPatch.class),
    @JsonSubTypes.Type(name = "InfluxDB", value = InfluxDbDataFeedPatch.class),
    @JsonSubTypes.Type(name = "MySql", value = MySqlDataFeedPatch.class),
    @JsonSubTypes.Type(name = "PostgreSql", value = PostgreSqlDataFeedPatch.class),
    @JsonSubTypes.Type(name = "SqlServer", value = SqlServerDataFeedPatch.class),
    @JsonSubTypes.Type(name = "MongoDB", value = MongoDbDataFeedPatch.class)
})
@Fluent
public class DataFeedPatch {
    /*
     * data feed name
     */
    @JsonProperty(value = "dataFeedName")
    private Option<String> dataFeedName;

    /*
     * data feed description
     */
    @JsonProperty(value = "dataFeedDescription")
    private Option<String> dataFeedDescription;

    /*
     * user-defined timestamp column. if timestampColumn is null, start time of
     * every time slice will be used as default value.
     */
    @JsonProperty(value = "timestampColumn")
    private Option<String> timestampColumn;

    /*
     * ingestion start time
     */
    @JsonProperty(value = "dataStartFrom")
    private Option<OffsetDateTime> dataStartFrom;

    /*
     * the time that the beginning of data ingestion task will delay for every
     * data slice according to this offset.
     */
    @JsonProperty(value = "startOffsetInSeconds")
    private Option<Long> startOffsetInSeconds;

    /*
     * the max concurrency of data ingestion queries against user data source.
     * 0 means no limitation.
     */
    @JsonProperty(value = "maxConcurrency")
    private Option<Integer> maxConcurrency;

    /*
     * the min retry interval for failed data ingestion tasks.
     */
    @JsonProperty(value = "minRetryIntervalInSeconds")
    private Option<Long> minRetryIntervalInSeconds;

    /*
     * stop retry data ingestion after the data slice first schedule time in
     * seconds.
     */
    @JsonProperty(value = "stopRetryAfterInSeconds")
    private Option<Long> stopRetryAfterInSeconds;

    /*
     * mark if the data feed need rollup
     */
    @JsonProperty(value = "needRollup")
    private Option<DataFeedRollupType> needRollup;

    /*
     * roll up method
     */
    @JsonProperty(value = "rollUpMethod")
    private Option<DataFeedAutoRollUpMethod> rollUpMethod;

    /*
     * roll up columns
     */
    @JsonProperty(value = "rollUpColumns")
    private Option<List<String>> rollUpColumns;

    /*
     * the identification value for the row of calculated all-up value.
     */
    @JsonProperty(value = "allUpIdentification")
    private Option<String> allUpIdentification;

    /*
     * the type of fill missing point for anomaly detection
     */
    @JsonProperty(value = "fillMissingPointType")
    private Option<DataFeedMissingDataPointFillType> fillMissingPointType;

    /*
     * the value of fill missing point for anomaly detection
     */
    @JsonProperty(value = "fillMissingPointValue")
    private Option<Double> fillMissingPointValue;

    /*
     * data feed access mode, default is Private
     */
    @JsonProperty(value = "viewMode")
    private Option<DataFeedAccessMode> viewMode;

    /*
     * data feed administrator
     */
    @JsonProperty(value = "admins")
    private Option<List<String>> admins;

    /*
     * data feed viewer
     */
    @JsonProperty(value = "viewers")
    private Option<List<String>> viewers;

    /*
     * data feed status
     */
    @JsonProperty(value = "status")
    private Option<DataFeedStatus> status;

    /*
     * action link for alert
     */
    @JsonProperty(value = "actionLinkTemplate")
    private Option<String> actionLinkTemplate;

    /*
     * authentication type for corresponding data source
     */
    @JsonProperty(value = "authenticationType")
    private Option<DatasourceAuthenticationType> authenticationType;

    /*
     * The credential entity id
     */
    @JsonProperty(value = "credentialId")
    private Option<String> credentialId;

    /**
     * Set the dataFeedName property: data feed name.
     *
     * @param dataFeedName the dataFeedName value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setName(String dataFeedName) {
        if (dataFeedName == null) {
            this.dataFeedName = Option.empty();
        } else {
            this.dataFeedName = Option.of(dataFeedName);
        }
        return this;
    }

    /**
     * Set the dataFeedDescription property: data feed description.
     *
     * @param dataFeedDescription the dataFeedDescription value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setDescription(String dataFeedDescription) {
        if (dataFeedDescription == null) {
            this.dataFeedDescription = Option.empty();
        } else {
            this.dataFeedDescription = Option.of(dataFeedDescription);
        }
        return this;
    }

    /**
     * Set the timestampColumn property: user-defined timestamp column. if timestampColumn is null, start time of every
     * time slice will be used as default value.
     *
     * @param timestampColumn the timestampColumn value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setTimestampColumn(String timestampColumn) {
        if (timestampColumn == null) {
            this.timestampColumn = Option.empty();
        } else {
            this.timestampColumn = Option.of(timestampColumn);
        }
        return this;
    }


    /**
     * Set the dataStartFrom property: ingestion start time.
     *
     * @param dataStartFrom the dataStartFrom value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setDataStartFrom(OffsetDateTime dataStartFrom) {
        if (dataStartFrom == null) {
            this.dataStartFrom = Option.empty();
        } else {
            this.dataStartFrom = Option.of(dataStartFrom);
        }
        return this;
    }

    /**
     * Set the startOffsetInSeconds property: the time that the beginning of data ingestion task will delay for every
     * data slice according to this offset.
     *
     * @param startOffsetInSeconds the startOffsetInSeconds value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setStartOffsetInSeconds(Long startOffsetInSeconds) {
        if (startOffsetInSeconds == null) {
            this.startOffsetInSeconds = Option.empty();
        } else {
            this.startOffsetInSeconds = Option.of(startOffsetInSeconds);
        }
        return this;
    }

    /**
     * Set the maxConcurrency property: the max concurrency of data ingestion queries against user data source. 0 means
     * no limitation.
     *
     * @param maxConcurrency the maxConcurrency value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setMaxConcurrency(Integer maxConcurrency) {
        if (maxConcurrency == null) {
            this.maxConcurrency = Option.empty();
        } else {
            this.maxConcurrency = Option.of(maxConcurrency);
        }
        return this;
    }

    /**
     * Set the minRetryIntervalInSeconds property: the min retry interval for failed data ingestion tasks.
     *
     * @param minRetryIntervalInSeconds the minRetryIntervalInSeconds value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setMinRetryIntervalInSeconds(Long minRetryIntervalInSeconds) {
        if (minRetryIntervalInSeconds == null) {
            this.minRetryIntervalInSeconds = Option.empty();
        } else {
            this.minRetryIntervalInSeconds = Option.of(minRetryIntervalInSeconds);
        }
        return this;
    }

    /**
     * Set the stopRetryAfterInSeconds property: stop retry data ingestion after the data slice first schedule time in
     * seconds.
     *
     * @param stopRetryAfterInSeconds the stopRetryAfterInSeconds value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setStopRetryAfterInSeconds(Long stopRetryAfterInSeconds) {
        if (stopRetryAfterInSeconds == null) {
            this.stopRetryAfterInSeconds = Option.empty();
        } else {
            this.stopRetryAfterInSeconds = Option.of(stopRetryAfterInSeconds);
        }
        return this;
    }

    /**
     * Set the needRollup property: mark if the data feed need rollup.
     *
     * @param needRollup the needRollup value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setNeedRollup(DataFeedRollupType needRollup) {
        if (needRollup == null) {
            this.needRollup = Option.empty();
        } else {
            this.needRollup = Option.of(needRollup);
        }
        return this;
    }

    /**
     * Set the rollUpMethod property: roll up method.
     *
     * @param rollUpMethod the rollUpMethod value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setRollUpMethod(DataFeedAutoRollUpMethod rollUpMethod) {
        if (rollUpMethod == null) {
            this.rollUpMethod = Option.empty();
        } else {
            this.rollUpMethod = Option.of(rollUpMethod);
        }
        return this;
    }

    /**
     * Set the rollUpColumns property: roll up columns.
     *
     * @param rollUpColumns the rollUpColumns value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setRollUpColumns(List<String> rollUpColumns) {
        if (rollUpColumns == null) {
            this.rollUpColumns = Option.empty();
        } else {
            this.rollUpColumns = Option.of(rollUpColumns);
        }
        return this;
    }

    /**
     * Set the allUpIdentification property: the identification value for the row of calculated all-up value.
     *
     * @param allUpIdentification the allUpIdentification value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setAllUpIdentification(String allUpIdentification) {
        if (allUpIdentification == null) {
            this.allUpIdentification = Option.empty();
        } else {
            this.allUpIdentification = Option.of(allUpIdentification);
        }
        return this;
    }

    /**
     * Set the fillMissingPointType property: the type of fill missing point for anomaly detection.
     *
     * @param fillMissingPointType the fillMissingPointType value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setFillMissingPointType(DataFeedMissingDataPointFillType fillMissingPointType) {
        if (fillMissingPointType == null) {
            this.fillMissingPointType = Option.empty();
        } else {
            this.fillMissingPointType = Option.of(fillMissingPointType);
        }
        return this;
    }

    /**
     * Set the fillMissingPointValue property: the value of fill missing point for anomaly detection.
     *
     * @param fillMissingPointValue the fillMissingPointValue value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setFillMissingPointValue(Double fillMissingPointValue) {
        if (fillMissingPointValue == null) {
            this.fillMissingPointValue = Option.empty();
        } else {
            this.fillMissingPointValue = Option.of(fillMissingPointValue);
        }
        return this;
    }

    /**
     * Set the viewMode property: data feed access mode, default is Private.
     *
     * @param viewMode the viewMode value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setAccessMode(DataFeedAccessMode viewMode) {
        if (viewMode == null) {
            this.viewMode = Option.empty();
        } else {
            this.viewMode = Option.of(viewMode);
        }
        return this;
    }

    /**
     * Set the admins property: data feed administrator.
     *
     * @param admins the admins value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setAdmins(List<String> admins) {
        if (admins == null) {
            this.admins = Option.empty();
        } else {
            this.admins = Option.of(admins);
        }
        return this;
    }

    /**
     * Set the viewers property: data feed viewer.
     *
     * @param viewers the viewers value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setViewers(List<String> viewers) {
        if (viewers == null) {
            this.viewers = Option.empty();
        } else {
            this.viewers = Option.of(viewers);
        }
        return this;
    }

    /**
     * Set the status property: data feed status.
     *
     * @param status the status value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setStatus(DataFeedStatus status) {
        if (status == null) {
            this.status = Option.empty();
        } else {
            this.status = Option.of(status);
        }
        return this;
    }

    /**
     * Set the actionLinkTemplate property: action link for alert.
     *
     * @param actionLinkTemplate the actionLinkTemplate value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setActionLinkTemplate(String actionLinkTemplate) {
        if (actionLinkTemplate == null) {
            this.actionLinkTemplate = Option.empty();
        } else {
            this.actionLinkTemplate = Option.of(actionLinkTemplate);
        }
        return this;
    }

    /**
     * Set the authenticationType property: authentication type for corresponding data source.
     *
     * @param authenticationType the authenticationType value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setAuthenticationType(DatasourceAuthenticationType authenticationType) {
        if (authenticationType == null) {
            this.authenticationType = Option.empty();
        } else {
            this.authenticationType = Option.of(authenticationType);
        }
        return this;
    }

    /**
     * Set the credentialId property: The credential entity id.
     *
     * @param credentialId the credentialId value to set.
     * @return the DataFeedDetailPatch object itself.
     */
    public DataFeedPatch setCredentialId(String credentialId) {
        if (credentialId == null) {
            this.credentialId = Option.empty();
        } else {
            this.credentialId = Option.of(credentialId);
        }
        return this;
    }
}
