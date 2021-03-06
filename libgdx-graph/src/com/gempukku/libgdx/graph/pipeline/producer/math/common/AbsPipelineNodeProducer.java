package com.gempukku.libgdx.graph.pipeline.producer.math.common;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.graph.pipeline.config.math.common.AbsPipelineNodeConfiguration;
import com.gempukku.libgdx.graph.pipeline.producer.math.SingleParamMathFunctionPipelineNodeProducerImpl;
import com.gempukku.libgdx.graph.pipeline.producer.node.PipelineNode;

public class AbsPipelineNodeProducer extends SingleParamMathFunctionPipelineNodeProducerImpl {
    public AbsPipelineNodeProducer() {
        super(new AbsPipelineNodeConfiguration());
    }

    @Override
    protected float executeFunction(JsonValue data, ObjectMap<String, PipelineNode.FieldOutput<?>> inputFields, float value) {
        return Math.abs(value);
    }
}
