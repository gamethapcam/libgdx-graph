package com.gempukku.libgdx.graph.shader.config.model.attribute;

import com.gempukku.libgdx.graph.data.NodeConfigurationImpl;
import com.gempukku.libgdx.graph.pipeline.loader.node.GraphNodeOutputImpl;
import com.gempukku.libgdx.graph.shader.ShaderFieldType;

public class AttributeColorShaderNodeConfiguration extends NodeConfigurationImpl<ShaderFieldType> {
    public AttributeColorShaderNodeConfiguration() {
        super("AttributeColor", "Color attribute", "Attribute");
        addNodeOutput(
                new GraphNodeOutputImpl<ShaderFieldType>("color", "Color", ShaderFieldType.Vector4));
    }
}