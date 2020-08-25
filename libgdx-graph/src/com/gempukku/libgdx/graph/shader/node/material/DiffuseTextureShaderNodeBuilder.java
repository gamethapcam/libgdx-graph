package com.gempukku.libgdx.graph.shader.node.material;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.WhitePixel;
import com.gempukku.libgdx.graph.shader.BasicShader;
import com.gempukku.libgdx.graph.shader.GraphShaderContext;
import com.gempukku.libgdx.graph.shader.ShaderFieldType;
import com.gempukku.libgdx.graph.shader.UniformRegistry;
import com.gempukku.libgdx.graph.shader.UniformSetters;
import com.gempukku.libgdx.graph.shader.builder.FragmentShaderBuilder;
import com.gempukku.libgdx.graph.shader.builder.VertexShaderBuilder;
import com.gempukku.libgdx.graph.shader.config.material.DiffuseTextureShaderNodeConfiguration;
import com.gempukku.libgdx.graph.shader.node.ConfigurationShaderNodeBuilder;
import com.gempukku.libgdx.graph.shader.node.DefaultFieldOutput;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class DiffuseTextureShaderNodeBuilder extends ConfigurationShaderNodeBuilder {
    public DiffuseTextureShaderNodeBuilder() {
        super(new DiffuseTextureShaderNodeConfiguration());
    }

    @Override
    public Map<String, ? extends FieldOutput> buildNode(boolean designTime, String nodeId, JSONObject data, Map<String, FieldOutput> inputs, Set<String> producedOutputs, VertexShaderBuilder vertexShaderBuilder, FragmentShaderBuilder fragmentShaderBuilder, GraphShaderContext graphShaderContext) {
        if (designTime) {
            fragmentShaderBuilder.addUniformVariable("u_diffuseTexture", "sampler2D", false,
                    new UniformRegistry.UniformSetter() {
                        @Override
                        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
                            shader.setUniform(location, WhitePixel.sharedInstance.texture);
                        }
                    });
            fragmentShaderBuilder.addUniformVariable("u_diffuseUVTransform", "vec4", false,
                    new UniformRegistry.UniformSetter() {
                        @Override
                        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
                            shader.setUniform(location, 0, 0, 1, 1);
                        }
                    });
        } else {
            fragmentShaderBuilder.addUniformVariable("u_diffuseTexture", "sampler2D", false,
                    UniformSetters.diffuseTexture);
            fragmentShaderBuilder.addUniformVariable("u_diffuseUVTransform", "vec4", false,
                    UniformSetters.diffuseUVTransform);
        }

        return Collections.singletonMap("texture", new DefaultFieldOutput(ShaderFieldType.TextureRegion, "u_diffuseUVTransform", "u_diffuseTexture"));
    }
}
