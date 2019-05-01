package com.uber.infer.task

class Infer extends InferAnalyzeCommand {

    /**
     * biabduction: run the bi-abduction based checker only, in particular to check for memory errors
     *
     * infer: alias for biabduction
     *
     * checkers: run the default checkers,
     *            including the bi-abduction based checker for memory errors (default)
     * @return
     */
    @Override
    protected String getAnalyzerParameter() {
        return "-a checkers"
    }
}
