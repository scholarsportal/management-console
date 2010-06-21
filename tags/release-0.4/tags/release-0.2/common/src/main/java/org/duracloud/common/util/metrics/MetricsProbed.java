
package org.duracloud.common.util.metrics;

/**
 * This interface allows implementations to be injected with a MetricsTable in
 * which to collect Metrics.
 *
 * @author Andrew Woods
 */
public interface MetricsProbed {

    public void setMetricsTable(MetricsTable metricsTable);

}
