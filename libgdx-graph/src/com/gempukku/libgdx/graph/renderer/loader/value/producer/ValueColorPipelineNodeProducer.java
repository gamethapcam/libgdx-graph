package com.gempukku.libgdx.graph.renderer.loader.value.producer;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.libgdx.graph.renderer.config.value.ValueColorPipelineNodeConfiguration;
import com.gempukku.libgdx.graph.renderer.loader.node.PipelineNode;
import com.gempukku.libgdx.graph.renderer.loader.node.PipelineNodeProducerImpl;
import com.gempukku.libgdx.graph.renderer.loader.value.node.ValuePipelineNode;
import org.json.simple.JSONObject;

import java.util.Map;

public class ValueColorPipelineNodeProducer extends PipelineNodeProducerImpl {
    public ValueColorPipelineNodeProducer() {
        super(new ValueColorPipelineNodeConfiguration());
    }

    @Override
    public PipelineNode createNode(JSONObject data, Map<String, PipelineNode.FieldOutput<?>> inputFields) {
        return new ValuePipelineNode(configuration, "value", Color.valueOf((String) data.get("color")));
    }
}
