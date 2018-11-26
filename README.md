# bitflow4j
bitflow4j is a lightweight framework for performing data analysis on streamed timeseries data.
This library implements sending and receiving of a data over various transport channels.
The basic data entity is a bitflow4j.Sample, which consists of a timestamp, a vector of double values, and a String-map of tags.
Supported marshalling formats are CSV and a dense binary format.
Supported transport channels are files, standard I/O, and TCP.
Received or generated Samples can be modified or analysed through a Pipeline object, which sends incoming Samples through a chain of
transformation steps implementing the bitflow4j.PipelineStep interface.
