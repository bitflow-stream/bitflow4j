// Generated from Bitflow.g4 by ANTLR 4.7.1
package bitflow4j.script.antlr;

import bitflow4j.main.AlgorithmPipeline;
import bitflow4j.script.antlr.generated.BitflowLexer;
import bitflow4j.script.antlr.generated.BitflowListener;
import bitflow4j.script.antlr.generated.BitflowParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class BitflowScriptCompiler {

    public AlgorithmPipeline ParseScript(String script) {
        CharStream charStream = CharStreams.fromString(script);
        BitflowLexer lexer = new BitflowLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        BitflowParser parser = new BitflowParser(tokens);

        BitflowScriptListener scriptListener = new BitflowScriptListener();
        parser.script().enterRule(scriptListener);
        return null;
    }

    private class BitflowScriptListener implements BitflowListener {
        @Override
        public void enterScript(BitflowParser.ScriptContext ctx) {

        }

        @Override
        public void exitScript(BitflowParser.ScriptContext ctx) {

        }

        @Override
        public void enterOutputFork(BitflowParser.OutputForkContext ctx) {

        }

        @Override
        public void exitOutputFork(BitflowParser.OutputForkContext ctx) {

        }

        @Override
        public void enterFork(BitflowParser.ForkContext ctx) {

        }

        @Override
        public void exitFork(BitflowParser.ForkContext ctx) {

        }

        @Override
        public void enterWindow(BitflowParser.WindowContext ctx) {

        }

        @Override
        public void exitWindow(BitflowParser.WindowContext ctx) {

        }

        @Override
        public void enterMultiinput(BitflowParser.MultiinputContext ctx) {

        }

        @Override
        public void exitMultiinput(BitflowParser.MultiinputContext ctx) {

        }

        @Override
        public void enterInput(BitflowParser.InputContext ctx) {

        }

        @Override
        public void exitInput(BitflowParser.InputContext ctx) {

        }

        @Override
        public void enterOutput(BitflowParser.OutputContext ctx) {

        }

        @Override
        public void exitOutput(BitflowParser.OutputContext ctx) {

        }

        @Override
        public void enterTransform(BitflowParser.TransformContext ctx) {

        }

        @Override
        public void exitTransform(BitflowParser.TransformContext ctx) {

        }

        @Override
        public void enterSubPipeline(BitflowParser.SubPipelineContext ctx) {

        }

        @Override
        public void exitSubPipeline(BitflowParser.SubPipelineContext ctx) {

        }

        @Override
        public void enterWindowSubPipeline(BitflowParser.WindowSubPipelineContext ctx) {

        }

        @Override
        public void exitWindowSubPipeline(BitflowParser.WindowSubPipelineContext ctx) {

        }

        @Override
        public void enterPipeline(BitflowParser.PipelineContext ctx) {

        }

        @Override
        public void exitPipeline(BitflowParser.PipelineContext ctx) {

        }

        @Override
        public void enterParameter(BitflowParser.ParameterContext ctx) {

        }

        @Override
        public void exitParameter(BitflowParser.ParameterContext ctx) {

        }

        @Override
        public void enterTransformParameters(BitflowParser.TransformParametersContext ctx) {

        }

        @Override
        public void exitTransformParameters(BitflowParser.TransformParametersContext ctx) {

        }

        @Override
        public void enterName(BitflowParser.NameContext ctx) {

        }

        @Override
        public void exitName(BitflowParser.NameContext ctx) {

        }

        @Override
        public void enterPipelineName(BitflowParser.PipelineNameContext ctx) {

        }

        @Override
        public void exitPipelineName(BitflowParser.PipelineNameContext ctx) {

        }

        @Override
        public void enterSchedulingHints(BitflowParser.SchedulingHintsContext ctx) {

        }

        @Override
        public void exitSchedulingHints(BitflowParser.SchedulingHintsContext ctx) {

        }

        @Override
        public void visitTerminal(TerminalNode node) {

        }

        @Override
        public void visitErrorNode(ErrorNode node) {

        }

        @Override
        public void enterEveryRule(ParserRuleContext ctx) {

        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {

        }
    }
}