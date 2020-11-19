package com.gempukku.libgdx.graph.shader.node.provided;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gempukku.libgdx.graph.shader.GraphShader;
import com.gempukku.libgdx.graph.shader.GraphShaderContext;
import com.gempukku.libgdx.graph.shader.ShaderFieldType;
import com.gempukku.libgdx.graph.shader.UniformSetters;
import com.gempukku.libgdx.graph.shader.builder.CommonShaderBuilder;
import com.gempukku.libgdx.graph.shader.config.provided.ViewportSizeShaderNodeConfiguration;
import com.gempukku.libgdx.graph.shader.node.ConfigurationCommonShaderNodeBuilder;
import com.gempukku.libgdx.graph.shader.node.DefaultFieldOutput;

public class ViewportSizeShaderNodeBuilder extends ConfigurationCommonShaderNodeBuilder {
    public ViewportSizeShaderNodeBuilder() {
        super(new ViewportSizeShaderNodeConfiguration());
    }

    @Override
    protected ObjectMap<String, ? extends FieldOutput> buildCommonNode(boolean designTime, String nodeId, JsonValue data, ObjectMap<String, FieldOutput> inputs, ObjectSet<String> producedOutputs, CommonShaderBuilder commonShaderBuilder, GraphShaderContext graphShaderContext, GraphShader graphShader) {
        commonShaderBuilder.addUniformVariable("u_viewportSize", "vec2", true, UniformSetters.viewportSize);
        ObjectMap<String, DefaultFieldOutput> result = new ObjectMap<>();
        if (producedOutputs.contains("size")) {
            result.put("size", new DefaultFieldOutput(ShaderFieldType.Vector2, "u_viewportSize"));
        }
        if (producedOutputs.contains("x")) {
            result.put("x", new DefaultFieldOutput(ShaderFieldType.Float, "u_viewportSize.x"));
        }
        if (producedOutputs.contains("y")) {
            result.put("y", new DefaultFieldOutput(ShaderFieldType.Float, "u_viewportSize.y"));
        }
        return result;
    }
}
