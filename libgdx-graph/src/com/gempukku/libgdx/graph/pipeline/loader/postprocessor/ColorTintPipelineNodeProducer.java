package com.gempukku.libgdx.graph.pipeline.loader.postprocessor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.graph.pipeline.PipelineFieldType;
import com.gempukku.libgdx.graph.pipeline.RenderPipeline;
import com.gempukku.libgdx.graph.pipeline.RenderPipelineBuffer;
import com.gempukku.libgdx.graph.pipeline.config.postprocessor.ColorTintPipelineNodeConfiguration;
import com.gempukku.libgdx.graph.pipeline.loader.BooleanFieldOutput;
import com.gempukku.libgdx.graph.pipeline.loader.FloatFieldOutput;
import com.gempukku.libgdx.graph.pipeline.loader.PipelineRenderingContext;
import com.gempukku.libgdx.graph.pipeline.loader.node.OncePerFrameJobPipelineNode;
import com.gempukku.libgdx.graph.pipeline.loader.node.PipelineNode;
import com.gempukku.libgdx.graph.pipeline.loader.node.PipelineNodeProducerImpl;
import com.gempukku.libgdx.graph.pipeline.loader.node.PipelineRequirements;

public class ColorTintPipelineNodeProducer extends PipelineNodeProducerImpl {
    public ColorTintPipelineNodeProducer() {
        super(new ColorTintPipelineNodeConfiguration());
    }

    @Override
    public PipelineNode createNodeForSingleInputs(JsonValue data, ObjectMap<String, PipelineNode.FieldOutput<?>> inputFields) {
        final ShapeRenderer shapeRenderer = new ShapeRenderer();

        PipelineNode.FieldOutput<Boolean> processorEnabled = (PipelineNode.FieldOutput<Boolean>) inputFields.get("enabled");
        PipelineNode.FieldOutput<Color> color = (PipelineNode.FieldOutput<Color>) inputFields.get("color");
        PipelineNode.FieldOutput<Float> strength = (PipelineNode.FieldOutput<Float>) inputFields.get("strength");

        if (processorEnabled == null)
            processorEnabled = new BooleanFieldOutput(true);
        if (color == null)
            color = new PipelineNode.FieldOutput<Color>() {
                @Override
                public PipelineFieldType getPropertyType() {
                    return PipelineFieldType.Color;
                }

                @Override
                public Color getValue(PipelineRenderingContext context, PipelineRequirements pipelineRequirements) {
                    return Color.BLACK;
                }
            };
        if (strength == null)
            strength = new FloatFieldOutput(0f);

        final PipelineNode.FieldOutput<RenderPipeline> renderPipelineInput = (PipelineNode.FieldOutput<RenderPipeline>) inputFields.get("input");

        final PipelineNode.FieldOutput<Float> finalStrength = strength;
        final PipelineNode.FieldOutput<Color> finalColor = color;
        final PipelineNode.FieldOutput<Boolean> finalProcessorEnabled = processorEnabled;
        return new OncePerFrameJobPipelineNode(configuration, inputFields) {
            @Override
            protected void executeJob(PipelineRenderingContext pipelineRenderingContext, PipelineRequirements pipelineRequirements, ObjectMap<String, ? extends OutputValue> outputValues) {
                RenderPipeline renderPipeline = renderPipelineInput.getValue(pipelineRenderingContext, pipelineRequirements);

                boolean enabled = finalProcessorEnabled.getValue(pipelineRenderingContext, null);
                float strengthValue = finalStrength.getValue(pipelineRenderingContext, null);
                if (enabled && strengthValue > 0) {
                    Color colorValue = finalColor.getValue(pipelineRenderingContext, null);

                    RenderPipelineBuffer currentBuffer = renderPipeline.getDefaultBuffer();
                    currentBuffer.beginColor();

                    Gdx.gl.glEnable(GL20.GL_BLEND);
                    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(colorValue.r, colorValue.g, colorValue.b, colorValue.a * strengthValue);
                    shapeRenderer.rect(0, 0, currentBuffer.getWidth(), currentBuffer.getHeight());
                    shapeRenderer.end();
                    currentBuffer.endColor();
                }

                OutputValue<RenderPipeline> output = outputValues.get("output");
                if (output != null)
                    output.setValue(renderPipeline);
            }

            @Override
            public void dispose() {
                shapeRenderer.dispose();
            }
        };
    }
}
