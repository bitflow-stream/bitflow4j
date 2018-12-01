# bitflow4j
bitflow4j is a lightweight framework for performing data analysis on streamed timeseries data.
This library implements sending and receiving of a data over various transport channels.
The basic data entity is a bitflow4j.Sample, which consists of a timestamp, a vector of double values, and a String-map of tags.
Supported marshalling formats are CSV and a dense binary format.
Supported transport channels are files, standard I/O, and TCP.
Received or generated Samples can be modified or analysed through a Pipeline object, which sends incoming Samples through a chain of
transformation steps implementing the bitflow4j.PipelineStep interface.

Bitflow4j can be used programmatically through the `Pipeline` class and different instances and implementations of `Source` and `PipelineStep`.
It is more convenient, however, to invoke the Bitflow4j functionality through a domain specific language called `Bitflow Script`.
This lightweight scripting language is implemented in the `bitflow-stream` sub-directory of the [antlr-grammars repository](https://github.com/bitflow-stream/antlr-grammars).
See the (README of the Bitflow Script)[https://github.com/bitflow-stream/antlr-grammars/tree/master/bitflow-script] for details regarding the script syntax.

The main entrypoint for a Bitflow Script is the class `bitflow4j.script.Main`.
It can be started with the `--help` parameter to list all command line options.
The script can be passed directly on the command line, or in a file, using the `-f` option:

```
java -cp bitflow4j.jar bitflow4j.script.Main -f bitflow-script.bf
```
```
java -cp bitflow4j.jar bitflow4j.script.Main "input.csv -> Standardize() -> output.csv"
```

When starting, the Java implementation of the Bitflow Script scans Java packages for implementations of the `bitflow4j.PipelineStep` interface,
and makes all implementing classes that meet certain conditions available as pipeline steps in the script.
In addition, all implementations of `bitflow4j.steps.fork.ScriptableDistributor` that meet the same criteria,
are available as fork steps (see the documentation of the script syntax for more info).
The conditions are:
 - The class must not be abstract
 - The class must have at least one constructor with only the following types: `String`, `int`, `long`, `double`, `float`, `boolean`, and the respective boxed types.

To see which pipeline steps and forks are available, start `bitflow4j.script.Main` with the `--capabilities` parameter.
Specify the `-v` parameter for verbose logging, which will show which classes have been scanned, but not registered for usage in the script.

Registered classes are available in the script under their **simple names**.
Currently, if multiple implementations share a name, only one of them will be available in the script.

The parameter names provided in the script must satisfy one of the constructors in the class that implements the pipeline step.
The provided parameter values will be parsed to the expected types (`double`, `boolean`, etc).
An error will be shown if parsing a parameter fails.
