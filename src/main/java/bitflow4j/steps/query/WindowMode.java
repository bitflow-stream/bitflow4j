package bitflow4j.steps.query;

/**
 * Enum for the window mode of the query. None: The window keyword is not part
 * of the query Value: The window can store up to x samples where x is part of
 * the query string Time: The window stores samples that are not older then a
 * specific time, like not more then 5 minutes All: The window stores all
 * samples where the Where part of sample is true and do not delete any sample
 * from the list.
 * 
 * Used by class StreamingQuery.
 * 
 */
public enum WindowMode {
	None, Value, Time, All
}
