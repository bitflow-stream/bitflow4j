[![Build Status](https://ci.bitflow.tream/jenkins/buildStatus/icon?job=bitflow4j%2Fmaster&build=lastBuild)](https://ci.bitflow.tream/jenkins/buildStatus/icon?job=bitflow4j%2Fmaster&build=lastBuild)

# bitflow4j
<b>bitflow4j</b> is a lightweight framework for performing data analysis on streamed timeseries data.
This library implements sending and receiving of a data over various transport channels.

## Installation

At first, you can 

```
$ git clone https://github.com/bitflow-stream/bitflow4j 
$ cd bitflow4j
$ mvn install
```
Within the `/target` directory, the file `bitflow4j-0.0.1-jar-with-dependencies.jar` is created. This file should be used for further usages.

## Bitflow script usage and examples

It is convenient, to invoke the Bitflow4j functionality through a domain specific language called `Bitflow Script`.
This lightweight scripting language is implemented in the `bitflow-stream` sub-directory of the [antlr-grammars repository](https://github.com/bitflow-stream/antlr-grammars).
See the [README of the Bitflow Script](https://github.com/bitflow-stream/antlr-grammars/tree/master/bitflow-script) for details regarding the script syntax.

The main entrypoint for a Bitflow Script is the class `bitflow4j.script.Main`.
It can be started with the `--help` parameter to list all command line options.
The script can be passed directly on the command line, or in a file, using the `-f` option:

```
java -cp bitflow4j.jar bitflow4j.script.Main -f bitflow-script.bf
```
```
java -cp bitflow4j.jar bitflow4j.script.Main "input.csv -> Standardize() -> output.csv"
```

When starting, the Java implementation of the Bitflow Script scans a list of packages for pipeline steps that will be made available in the script. The list of packages to scan defaults to `bitflow4j`, but can be modified by passing a comma-separated list of packages to the `-P` parameter.

To see which pipeline steps and forks are available, start `bitflow4j.script.Main` with the `--capabilities` parameter.
Specify the `-v` parameter for verbose logging, which will show which classes have been scanned, but not registered for usage in the script, because they do not fullfill the requirements listed below.


### Registering new Pipeline Steps

Installation via maven (add this to your pom.xml):

```
<dependency>
	<groupId>bitflow4j</groupId>
	<artifactId>bitflow4j</artifactId>
	<version>0.0.1</version>
</dependency>
```

To provide functionality inside a Bitflow Script, a class must implement one of 3 interfaces and 1 abstract class.
All implementations of these interfaces are scanned in a list of packages, as describe above.

The general conditions for successfully registering a class as a pipeline step are:

* The class must not be abstract
* The class must have at least one constructor with only the following types: `String`, `int`, `long`, `double`, `float`, `boolean`, and the respective boxed types.
* Alternatively, a constructor with a single `java.lang.Map<String, String>` constructor is supported. In this case the parameters are not parsed or validated, but simply passed to the constructor inside the Map.

Registered classes are available in the script under their **simple names**.
Currently, if multiple implementations share a name, only one of them will be available in the script. Such collisions will be logged as a warning.

The parameter names provided in the script must satisfy one of the constructors in the class that implements the pipeline step.
The provided parameter values will be parsed to the expected types (`double`, `boolean`, etc), except if the `Map<String, String>` constructor is used.
An error will be shown if parsing a parameter fails or if no fitting constructor can be found.

The following interfaces can be implemented. See the documentation of the Bitflow Script for more information.

 - `bitflow4j.PipelineStep` (abstract class): Registered subclasses of `PipelineStep` can directly be instantiated and added as a single pipeline step.
 - `bitflow4j.script.registry.ProcessingStepBuilder` (interface): Registered instances of this interface can be used to modify the created pipeline in more complex ways than adding a single pipeline step. For example, multiple connected instances of `PipelineStep` can be instantiated and added in a predefined sequence. This should only be used, when pipeline steps depend on each other, so that a constructor with only primitive parameter types is not sufficient.
 - `bitflow4j.steps.fork.ScriptableDistributor` (interface): Registered implementations of `ScriptableDistributor` can be instantiated as a single fork step inside the pipeline. They will receive all defined sub pipelines and must distribute samples according to their custom logic.
 - `bitflow4j.script.registry.ForkBuilder` (interface): If the creation of a `ScriptableDistributor` cannot be implemented in a single constructor with primitive parameter types, a `ForkBuilder` can be used to move the creation of the `ScriptableDistributor` to a dedicated builder function. However, contrary to a `PipelineBuilder`, the `Pipeline` can not be further modified by a `ForkBuilder`.

## Usage in Java code

The basic data entity is a `bitflow4j.Sample`, which consists of a timestamp, a vector of double values (double array), and a String-map of tags.
Supported marshalling formats are CSV and a dense binary format.
Supported transport channels are files, standard I/O, and TCP.
Received or generated Samples can be modified or analysed through a Pipeline object, which sends incoming Samples through a chain of
transformation steps implementing the bitflow4j.PipelineStep interface.

Bitflow4j can be used programmatically through the `Pipeline` class and different instances and implementations of `Source` and `PipelineStep`.

```
new Pipeline()
	.inputCsv("/some/path/to/data.csv")
	.HistogramAlgorithm("/path/to/visualization/output")
	.outputCsv("/path/to/output/results.csv")
	.runAndWait();
```

Also parallel pipelines can be defined via forks. Forks are also steps, which provide the possibility to define several different subpipelines. There exists different distribution techniques like `ForkTag`, `MultiplexDistributor` and `RoundRobinDistributor`.

```
ForkTag forkTag = new ForkTag("tagName");
forkTag.setSubPipelines(
	Arrays.asList(
		new Pair<>("key1", new Pipeline().step(new BatchFeatureScaler.MinMax())), 
		new Pair<>("key2", new Pipeline().step(new BatchFeatureScaler.Standardize()))));
new Pipeline()
	.inputCsv("/some/path/to/data.csv")
	.step(new Fork(forkTag))
	.outputCsv("/path/to/output/results.csv")
	.runAndWait();
```
