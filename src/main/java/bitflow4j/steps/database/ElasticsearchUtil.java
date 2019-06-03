package bitflow4j.steps.database;

import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author kevinstyp
 */
public class ElasticsearchUtil {

    protected static final Logger logger = Logger.getLogger(ElasticsearchUtil.class.getName());

    private final String hostPorts;
    private final String indexName;
    private final String identifierKey;
    private final String identifierTemplate;
    private final RestHighLevelClient client;
    private boolean indexCheckDone = false;

    /**
     * @param hostPorts          Comma-separated list of host:port pairs, e.g.: host1:port1, host2:port2
     * @param indexName          Index name of the respective index in the Elasticsearch-Database
     * @param identifierKey      Name of key-tag which is saved for each sample and used to identify/filter the entry in
     *                           the database (the same name will be applied in the DB entry as the property name)
     * @param identifierTemplate Used template to fill the named property with meaningful content (Tag-templates
     *                           should be used here)
     */
    public ElasticsearchUtil(String hostPorts, String indexName, String identifierKey, String identifierTemplate) throws IOException {
        this.hostPorts = hostPorts;
        this.indexName = indexName;
        this.identifierKey = identifierKey;
        this.identifierTemplate = identifierTemplate;

        List<Pair<String, Integer>> hostPortPairs = convertHostPortArgs(hostPorts);
        HttpHost[] httpHosts = new HttpHost[hostPortPairs.size()];
        for (int i = 0; i < hostPortPairs.size(); i++) {
            Pair<String, Integer> hostport = hostPortPairs.get(i);
            httpHosts[i] = new HttpHost(hostport.getLeft(), hostport.getRight(), "http");
        }
        this.client = new RestHighLevelClient(
                RestClient.builder(httpHosts));
    }

    private static List<Pair<String, Integer>> convertHostPortArgs(String tags) throws IOException {
        try {
            return Arrays.stream(tags.split(",")).map(String::trim)
                    .map(s -> {
                        String[] hostPorts = s.split(":");
                        return new Pair<>(hostPorts[0], Integer.valueOf(hostPorts[1]));
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new IOException("Failed to convert hostPorts pairs '" + tags + "' into host and ports, use syntax 'host1:port1, host2:port2': " + e);
        }
    }

    public void write(Sample sample) throws IOException {
        IndexRequest request = generateIndexRequest(sample);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        printResponse(response);
    }

    public void write(List<Sample> batch) throws IOException {
        // 'Index' in IndexRequest stands for Putting data into the DB
        BulkRequest bulkRequest = new BulkRequest();

        // For each sample of the batch
        for (Sample sample : batch) {
            IndexRequest request = generateIndexRequest(sample);
            bulkRequest.add(request);
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        handleResponse(bulkResponse);
    }

    private IndexRequest generateIndexRequest(Sample sample) throws IOException {
        if(!indexCheckDone){
            indexCheck(sample);
        }

        // 'Index' in IndexRequest stands for Putting data into the DB
        double[] metrics = sample.getMetrics();
        IndexRequest request = new IndexRequest(indexName, "_doc");
        request.id();
        //Generate the data point which represents one frequency with its amplitudes (all have the same timestamp)
        XContentBuilder data = XContentFactory.jsonBuilder();
        data.startObject()
                .field(identifierKey, sample.resolveTagTemplate(identifierTemplate))
                .field("timestamp", sample.getTimestamp().getTime());

        for (int j = 0; j < metrics.length; j++) {
            data.field(sample.getHeader().header[j], metrics[j]);
        }
        data.endObject();

        request.source(data);
        return request;
    }

    private void indexCheck(Sample sample) throws IOException {
        // Check whether index mapping exists, add the correct one if not
        GetIndexRequest requestIndex = new GetIndexRequest();
        requestIndex.indices(indexName);
        boolean indexExists = client.indices().exists(requestIndex, RequestOptions.DEFAULT);
        logger.log(Level.INFO, String.format("indexExists: %s", indexExists));
        if (!indexExists) {
            CreateIndexRequest requestCreate = new CreateIndexRequest(indexName);
            //requestCreate.

            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            // Brackets only for readability
            {
                builder.startObject("_doc");
                {
                    builder.startObject("properties");
                    {
                        // Create timestamp field as date which is saved in milliseconds
                        builder.startObject("timestamp");
                        {
                            builder.field("type", "date");
                            builder.field("index", "true");
                            builder.field("format", "epoch_millis");
                        }
                        builder.endObject();

                        // Create identifierKey field as keyword
                        builder.startObject(identifierKey);
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();

                        // Create
                        String[] header = sample.getHeader().header;
                        for (String metric : header) {
                            builder.startObject(metric);
                            {
                                builder.field("type", "float");
                            }
                            builder.endObject();
                        }
                    }
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();
            requestCreate.mapping("_doc", builder);
            try {
                CreateIndexResponse createIndexResponse = client.indices().create(requestCreate, RequestOptions.DEFAULT);
                boolean acknowledged = createIndexResponse.isAcknowledged();
                boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();
                logger.log(Level.INFO, String.format("Created index for indexName '%s' and got response '%s' and '%s'.", indexName, acknowledged, shardsAcknowledged));
            }
            catch (ElasticsearchStatusException elasticEx){
                //Can happen if multiple forks try to create this index, just ignore the message that it already was added
                if (!elasticEx.getMessage().contains("resource_already_exists_exception")){
                    throw elasticEx;
                }
            }
        }
        indexCheckDone = true;
    }

    private void printResponse(IndexResponse indexResponse) {
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            // Object was created in Database
            logger.log(Level.FINE, String.format("Document was created: %s", toString()));
        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            logger.log(Level.FINE, String.format("Rewritten existing document: %s", toString()));
        }
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            logger.log(Level.FINE, String.format("Total-Shards (%s) did not match successful Shards (%s): %s",
                    shardInfo.getTotal(), shardInfo.getSuccessful(), toString()));
        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure :
                    shardInfo.getFailures()) {
                String reason = failure.reason();
                logger.log(Level.SEVERE, String.format("Failure (%s):\n %s ", toString(), reason));
            }
        }
    }

    private void handleResponse(BulkResponse bulkResponse) {
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                if (bulkItemResponse.isFailed()) {
                    BulkItemResponse.Failure failure =
                            bulkItemResponse.getFailure();
                    logger.log(Level.WARNING, String.format("Elasticsearch write has failed with message: %s \n%s",
                            failure.getMessage(), toString()));
                }
            }
        } else {
            logger.log(Level.INFO, String.format("Elasticsearch saved Histogram: %s Elements", bulkResponse.getItems().length));
        }
    }

    @Override
    public String toString() {
        return String.format("%s: Hostports: %s , Index: %s, Identifier = Identifier-Value: %s = %s",
                this.getClass().toString(), hostPorts, indexName, identifierKey, identifierTemplate);
    }
}
