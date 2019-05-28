package bitflow4j.steps.database;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.logging.Logger;

public class WriteToElasticsearchDB extends AbstractPipelineStep {

    protected static final Logger logger = Logger.getLogger(WriteToElasticsearchDB.class.getName());

    private final ElasticsearchUtil elasticsearchUtil;

    /**
     * @param hostPorts          Comma-separated list of host:port pairs, e.g.: host1:port1, host2:port2
     * @param indexName          Index name of the respective index in the Elasticsearch-Database
     * @param identifierKey      Name of key-tag which is saved for each sample and used to identify/filter the entry in
     *                           the database (the same name will be applied in the DB entry as the property name)
     * @param identifierTemplate Used template to fill the named property with meaningful content (Tag-templates
     *                           should be used here)
     */
    public WriteToElasticsearchDB(String hostPorts, String indexName, String identifierKey, String identifierTemplate) throws IOException {
        elasticsearchUtil = new ElasticsearchUtil(hostPorts, indexName, identifierKey, identifierTemplate);
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        elasticsearchUtil.write(sample);
        output.writeSample(sample);
    }

    @Override
    public String toString() {
        return elasticsearchUtil.toString();
    }
}
