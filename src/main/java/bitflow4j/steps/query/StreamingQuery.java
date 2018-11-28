package bitflow4j.steps.query;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.steps.query.exceptions.IllegalQuerySemantics;
import bitflow4j.steps.query.exceptions.IllegalQuerySyntax;
import bitflow4j.steps.query.exceptions.IllegalStreamingQueryException;
import bitflow4j.steps.query.exceptions.MissingMetricException;
import bitflow4j.steps.query.generated.BitflowQueryLexer;
import bitflow4j.steps.query.generated.BitflowQueryParser;
import bitflow4j.steps.query.visitors.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.*;

/**
 * Main class for processing samples. Gets a query string like "Select *" as input.
 * Inherit from AbstractAlgorithm to get Bitflow interfaces.
 * The method writeSample(Sample s) is used to process Samples.
 */
public class StreamingQuery extends AbstractPipelineStep {

    private String query;
    private ParseTree tree;

    private BooleanVisitor booleanVisitor; // visitor for evaluation of the where part
    private CalculateValuesVisitor valueVisitor; // visitor to calculate values in the output sample
    private HeaderBuilderVisitor headerBuilderVisitor; // visitor that calculate the new header in outputSample
    private CalculateTimeVisitor timeVisitor; // visitor for calculate time in window time mode

    private ArrayList<Sample> sampleList = new ArrayList<>(); // holds all Sample in Window
    private ArrayList<String> neededMetrics;

    private String[] lastHeader;
    private Header lastOutputHeader;

    // when true an output is calculated when where is false for actual sample, need samples in window for doing this
    private boolean calculateOutputIfWhereIsFalse;

    private boolean windowIsPartOfQuery = false;
    private boolean havingIsPartOfQuery = false;

    // variables for window
    private WindowMode windowMode; // None, Value, Time or All
    private int maxSamplesInList; // used only in window value mode
    private Long maximalMilliSecondsBetweenSamples; // used only in window time mode
    private boolean windowFileMode = false; // when true use time of actual sample instead of actual time, will be true when FILE is following Window in query string

    // variables for group by
    private boolean groupByMode = false; // will be true when Group by is part of query string
    private Map<String, ArrayList<Sample>> groups; // used to store the different groups
    private ArrayList<String> groupByTags; // tags that are part of query like host, IP...
    private ArrayList<String> tagValues; // list of the different tags in sample
    private int groupCounter; // actual group that is calculated, used from functions like Sum() when asking for sampleList for actual group


    public StreamingQuery(String query) throws IllegalStreamingQueryException {
        calculateOutputIfWhereIsFalse = true; // true = default
        initQuery(query);
    }

    public StreamingQuery(String query, boolean outputWhenWhereIsFalse) throws IllegalStreamingQueryException {
        setCalculateOutputIfWhereIsFalse(outputWhenWhereIsFalse);
        initQuery(query);
    }

    /**
     * Initialise the object.
     */
    private void initQuery(String query) throws IllegalStreamingQueryException {
        this.query = query;
        CharStream stream = CharStreams.fromString(query);
        BitflowQueryLexer lexer = new BitflowQueryLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BitflowQueryParser parser = new BitflowQueryParser(tokens);

        tree = parser.parse();
        int parsingErrors = parser.getNumberOfSyntaxErrors();
        if (parsingErrors != 0) {
            throw new IllegalQuerySyntax(query);
        }

        if (!semanticOfQueryIsOk(query))
            throw new IllegalQuerySemantics(query);

        booleanVisitor = new BooleanVisitor();
        // visitor to calculate the needed metrics
        CalculateNeededMetricsVisitor calculateNeededMetricsVisitor = new CalculateNeededMetricsVisitor();
        headerBuilderVisitor = new HeaderBuilderVisitor();
        valueVisitor = new CalculateValuesVisitor(this);
        timeVisitor = new CalculateTimeVisitor();

        neededMetrics = calculateNeededMetricsVisitor.visit(tree);

        checkForWindowAndSetMode();
        checkForGroupByAndSetTags();

        // check if Window is part of query
        windowIsPartOfQuery = false;
        if (tree.getChildCount() > 1) {
            if (tree.getChild(1).getText().toLowerCase().startsWith(" where "))
                windowIsPartOfQuery = true;
        }

        // check if Having is part of query
        havingIsPartOfQuery = tree.getChild(tree.getChildCount() - 2).getText().toLowerCase().startsWith(" having ");
    }

    /**
     * Checks if group by is part of query and store the tags if so.
     */
    private void checkForGroupByAndSetTags() {
        groupByMode = query.toLowerCase().contains("group by");
        if (groupByMode) {
            // visitor vor calculate tags for group by
            GroupByVisitor groupVisitor = new GroupByVisitor();
            groupByTags = groupVisitor.visitFunction(tree);
        }
    }

    /**
     * This methods gets a input sample and calculate one or more output samples.
     */
    public void writeSample(Sample s) throws IOException {
        String[] tmpLastHeader = lastHeader; // store old lastHeader in temp variable because we overwrite it soon

        if (lastHeader == null) {
            boolean headerIsOk = isHeaderOk(s.getHeader().header);
            if (headerIsOk) {
                lastHeader = s.getHeader().header;
            } else {
                throw new MissingMetricException(Arrays.toString(s.getHeader().header));
            }

        } else if (!Arrays.equals(lastHeader, s.getHeader().header)) {
            boolean headerIsOk = isHeaderOk(s.getHeader().header);
            if (headerIsOk) {
                lastHeader = s.getHeader().header;
            } else {
                throw new MissingMetricException(Arrays.toString(s.getHeader().header));
            }
        }

        boolean wherePartIsTrueOrNotPresent;
        if (windowIsPartOfQuery) {
            // calculate if the where part of the actual sample is true or false
            ReturnHelperWherePart r = booleanVisitor.visitWithSample(tree, s);
            wherePartIsTrueOrNotPresent = r.getBool();
        } else {
            // no where in query
            wherePartIsTrueOrNotPresent = true;
        }

        // where part of newest sample is true or list is not empty and we want
        // outputs even is where part is false
        if (wherePartIsTrueOrNotPresent || (sampleList.size() > 0 && getCalculateOutputIfWhereIsFalse() && !(windowMode == WindowMode.None))) {

            if (wherePartIsTrueOrNotPresent) {
                sampleList.add(s);
            }
            deleteSamplesFromListIfNecessary();

            if (lastOutputHeader == null) {
                lastOutputHeader = new Header(headerBuilderVisitor.visitWithSample(tree, sampleList.get(sampleList.size() - 1)));
            } else {
                if (!Arrays.equals(tmpLastHeader, sampleList.get(sampleList.size() - 1).getHeader().header)) {
                    lastOutputHeader = new Header(headerBuilderVisitor.visitWithSample(tree, sampleList.get(sampleList.size() - 1)));
                }
            }

            if (groupByMode) {
                groupOutputCalculation(lastOutputHeader);
            } else {
                Date d = sampleList.get(sampleList.size() - 1).getTimestamp();
                double[] metrics = valueVisitor.visitWithSample(tree, sampleList.get(sampleList.size() - 1));
                Sample outputSample = new Sample(lastOutputHeader, metrics, d, sampleList.get(sampleList.size() - 1).getTags());
                this.writeOutputSample(outputSample);
            }
        }
    }

    /**
     * Calculate output sample for each group.
     */
    private void groupOutputCalculation(Header h) throws IOException {
        groups = new HashMap<>();
        tagValues = new ArrayList<>();

        // calculate groups
        for (Sample s : sampleList) {
            String tagSummary = "";
            for (String tag : groupByTags) {
                if (s.hasTag(tag)) {
                    tagSummary = tagSummary + s.getTag(tag);
                } else {
                    tagSummary = "voidGroup";
                    break;
                }
            }

            if (groups.containsKey(tagSummary)) {
                groups.get(tagSummary).add(s);
            } else {
                ArrayList<Sample> list = new ArrayList<>();
                list.add(s);
                groups.put(tagSummary, list);
                tagValues.add(tagSummary);
            }

        }

        // calculate outputs for each group
        groupCounter = 0;
        for (; groupCounter < tagValues.size(); groupCounter++) {

            ArrayList<Sample> actualList = groups.get(tagValues.get(groupCounter));

            // calculate Tags
            Map<String, String> tags;
            tags = actualList.get(0).getTags();
            for (Sample s : actualList) {
                for (String tag : tags.keySet()) {
                    if (s.hasTag(tag)) {
                        if (!s.getTag(tag).equals(tags.get(tag))) {
                            tags.remove(tag);
                        }
                    } else {
                        tags.remove(tag);
                    }
                }
            }

            Date d = actualList.get(actualList.size() - 1).getTimestamp();
            double[] metrics = valueVisitor.visitWithSample(tree, actualList.get(actualList.size() - 1));
            Sample outputSample = new Sample(h, metrics, d, tags);
            this.writeOutputSample(outputSample);
        }

    }


    /**
     * Gets a sample and forward it to the output after checking the having
     * part.
     */
    private void writeOutputSample(Sample outputSample) throws IOException {

        if (havingIsPartOfQuery) {
            ReturnHelperWherePart r = booleanVisitor.visitWithSample(tree, outputSample, true);
            if (r == null || r.getBool())
                output().writeSample(outputSample);
        } else {
            //no having
            output().writeSample(outputSample);
        }
    }

    /**
     * Checks which window mode the query has including no window.
     */
    private void checkForWindowAndSetMode() {
        if (query.toLowerCase().contains("window all")) {
            windowMode = WindowMode.All;
        } else if (query.toLowerCase().matches(".*window( file)? [0-9]+ [dmsh]( having.*)?")) {
            windowMode = WindowMode.Time;
            maximalMilliSecondsBetweenSamples = timeVisitor.visit(tree) * 1000;

            if (query.toLowerCase().contains("window file")) {
                windowFileMode = true;
            }

        } else if (query.toLowerCase().matches(".*window [0-9]+.*")) {
            windowMode = WindowMode.Value;

            String lowerQuery = query.toLowerCase();
            String[] querySplit = lowerQuery.split(" ");
            for (int i = 0; i < querySplit.length; i++) {
                if (querySplit[i].equals("window")) {
                    maxSamplesInList = Integer.parseInt(querySplit[i + 1]);
                }
            }
        } else {
            windowMode = WindowMode.None;
        }
    }

    /**
     * Uses the window mode to decide if samples must be dropped from sample
     * list.
     */
    private void deleteSamplesFromListIfNecessary() {

        if (windowMode == WindowMode.None) {
            if (sampleList.size() == 2) {
                sampleList.remove(0);
            }
        } else if (windowMode == WindowMode.Value) {
            if (sampleList.size() > maxSamplesInList) {
                sampleList.remove(0);
            }
        } else if (windowMode == WindowMode.Time) {

            long actualTime = java.util.Calendar.getInstance().getTime().getTime();
            if (windowFileMode) {
                actualTime = sampleList.get(sampleList.size() - 1).getTimestamp().getTime();
            }

            boolean deleteMore = true;

            while (deleteMore) {
                if (sampleList.size() > 0) {
                    if (actualTime - sampleList.get(0).getTimestamp().getTime() > maximalMilliSecondsBetweenSamples) {
                        sampleList.remove(0);
                    } else {
                        deleteMore = false;
                    }
                } else {
                    deleteMore = false;
                }
            }

        } else if (windowMode == WindowMode.All) {
            // in this case we do not want to drop any sample
        }
    }

    /**
     * Returns the list of Samples, or in case of Group by the samples from the actual group.
     */
    public ArrayList<Sample> getSampleList() {

        if (groupByMode) {
            return groups.get(tagValues.get(groupCounter));
        } else {
            return this.sampleList;
        }

    }

    /**
     * Check if the header from a Sample fits to query
     */
    private boolean isHeaderOk(String[] headers) {

        for (String metricString : neededMetrics) {
            boolean found = false;
            for (String headerString : headers) {
                if (metricString.equals(headerString)) {
                    found = true;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the query is correct in an semantic way. A semantic error
     * for example is when Sum() is used without Window.
     */
    private boolean semanticOfQueryIsOk(String query) {

        String s = query.toLowerCase();
        if (s.contains("sum(") || query.contains("min(") || query.contains("max(") || query.contains("avg(")
                || query.contains("median(") || query.contains("count(")) {
            if (!s.contains("window")) {
                return false;
            }
        }

        // group by is only allowed with a window
        return !s.contains("group by") || s.contains("window");
    }

    public boolean getCalculateOutputIfWhereIsFalse() {
        return calculateOutputIfWhereIsFalse;
    }

    public void setCalculateOutputIfWhereIsFalse(boolean calculateOutputIfWhereIsFalse) {
        this.calculateOutputIfWhereIsFalse = calculateOutputIfWhereIsFalse;
    }
}