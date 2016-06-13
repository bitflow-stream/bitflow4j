package metrics.main.analysis;

import metrics.Header;
import metrics.io.fork.AbstractSampleSplitter;
import metrics.main.misc.ParameterHash;

import java.util.*;

/**
 * Created by anton on 4/28/16.
 * <p>
 * Splits incoming metrics collected on a physical host into multiple output streams.
 * The metrics actually belonging to the physical host go into the default output.
 * Metrics that are related to a virtual machine will go into separate outputs.
 */
abstract class PhysicalHostSampleSplitter extends AbstractSampleSplitter {

    private static final String LIBVIRT_PREFIX = "libvirt/";
    private static final String OVSDB_PREFIX = "ovsdb/";

    // This must filled (configured) before using this algorithm
    // The values must not contain empty Strings (used below to encode the "default output")
    final Map<String, String> libvirt_ids = new HashMap<>(); // Key: libvirt domain name
    final Map<String, String> ovsdb_ids = new HashMap<>(); // Key: ovsdb interface name

    @Override
    protected void fillHeaders(Header inputHeader,
                               Map<String, Header> vmHeaders, Map<String, List<Integer>> vmMetrics) {
        Set<String> warnedIds = new HashSet<>(); // Avoid duplicate warnings...

        for (int i = 0; i < inputHeader.header.length; i++) {
            String field = inputHeader.header[i];
            String hostName;
            if (field.startsWith(LIBVIRT_PREFIX)) {
                String libvirtDomain = extractId(field, LIBVIRT_PREFIX);
                hostName = libvirt_ids.get(libvirtDomain);
                if (hostName == null) {
                    if (!warnedIds.contains(libvirtDomain)) {
                        System.err.println("Warning: Unknown libvirt domain, ignoring metrics: " + libvirtDomain);
                        warnedIds.add(libvirtDomain);
                    }
                    continue;
                }
            } else if (field.startsWith(OVSDB_PREFIX)) {
                String interfaceName = extractId(field, OVSDB_PREFIX);
                hostName = ovsdb_ids.get(interfaceName);
                if (hostName == null) {
                    if (!warnedIds.contains(interfaceName)) {
                        System.err.println("Warning: Unknown OVSDB interface, ignoring metrics: " + interfaceName);
                        warnedIds.add(interfaceName);
                    }
                    continue;
                }
            } else {
                // Metric belongs to the physical host
                hostName = "";
            }

            List<Integer> metricIndices = vmMetrics.get(hostName);
            if (metricIndices == null) {
                metricIndices = new ArrayList<>();
                vmMetrics.put(hostName, metricIndices);
            }
            metricIndices.add(i);
        }

        for (Map.Entry<String, List<Integer>> metrics : vmMetrics.entrySet()) {
            List<Integer> indices = metrics.getValue();
            String fields[] = new String[indices.size()];
            for (int i = 0; i < fields.length; i++) {
                fields[i] = inputHeader.header[indices.get(i)];
            }
            Header header = new Header(fields, inputHeader);
            vmHeaders.put(metrics.getKey(), header);
        }
    }

    private static String extractId(String field, String prefix) {
        String sub = field.substring(prefix.length());
        return sub.substring(0, sub.indexOf('/'));
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeInt(libvirt_ids.size());
        hash.writeInt(ovsdb_ids.size());
        for (String name : libvirt_ids.keySet()) {
            hash.writeChars(name);
            hash.writeChars(libvirt_ids.get(name));
        }
        for (String name : ovsdb_ids.keySet()) {
            hash.writeChars(name);
            hash.writeChars(ovsdb_ids.get(name));
        }
    }

    @Override
    public String toString() {
        return "physical-host-sample-splitter";
    }

}
