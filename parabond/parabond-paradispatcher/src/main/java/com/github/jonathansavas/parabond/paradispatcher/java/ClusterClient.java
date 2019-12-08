package com.github.jonathansavas.parabond.paradispatcher.java;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import java.io.IOException;

public class ClusterClient {
    private ApiClient client;
    private CoreV1Api api;

    public ClusterClient() throws IOException {
        this.client = ClientBuilder.cluster().build();
        Configuration.setDefaultApiClient(this.client);
        api = new CoreV1Api();
    }

    public int getNumPodsAllNamespaces(String labelSelector) throws ApiException {
        V1PodList pods = api.listPodForAllNamespaces(null, null, null, labelSelector, null, null, null, null, null);
        return pods.getItems().size();
    }
}
