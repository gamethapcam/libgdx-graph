package com.gempukku.libgdx.graph.plugin.maps.producer;

import com.gempukku.libgdx.graph.data.NodeConfigurationImpl;
import com.gempukku.libgdx.graph.pipeline.PipelineFieldType;
import com.gempukku.libgdx.graph.pipeline.producer.node.GraphNodeInputImpl;
import com.gempukku.libgdx.graph.pipeline.producer.node.GraphNodeOutputImpl;

import static com.gempukku.libgdx.graph.pipeline.PipelineFieldType.Camera;
import static com.gempukku.libgdx.graph.pipeline.PipelineFieldType.RenderPipeline;

public class MapsRendererPipelineNodeConfiguration extends NodeConfigurationImpl<PipelineFieldType> {
    public MapsRendererPipelineNodeConfiguration() {
        super("MapRendererAll", "Map renderer", "Maps");
        addNodeInput(
                new GraphNodeInputImpl<PipelineFieldType>("enabled", "Enabled", false, PipelineFieldType.Boolean));
        addNodeInput(
                new GraphNodeInputImpl<PipelineFieldType>("camera", "Camera", true, Camera));
        addNodeInput(
                new GraphNodeInputImpl<PipelineFieldType>("input", "Input", true, true, RenderPipeline));
        addNodeOutput(
                new GraphNodeOutputImpl<PipelineFieldType>("output", "Output", true, RenderPipeline));
    }
}
