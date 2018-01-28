package com.uber.infer.task

class Eradicate extends InferAnalyzeCommand {

    @Override
    protected String getAnalyzerParameter() {
        return "--eradicate"
    }
}
